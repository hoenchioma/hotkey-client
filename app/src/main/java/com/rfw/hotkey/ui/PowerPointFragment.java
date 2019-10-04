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
import android.widget.ImageButton;
import android.widget.Toast;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;


public class PowerPointFragment extends Fragment implements View.OnClickListener,View.OnTouchListener{

    private ImageButton fullScreenButton, upButton,  leftButton, downButton, righButton;
    private Button fromThisSlideButton,fromTheBeginningButton;
    private Boolean isFullScreen;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_power_point, container, false);
        initialization(rootView);
        // Inflate the layout for this fragment
        return rootView;
    }

    private void initialization(View rootView) {
        isFullScreen = false;
        fullScreenButton = rootView.findViewById(R.id.fullScreenButtonID);
        upButton = rootView.findViewById(R.id.upButtonID);
        fromTheBeginningButton = rootView.findViewById(R.id.fromTheBeginningButtonID);
        fromThisSlideButton = rootView.findViewById(R.id.fromThisSlideButtonID);
        leftButton = rootView.findViewById(R.id.leftButtonID);
        downButton = rootView.findViewById(R.id.downButtonID);
        righButton = rootView.findViewById(R.id.rightButtonID);
        fullScreenButton.setOnClickListener(this);
        upButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        downButton.setOnClickListener(this);
        righButton.setOnClickListener(this);
        fromThisSlideButton.setOnClickListener(this);
        fromTheBeginningButton.setOnClickListener(this);
    }
    // TODO Create a POPUP menu for Slide full screen
    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.fullScreenButtonID:

                if (!isFullScreen) {
                    //sendMessageToServer("F5", "modifier");
                    //Log.d("onclick", "F5");
                    //fullScreenButton.setImageResource(R.drawable.ic_fullscreen_exit_black_24dp);
                    Toast.makeText(getActivity(), "Select Presentation Mode", Toast.LENGTH_SHORT).show();
                    if(fromThisSlideButton.getVisibility() == View.VISIBLE){
                        fromTheBeginningButton.setVisibility(View.INVISIBLE);
                        fromThisSlideButton.setVisibility(View.INVISIBLE);
                    }
                    fromTheBeginningButton.setVisibility(View.VISIBLE);
                    fromThisSlideButton.setVisibility(View.VISIBLE);

                    //isFullScreen = true;
                } else {
                    sendMessageToServer("ESC", "modifier");
                    Log.d("onclick", "ESC");
                    fullScreenButton.setImageResource(R.drawable.ic_presentation_color);
                    Toast.makeText(getActivity(), "Normal Mode", Toast.LENGTH_SHORT).show();
                    isFullScreen = false;
                }

                break;
            case R.id.fromThisSlideButtonID:
                sendMessageToServer("current", "modifier");
                Log.d("onclick", "current");
                isFullScreen = true;
                fullScreenButton.setImageResource(R.drawable.ic_fullscreen_exit_black_24dp);
                fromTheBeginningButton.setVisibility(View.INVISIBLE);
                fromThisSlideButton.setVisibility(View.INVISIBLE);
                break;
            case R.id.fromTheBeginningButtonID:
                sendMessageToServer("beginning", "modifier");
                Log.d("onclick", "beginning");
                isFullScreen = true;
                fullScreenButton.setImageResource(R.drawable.ic_fullscreen_exit_black_24dp);
                fromTheBeginningButton.setVisibility(View.INVISIBLE);
                fromThisSlideButton.setVisibility(View.INVISIBLE);
                break;
            case R.id.upButtonID:
                // sendMessageToServer("modifier");
                sendMessageToServer("UP", "modifier");
                Log.d("onclick", "UP");
                break;
            case R.id.leftButtonID:
                //sendMessageToServer("modifier");
                sendMessageToServer("LEFT", "modifier");
                Log.d("onclick", "LEFT");
                break;
            case R.id.downButtonID:
                // sendMessageToServer("modifier");
                sendMessageToServer("DOWN", "modifier");
                Log.d("onclick", "DOWN");
                break;
            case R.id.rightButtonID:
                //sendMessageToServer("modifier");
                sendMessageToServer("RIGHT", "modifier");
                Log.d("onclick", "RIGHT");
                break;
            default:
                fromTheBeginningButton.setVisibility(View.INVISIBLE);
                fromThisSlideButton.setVisibility(View.INVISIBLE);


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

            new ConnectionManager.SendTask(packet).execute();

        } catch (JSONException e) {
            Log.e("PowerPointFragment", "sendMessageToServer: error sending key-press", e);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
