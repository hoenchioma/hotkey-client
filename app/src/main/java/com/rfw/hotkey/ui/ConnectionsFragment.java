package com.rfw.hotkey.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.rfw.hotkey.R;
import com.rfw.hotkey.databinding.FragmentConnectionsBinding;
import com.rfw.hotkey.net.Connection;
import com.rfw.hotkey.net.ConnectionManager;
import com.rfw.hotkey.net.WiFiConnection;

import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ConnectionsFragment extends Fragment {
    private static final String TAG = ConnectionsFragment.class.getCanonicalName();

    private View contextView;

    private MaterialButton connectButton;
    private TextInputLayout ipAddressTextField;
    private TextInputLayout portTextField;

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

        connectButton = contextView.findViewById(R.id.connect_button);
        ipAddressTextField = contextView.findViewById(R.id.ip_address_textfield);
        portTextField = contextView.findViewById(R.id.port_textfield);

        connectButton.setOnClickListener(view -> connectButtonAction());

        return contextView;
    }

    private void connectButtonAction() {
        try {
            if (!ConnectionManager.getInstance().isConnectionActive()) {
                String ipAddress = Objects.requireNonNull(ipAddressTextField.getEditText()).getText().toString();
                if (ipAddress.isEmpty()) throw new RuntimeException("IP Address field empty");
                String portText = Objects.requireNonNull(portTextField.getEditText()).getText().toString();
                if (portText.isEmpty()) throw new RuntimeException("Port field empty");
                int port = Integer.parseInt(portText);

                Connection connection = new WiFiConnection(ipAddress, port) {
                    Activity activity = getActivity();

                    @Override
                    protected void onConnect(boolean success) {
                        Snackbar.make(activity.getWindow().getDecorView().getRootView(),
                                success ? R.string.connection_success : R.string.connection_error,
                                Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onDisconnect() {
                        Snackbar.make(activity.getWindow().getDecorView().getRootView(),
                                R.string.connection_closed,
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
            snackbar.setAction(R.string.retry, view -> connectButtonAction());
            snackbar.show();
        }

        // hide the soft keyboard on button press
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
