package com.rfw.hotkey.ui.connections;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.rfw.hotkey.R;
import com.rfw.hotkey.databinding.FragmentConnectionsBinding;
import com.rfw.hotkey.net.Connection;
import com.rfw.hotkey.net.ConnectionManager;
import com.rfw.hotkey.net.WiFiConnection;
import com.rfw.hotkey.util.Constants;

import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.rfw.hotkey.util.Utils.getIntPref;

public class ConnectionsFragment extends Fragment {
    private static final String TAG = ConnectionsFragment.class.getCanonicalName();

    private View contextView;

    // don't remove local fields of UI components (as they are required to save the state of Fragment)
    private MaterialButton connectButton;
    private TextInputLayout ipAddressTextField;
    private EditText ipAddressEditText;
    private TextInputLayout portTextField;
    private EditText portEditText;
    private ProgressBar connectingSpinner;
    private TextView statusTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        FragmentConnectionsBinding binding = FragmentConnectionsBinding.inflate(inflater, container, false);
        contextView = binding.getRoot();

        // setup databinding for cm (connectionManager)
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        binding.setCm(connectionManager);

        connectButton = contextView.findViewById(R.id.connectButton);
        ipAddressTextField = contextView.findViewById(R.id.ipAddressTextField);
        ipAddressEditText = contextView.findViewById(R.id.ipAddressEditText);
        portTextField = contextView.findViewById(R.id.portTextField);
        portEditText = contextView.findViewById(R.id.portEditText);
        connectingSpinner = contextView.findViewById(R.id.connectingSpinner);
        statusTextView = contextView.findViewById(R.id.statusTextView);

        connectButton.setOnClickListener(view -> connectButtonAction());

        // load ip address and port from shared pref
        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        String ipAddress = sharedPref.getString("ipAddress", null);
        String portText = sharedPref.getString("portText", null);
        if (ipAddress != null && portText != null) {
            ipAddressEditText.setText(ipAddress);
            portEditText.setText(portText);
        }

        return contextView;
    }

    @Override
    public void onStop() {
        // save ip address and port on exit
        String ipAddress = ipAddressEditText.getText().toString();
        String portText = portEditText.getText().toString();
        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ipAddress", ipAddress);
        editor.putString("portText", portText);
        editor.apply();

        super.onStop();
    }

    private void connectButtonAction() {
        try {
            if (!ConnectionManager.getInstance().isConnectionActive()) {
                String ipAddress = Objects.requireNonNull(ipAddressTextField.getEditText()).getText().toString();
                if (ipAddress.isEmpty()) throw new RuntimeException("IP Address field empty");
                String portText = Objects.requireNonNull(portTextField.getEditText()).getText().toString();
                if (portText.isEmpty()) throw new RuntimeException("Port field empty");
                int port = Integer.parseInt(portText);

                statusTextView.setVisibility(View.GONE); // hide text view
                connectingSpinner.setVisibility(View.VISIBLE); // show loading spinner
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

                        connectingSpinner.setVisibility(View.GONE); // hide loading spinner
                        statusTextView.setVisibility(View.VISIBLE); // show status text view

                        Snackbar.make(activity.findViewById(android.R.id.content),
                                success ? R.string.connection_success_msg : R.string.connection_error_msg,
                                Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDisconnect() {
                        Snackbar.make(activity.findViewById(android.R.id.content),
                                R.string.connection_closed_msg,
                                Snackbar.LENGTH_SHORT).show();
                    }
                };
                ConnectionManager.getInstance().makeConnection(connection);
            } else { // if already connected disconnect on click
                ConnectionManager.getInstance().closeConnection();
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
