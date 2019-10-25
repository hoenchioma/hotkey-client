package com.rfw.hotkey.ui.mouse;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;



public class MouseFragment extends Fragment {

    private static final String TAG = "MouseFragment";

    private boolean mouseMoved = false;
    private boolean scrollMoved = false;
    private float initX = 0;
    private float initY = 0;
    private float disX;
    private float disY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mouse, container, false);
        TextView touchpad = (TextView) v.findViewById(R.id.touchpadID);
        TextView scroll = (TextView) v.findViewById(R.id.scrollID);
        Button leftClick = (Button) v.findViewById(R.id.leftClickID);
        Button rightClick = (Button) v.findViewById(R.id.rightClickID);

        touchpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendMessageToServer("LeftClick",0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        touchpad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initX = event.getX();
                        initY = event.getY();
                        mouseMoved = false;
                        disX = 0;
                        disY = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        disX = event.getX() - initX;
                        disY = event.getY() - initY;
                        initX = event.getX();
                        initY = event.getY();
                        try {
                            sendMessageToServer("TouchpadMove",(int)disX, (int)disY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mouseMoved = true;
                        break;
                    case MotionEvent.ACTION_UP:

                        if (disX == 0 && disY == 0) {
                            try {
                                sendMessageToServer("LeftClick",0, 0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
                return true;
            }
        });

        scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initY = event.getY();
                        scrollMoved = false;
                        disY = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        disY = event.getY() - initY;
                        initY = event.getY();
                        if (disY != 0) {
                            try {
                                sendMessageToServer("ScrollMove",0, (int)disY);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        scrollMoved = true;
                        break;
                    case MotionEvent.ACTION_UP:

                        if (!scrollMoved) {
                            try {
                                sendMessageToServer("ScrollClick",0, 0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
                return true;
            }
        });

        leftClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void  onClick(View view){
                try {
                    sendMessageToServer("LeftClick",0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        leftClick.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view){

                return true;
            }
        });

        rightClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void  onClick(View view){
                try {
                    sendMessageToServer("RightClick",0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        rightClick.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view){

                return true;
            }
        });

        return v;
    }


    private void sendMessageToServer(String action, int moveX, int moveY) throws JSONException {
        JSONObject packet = new JSONObject();
        packet.put("type", "mouse");
        switch (action){
            case "TouchpadMove":
                try{
                    packet.put("action", action);
                    packet.put("deltaX", moveX);
                    packet.put("deltaY", moveY);
                } catch (JSONException e) {
                    Log.e("MouseFragment", "sendMessageToServer: error sending mouse movement", e);
                }
                break;
            case "RightClick":
                try{
                    packet.put("action", action);
                }catch (JSONException e){
                    Log.e("MouseFragment", "sendMessageToServer: error sending right click", e);
                }
                break;
            case "LeftClick":
                try{
                    packet.put("action", action);
                }catch (JSONException e){
                    Log.e("MouseFragment", "sendMessageToServer: error sending left click", e);
                }
                break;
            case "ScrollMove":
                try{
                    packet.put("action", action);
                    packet.put("deltaY", moveY);
                }catch (JSONException e){
                    Log.e("MouseFragment", "sendMessageToServer: error sending scroll movement", e);
                }
                break;
            case "ScrollClick":
                try{
                    packet.put("action", action);
                }catch (JSONException e){
                    Log.e("MouseFragment", "sendMessageToServer: error sending scroll click", e);
                }
                break;

        }
        ConnectionManager.getInstance().sendPacket(packet);
    }
}
