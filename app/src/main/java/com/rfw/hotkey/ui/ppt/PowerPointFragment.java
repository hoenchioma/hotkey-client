package com.rfw.hotkey.ui.ppt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * A fragment to help user to control ppt slides and also use pointer
 * feature : page change, laser pointer, full Screen
 * @author  Shadman Wadith
 */

public class PowerPointFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private boolean mouseMoved = false;
    private boolean scrollMoved = false;
    private boolean isPointerOn = false;
    private float initX = 0;
    private float initY = 0;
    private float disX;
    private float disY;

    private TextView touchpad;
    private ImageButton fullScreenButton, upButton, leftButton, downButton, righButton, pointerButton;
    private Button fromThisSlideButton, fromTheBeginningButton;
    private Boolean isFullScreen;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_power_point, container, false);
        initialization(rootView);

        touchpad.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("LongLogTag")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initX = event.getX();
                        initY = event.getY();
                        mouseMoved = false;
                        disX = 0;
                        disY = 0;
                        //sendMessageToServer("on","pointer_status");
                        //Log.d("powerPoint Fragment pointer", disX + " " + disY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        disX = event.getX() - initX;
                        disY = event.getY() - initY;
                        initX = event.getX();
                        initY = event.getY();
                        try {
                            sendMessageToServer("pointer", (int) disX, (int) disY);
                           // Log.d("powerPoint Fragment pointer", disX + " " + disY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mouseMoved = true;
                        //Log.d("powerPoint Fragment pointer", disX + " " + disY);
                        break;

                }
                return true;
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    private void initialization(View rootView) {
        isFullScreen = false;
        touchpad = rootView.findViewById(R.id.pointerCursorID);
        fullScreenButton = rootView.findViewById(R.id.ppt_presentationButtonID);
        upButton = rootView.findViewById(R.id.ppt_upButtonID);
        fromTheBeginningButton = rootView.findViewById(R.id.fromTheBeginningButtonID);
        fromThisSlideButton = rootView.findViewById(R.id.fromThisSlideButtonID);
        leftButton = rootView.findViewById(R.id.ppt_leftButtonID);
        downButton = rootView.findViewById(R.id.ppt_downButtonID);
        righButton = rootView.findViewById(R.id.ppt_rightButtonID);
        pointerButton = rootView.findViewById(R.id.ppt_pointerID);
        touchpad.setVisibility(View.INVISIBLE);
        //touchpad.setOnClickListener(this);
        touchpad.setOnTouchListener(this);
        fullScreenButton.setOnClickListener(this);
        upButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        downButton.setOnClickListener(this);
        righButton.setOnClickListener(this);
        fromThisSlideButton.setOnClickListener(this);
        fromTheBeginningButton.setOnClickListener(this);
        pointerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.ppt_presentationButtonID:

                if (!isFullScreen) {
                    Toast.makeText(getActivity(), "Select Presentation Mode", Toast.LENGTH_SHORT).show();
                    if (fromThisSlideButton.getVisibility() == View.VISIBLE) {
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
                fullScreenButton.setImageResource(R.drawable.ic_fullscreen_exit_white_24dp);
                fromTheBeginningButton.setVisibility(View.INVISIBLE);
                fromThisSlideButton.setVisibility(View.INVISIBLE);
                break;
            case R.id.fromTheBeginningButtonID:
                sendMessageToServer("beginning", "modifier");
                Log.d("onclick", "beginning");
                isFullScreen = true;
                fullScreenButton.setImageResource(R.drawable.ic_fullscreen_exit_white_24dp);
                fromTheBeginningButton.setVisibility(View.INVISIBLE);
                fromThisSlideButton.setVisibility(View.INVISIBLE);
                break;
            case R.id.ppt_upButtonID:
                // sendMessageToServer("modifier");
                sendMessageToServer("UP", "modifier");
                Log.d("onclick", "UP");
                break;
            case R.id.ppt_leftButtonID:
                //sendMessageToServer("modifier");
                sendMessageToServer("LEFT", "modifier");
                Log.d("onclick", "LEFT");
                break;
            case R.id.ppt_downButtonID:
                // sendMessageToServer("modifier");
                sendMessageToServer("DOWN", "modifier");
                Log.d("onclick", "DOWN");
                break;
            case R.id.ppt_rightButtonID:
                //sendMessageToServer("modifier");
                sendMessageToServer("RIGHT", "modifier");
                Log.d("onclick", "RIGHT");
                break;
            case R.id.ppt_pointerID:
                if(!isPointerOn){
                    touchpad.setVisibility(View.VISIBLE);
                    isPointerOn = true;
                    sendMessageToServer("on","point");
                }
                else {
                    touchpad.setVisibility(View.INVISIBLE);
                    isPointerOn = false;
                    sendMessageToServer("off","point");
                }
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

            ConnectionManager.getInstance().sendPacket(packet);

        } catch (JSONException e) {
            Log.e("PowerPointFragment", "sendMessageToServer: error sending key-press", e);
        }
    }
    /**
     * sends the message of specified action to Connection Manager
     *
     * @param moveX move x co-ordinate of pointer
     * @param moveY move y co-ordinate of pointer
     * @param action  type of the message
     */
    private void sendMessageToServer(String action, int moveX, int moveY) throws JSONException {
        JSONObject packet = new JSONObject();
        try {
            packet.put("type", "ppt");
            packet.put("action", action);
            packet.put("deltaX", moveX);
            packet.put("deltaY", moveY);
            ConnectionManager.getInstance().sendPacket(packet);

        } catch (JSONException e) {
            Log.e("PowerPointFragment", "sendMessageToServer: error sending key-press", e);
        }

//        packet.put("type", "ppt");
//        switch (action) {
//            case "pointer":
//                try {
//                    packet.put("action", action);
//                    packet.put("deltaX", moveX);
//                    packet.put("deltaY", moveY);
//                    Log.d("powerPoint", moveX + " " + moveY);
//                    ConnectionManager.getInstance().sendPacket(packet);
//                } catch (JSONException e) {
//                    Log.e("PowerPointFragment", "sendMessageToServer: error sending pointer movement", e);
//                }
//                break;
//        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        return false;
    }
}
