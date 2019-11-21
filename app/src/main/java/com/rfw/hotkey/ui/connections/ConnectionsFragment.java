package com.rfw.hotkey.ui.connections;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.rfw.hotkey.R;
import com.rfw.hotkey.databinding.FragmentConnectionsBinding;
import com.rfw.hotkey.net.ConnectionHeartbeat;
import com.rfw.hotkey.net.ConnectionManager;
import com.rfw.hotkey.net.connection.BluetoothConnection;
import com.rfw.hotkey.net.connection.Connection;
import com.rfw.hotkey.net.connection.WiFiConnection;
import com.rfw.hotkey.ui.connections.ConnectionsViewModel.State;
import com.rfw.hotkey.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.rfw.hotkey.util.Utils.getIntPref;

/**
 * Fragment which shows whether the device is connected and
 * info about the connected device (if present)
 *
 * @author Raheeb Hassan
 */
public class ConnectionsFragment extends Fragment {
    private static final String TAG = ConnectionsFragment.class.getCanonicalName();
    private static final int QR_CODE_READER_ACTIVITY_REQUEST = 0;

    private View contextView;
    private ConnectionsViewModel viewModel;

    private Timer heartbeatTimer = new Timer();
    private TimerTask connectionHeartbeat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        FragmentConnectionsBinding binding = FragmentConnectionsBinding.inflate(inflater, container, false);
        contextView = binding.getRoot();

        initDataBinding(binding);
        init(contextView);
        loadSharedPref();

        return contextView;
    }

    private void init(View contextView) {
        MaterialButton wiFiButton       = contextView.findViewById(R.id.wiFiButton       );
        MaterialButton bluetoothButton  = contextView.findViewById(R.id.bluetoothButton  );
        MaterialButton connectButton    = contextView.findViewById(R.id.connectButton    );

        wiFiButton      .setOnClickListener(view -> wiFiButtonAction());
        bluetoothButton .setOnClickListener(view -> scanQRCodeAction(Connection.Type.BLUETOOTH));
        connectButton   .setOnClickListener(view -> connectButtonAction());
    }

    private void initDataBinding(FragmentConnectionsBinding binding) {
        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ConnectionsViewModel.class);
