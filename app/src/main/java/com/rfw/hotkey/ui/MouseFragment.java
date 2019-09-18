package com.rfw.hotkey.ui;

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

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static java.lang.System.out;


public class MouseFragment extends Fragment {

    private static final String TAG = "MouseFragment";

    private boolean mouseMoved = false;
    private boolean leftClickAction = false;
    private boolean rightClickAction = false;
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

        touchpad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initX = event.getX();
                        initY = event.getY();
                        mouseMoved = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        disX = event.getX() - initX;
                        disY = event.getY() - initY;


                        initX = event.getX();
                        initY = event.getY();
                        if (disX != 0 || disY != 0) {
                            out.println(disX + "," + disY);
                        }
                        mouseMoved = true;
                        break;
                    case MotionEvent.ACTION_UP:

                        if (!mouseMoved) {
                            out.println("left click");
                        }
                }
                out.println(initX +","+ initY);
                return true;
            }
        });

        scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initX = event.getX();
                        initY = event.getY();
                        mouseMoved = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        disX = event.getX() - initX;
                        disY = event.getY() - initY;


                        initX = event.getX();
                        initY = event.getY();
                        if (disX != 0 || disY != 0) {
                            out.println(disX + "," + disY);
                        }
                        mouseMoved = true;
                        break;
                    case MotionEvent.ACTION_UP:

                        if (!mouseMoved) {
                            out.println("scroll click");
                        }
                }
                return true;
            }
        });

        leftClick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Log.d(TAG, "onTouch: left click");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initX = event.getX();
                        initY = event.getY();
                        leftClickAction = true;
                        Log.d(TAG, "onTouch: left click");
                        break;
                    case MotionEvent.ACTION_UP:

                        if (leftClickAction) {
                            Log.d(TAG, "onTouch: left click");
                            leftClickAction = false;
                        }
                        break;
                }
                while(event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) out.println("left click");
                return true;
            }
        });

        rightClick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initX = event.getX();
                        initY = event.getY();
                        rightClickAction = true;
                        break;
                    case MotionEvent.ACTION_UP:

                        if (rightClickAction) {
                            out.println("right click");
                            rightClickAction = false;
                        }
                }
                while(event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) out.println("right click");
                return true;
            }
        });

        return v;
    }


    private void sendMessageToServer(String action, int moveX, int moveY) throws JSONException {
        JSONObject packet = new JSONObject();
        packet.put("type", "mouse");
        switch (action){
            case "Touchpad":
                try{
                    packet.put("type", action);
                    packet.put("deltaX", moveX);
                    packet.put("deltaY", moveY);
                } catch (JSONException e) {
                    Log.e("MouseFragment", "sendMessageToServer: error sending mouse movement", e);
                }
                break;
            case "RightClick":
                try{
                    packet.put("type", action);
                }catch (JSONException e){
                    Log.e("MouseFragment", "sendMessageToServer: error sending right click", e);
                }
                break;
        case "LeftClick":
                try{
                    packet.put("type", action);
                }catch (JSONException e){
                    Log.e("MouseFragment", "sendMessageToServer: error sending left click", e);
                }
                break;
            case "ScrollMove":
                try{
                    packet.put("type", action);
                    packet.put("deltaY", moveY);
                }catch (JSONException e){
                    Log.e("MouseFragment", "sendMessageToServer: error sending scroll movement", e);
                }
                break;
            case "ScrollClick":
                try{
                    packet.put("type", action);
                }catch (JSONException e){
                    Log.e("MouseFragment", "sendMessageToServer: error sending scroll click", e);
                }

        }

    }
}
