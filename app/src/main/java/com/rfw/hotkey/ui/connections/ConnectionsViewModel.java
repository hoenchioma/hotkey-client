package com.rfw.hotkey.ui.connections;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

public class ConnectionsViewModel extends ViewModel {
    public ObservableField<State> state = new ObservableField<>(State.NOT_CONNECTED);
    public String computerName;

    private String ipAddress = "";
    private String portStr = "";

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPortStr() {
        return portStr;
    }

    public void setPortStr(String portStr) {
        this.portStr = portStr;
    }

    public enum State {
        NOT_CONNECTED,
        CONNECTING,
        IP_INPUT,
        CONNECTED
    }
}