//        viewModel.state.set(ConnectionManager.getInstance().isConnectionActive()? State.CONNECTED : State.NOT_CONNECTED);
//        viewModel.computerName = ConnectionManager.getInstance().getComputerName();
        binding.setViewModel(viewModel);
    }

    private void loadSharedPref() {
        // load ip address and port from shared pref
        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        String ipAddress = sharedPref.getString("ipAddress", null);
        String portText = sharedPref.getString("portText", null);
        if (ipAddress != null && portText != null) {
            viewModel.setIpAddress(ipAddress);
            viewModel.setPortStr(portText);
        }
    }

    private void saveSharedPref() {
        // save ip address and port on exit
        String ipAddress = viewModel.getIpAddress();
        String portText = viewModel.getPortStr();
        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ipAddress", ipAddress);
        editor.putString("portText", portText);
        editor.apply();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getView() != null;
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        // handle the back key press
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (viewModel.state.get() == State.IP_INPUT) {
                    viewModel.state.set(State.NOT_CONNECTED);
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        processQRCodeResult(resultCode, requestCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        connectionHeartbeat = new ConnectionHeartbeat();
        try {
            heartbeatTimer.schedule(connectionHeartbeat, new Date(), Constants.Net.HEART_BEAT_INTERVAL);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        connectionHeartbeat.cancel();
        super.onPause();
    }

    @Override
    public void onStop() {
        saveSharedPref();
        super.onStop();
    }

    private void scanQRCodeAction(Connection.Type connectionType) {
        Intent intent = QRCodeReaderActivity.getLaunchIntent(getContext(), true, false, barcode -> {
            try {
                JSONObject code = new JSONObject(new JSONTokener(barcode.rawValue));
                // check whether the qr code is valid
                return code.getString("type").equals("qrCode")
                        && code.getString("connectionType").equals(connectionType.toCamelCaseString());
            } catch (JSONException e) {
                return false;
            }
        });
        startActivityForResult(intent, QR_CODE_READER_ACTIVITY_REQUEST);
    }

    private void processQRCodeResult(int resultCode, int requestCode, @Nullable Intent data) {
        if (requestCode == QR_CODE_READER_ACTIVITY_REQUEST && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                Barcode barcode = data.getParcelableExtra(QRCodeReaderActivity.KEY_CAPTURED_BARCODE);
                assert barcode != null;
                try {
                    JSONObject code = new JSONObject(new JSONTokener(barcode.rawValue));
                    if (!code.getString("type").equals("qrCode")) throw new AssertionError();

                    if (code.getString("connectionType").equals(Connection.Type.WIFI.toCamelCaseString())) { // if WiFi
                        String ipAddress = code.getString("ipAddress");
                        int port = code.getInt("port");
                        makeWiFiConnection(ipAddress, port);
                    }
                    else if (code.getString("connectionType").equals(Connection.Type.BLUETOOTH.toCamelCaseString())) { // if bluetooth
                        String bluetoothAddress = code.getString("bluetoothAddress");
                        makeBluetoothConnection(bluetoothAddress);
                    }
                    else throw new IllegalStateException("Unknown connection type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String error = data.getStringExtra(QRCodeReaderActivity.KEY_ERROR);
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makeWiFiConnection(String ipAddress, int port) {
        State prevState = viewModel.state.get();
        viewModel.state.set(State.CONNECTING);
        long startTime = System.nanoTime();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
        int connectTimeOut = getIntPref(sharedPref, getString(R.string.settings_key_connect_timeout), Constants.Net.SOCKET_CONNECT_TIMEOUT);

        Connection connection = new WiFiConnection(ipAddress, port, connectTimeOut) {
            Activity activity = getActivity();

            @Override
            public void onConnect(boolean success, String errorMessage) {
                try {
                    // make sure the spinner is visible for at least 500 ms
                    long currentTime = (System.nanoTime() - startTime);
                    Thread.sleep(Math.max(500 - currentTime / 1000000, 0));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (success) {
                    viewModel.computerName = getComputerName();
                    viewModel.state.set(State.CONNECTED);
                } else {
                    viewModel.state.set(prevState);

                    Snackbar.make(activity.findViewById(android.R.id.content),
                            getString(R.string.connection_error_msg)
                                    + (errorMessage == null || errorMessage.isEmpty()? "" : "\n" + errorMessage),
                            Snackbar.LENGTH_SHORT).show();
                }

//                        Snackbar.make(activity.findViewById(android.R.id.content),
//                                success ? R.string.connection_success_msg : R.string.connection_error_msg,
//                                Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onDisconnect() {
                viewModel.state.set(State.NOT_CONNECTED);

                Snackbar.make(activity.findViewById(android.R.id.content),
                        R.string.connection_closed_msg,
                        Snackbar.LENGTH_SHORT).show();
            }
        };
        ConnectionManager.getInstance().makeConnection(connection);
    }

    private void makeBluetoothConnection(String bluetoothAddress) {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content),
                    getString(R.string.connection_error_msg)
                            + "\nBluetooth not enabled",
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        State prevState = viewModel.state.get();
        viewModel.state.set(State.CONNECTING);
        long startTime = System.nanoTime();

        Connection connection = new BluetoothConnection(bluetoothAddress) {
            Activity activity = getActivity();

            @Override
            public void onConnect(boolean success, String errorMessage) {
                try {
                    // make sure the spinner is visible for at least 500 ms
                    long currentTime = (System.nanoTime() - startTime);
                    Thread.sleep(Math.max(500 - currentTime / 1000000, 0));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (success) {
                    viewModel.computerName = getComputerName();
                    viewModel.state.set(State.CONNECTED);
                } else {
                    viewModel.state.set(prevState);

                    Snackbar.make(activity.findViewById(android.R.id.content),
                            getString(R.string.connection_error_msg)
                                    + (errorMessage == null || errorMessage.isEmpty()? "" : "\n" + errorMessage),
                            Snackbar.LENGTH_SHORT).show();
                }

//                        Snackbar.make(activity.findViewById(android.R.id.content),
//                                success ? R.string.connection_success_msg : R.string.connection_error_msg,
//                                Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onDisconnect() {
                viewModel.state.set(State.NOT_CONNECTED);

                Snackbar.make(activity.findViewById(android.R.id.content),
                        R.string.connection_closed_msg,
                        Snackbar.LENGTH_SHORT).show();
            }
        };
        ConnectionManager.getInstance().makeConnection(connection);
    }

    private void wiFiButtonAction() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
        boolean enterIp = sharedPref.getBoolean(getString(R.string.settings_key_enter_ip), false);
        if (enterIp) viewModel.state.set(State.IP_INPUT);
        else scanQRCodeAction(Connection.Type.WIFI);
    }

    private void connectButtonAction() {
        try {
            if (!ConnectionManager.getInstance().isConnectionActive()) {
                String ipAddress = viewModel.getIpAddress();
                if (ipAddress.isEmpty()) throw new RuntimeException("IP Address field empty");
                String portText = viewModel.getPortStr();
                if (portText.isEmpty()) throw new RuntimeException("Port field empty");
                int port = Integer.parseInt(portText);

                makeWiFiConnection(ipAddress, port);
            } else { // if already connected disconnect on click
                ConnectionManager.getInstance().closeConnection();
                viewModel.state.set(State.NOT_CONNECTED);
            }
        } catch (RuntimeException e) {
            // show a snackbar if an error occurred
            Snackbar snackbar = Snackbar.make(contextView, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);
            snackbar.setAction(R.string.retry_msg, view -> connectButtonAction());
            snackbar.show();
        }

        hideSoftKeyboard();
    }

    // hide the soft keyboard on button press
    private void hideSoftKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            Log.i(TAG, "connectButtonAction: soft keyboard cannot be hidden because it is not currently active");
        } catch (Exception e) {
            Log.e(TAG, "connectButtonAction: error closing soft keyboard", e);
        }
    }
}
