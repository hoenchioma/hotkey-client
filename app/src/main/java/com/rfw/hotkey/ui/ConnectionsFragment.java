package com.rfw.hotkey.ui;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.rfw.hotkey.BuildConfig;
import com.rfw.hotkey.R;
import com.rfw.hotkey.databinding.FragmentConnectionsBinding;
import com.rfw.hotkey.net.ConnectionManager;

import java.io.IOException;
import java.util.Objects;

public class ConnectionsFragment extends Fragment {
    private static final String TAG = "ConnectionsFragment";

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

        connectButton = contextView.findViewById(R.id.connect_button);
        ipAddressTextField = contextView.findViewById(R.id.ip_address_textfield);
        portTextField = contextView.findViewById(R.id.port_textfield);

        connectButton.setOnClickListener(view -> connectButtonAction());

        return contextView;
    }

    private void connectButtonAction() {
        try {
            String ipAddress = Objects.requireNonNull(ipAddressTextField.getEditText()).getText().toString();
            if (ipAddress.isEmpty()) throw new RuntimeException("IP Address field empty");
            String portText = Objects.requireNonNull(portTextField.getEditText()).getText().toString();
            if (portText.isEmpty()) throw new RuntimeException("Port field empty");
            int port = Integer.parseInt(portText);

            new ConnectionTask(ipAddress, port).execute();
        } catch (Exception e) {
            if (e.getMessage() == null) throw e;
            // show a snackbar if an error occurred
            Snackbar snackbar = Snackbar.make(contextView,
                    e.getMessage() != null ? e.getMessage(): getString(R.string.connection_error),
                    Snackbar.LENGTH_SHORT);
            snackbar.setAction(R.string.retry, view -> connectButtonAction());
            snackbar.show();
        }
    }

    private static class ConnectionTask extends AsyncTask<Void, Void, Void> {
        String ipAddress;
        int port;

        ConnectionTask(String ipAddress, int port) {
            this.ipAddress = ipAddress;
            this.port = port;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ConnectionManager.getInstance().connect(ipAddress, port);
            } catch (IOException e) {
                Log.e("ConnectionTask", "doInBackground: Error connecting", e);
                e.printStackTrace();
            }
            return null;
        }
    }
}
