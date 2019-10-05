package com.rfw.hotkey.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;


public class PowerPointFragment extends Fragment implements View.OnClickListener,View.OnTouchListener{

    private Button fullScreenButton,ESCButton, upButton, pgdnButton, leftButton, downButton, righButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_power_point, container, false);
        initialization(rootView);
        // Inflate the layout for this fragment
        return rootView;
    }

    private void initialization(View rootView) {
        fullScreenButton = rootView.findViewById(R.id.fullScreenButtonID);
        ESCButton = rootView.findViewById(R.id.ESCButtonID);
        upButton = rootView.findViewById(R.id.upButtonID);
        pgdnButton = rootView.findViewById(R.id.pgdnButtonID);
        leftButton = rootView.findViewById(R.id.leftButtonID);
        downButton = rootView.findViewById(R.id.downButtonID);
        righButton = rootView.findViewById(R.id.rightButtonID);
        fullScreenButton.setOnClickListener(this);
        ESCButton.setOnClickListener(this);
        upButton.setOnClickListener(this);
        pgdnButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        downButton.setOnClickListener(this);
        righButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if(id == R.id.fullScreenButtonID){
            sendMessageToServer("F5", "modifier");
            Log.d("onclick", "F5");
        }
        if(id == R.id.ESCButtonID){
            sendMessageToServer("ESC","modifier");
            Log.d("onclick","ESC");
        }
        if (id == R.id.upButtonID) {
            // sendMessageToServer("modifier");
            sendMessageToServer("UP", "modifier");
            Log.d("onclick", "UP");
        }
        if (id == R.id.pgdnButtonID) {
            // sendMessageToServer("modifier");
            sendMessageToServer("PGDN", "modifier");
            Log.d("onclick", "PGDN");
        }
        if (id == R.id.leftButtonID) {
            //sendMessageToServer("modifier");
            sendMessageToServer("LEFT", "modifier");
            Log.d("onclick", "LEFT");
        }
        if (id == R.id.downButtonID) {
            // sendMessageToServer("modifier");
            sendMessageToServer("DOWN", "modifier");
            Log.d("onclick", "DOWN");
        }
        if (id == R.id.rightButtonID) {
            //sendMessageToServer("modifier");
            sendMessageToServer("RIGHT", "modifier");
            Log.d("onclick", "RIGHT");
        }
    }

    /**
     * sends the message of specified action to Connection Manager
     *
     * @param message message (key press type)
     * @param action  type of the message
     */
    private void sendMessageToServer(String message, String action) {
        JSONObject packet = new JSONObject();

        try {
            packet.put("type", "ppt");
            packet.put("action", action);
            packet.put("key", message);

            ConnectionManager.getInstance().sendPacket(packet);

        } catch (JSONException e) {
            Log.e("PowerPointFragment", "sendMessageToServer: error sending key-press", e);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
