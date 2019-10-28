package com.rfw.hotkey.ui;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class GamepadActivity extends AppCompatActivity {

    private ArrayList<ImageView> buttons;
    private boolean editLayout;
    ImageView rightStick;
    ImageView editButton;
    GridView keyBoard;
    SeekBar seekBar;

    private int xDelta;
    private int yDelta;
    private int widthV;
    private int heightV;
    private float RSX;
    private float RSY;
    private float disRSX;
    private float disRSY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamepad);
        editLayout = false;


        buttons = new ArrayList<ImageView>();

        buttons.add(findViewById(R.id.leftLeftID));
        buttons.add(findViewById(R.id.leftBotID));
        buttons.add(findViewById(R.id.leftRightID));
        buttons.add(findViewById(R.id.leftTopID));
        buttons.add(findViewById(R.id.rightLeftID));
        buttons.add(findViewById(R.id.rightBotID));
        buttons.add(findViewById(R.id.rightRightID));
        buttons.add(findViewById(R.id.rightTopID));
        buttons.add(findViewById(R.id.LBID));
        buttons.add(findViewById(R.id.LTID));
        buttons.add(findViewById(R.id.LSID));
        buttons.add(findViewById(R.id.RBID));
        buttons.add(findViewById(R.id.RTID));
        buttons.add(findViewById(R.id.RSID));
        buttons.add(findViewById(R.id.menuID));
        buttons.add(findViewById(R.id.startID));
        buttons.add(findViewById(R.id.leftStickTopID));
        buttons.add(findViewById(R.id.leftStickLeftID));
        buttons.add(findViewById(R.id.leftStickBotID));
        buttons.add(findViewById(R.id.leftStickRightID));
        rightStick = findViewById(R.id.rightStickID);

        init();

        for(int i = 0; i < buttons.size(); i++) buttons.get(i).setOnTouchListener(onTouchListener());

        rightStick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(editLayout) {
                    final int x = (int) event.getRawX();
                    final int y = (int) event.getRawY();

                    switch (event.getAction() & MotionEvent.ACTION_MASK) {

                        case MotionEvent.ACTION_DOWN:
                            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                    view.getLayoutParams();

                            xDelta = x - view.getLeft();
                            yDelta = y - view.getTop();
                            widthV = view.getWidth();
                            heightV = view.getHeight();
                            break;

                        case MotionEvent.ACTION_UP:

                            break;

                        case MotionEvent.ACTION_MOVE:
                            view.setLeft(x - xDelta);
                            view.setTop(y - yDelta);
                            view.setRight(view.getLeft() + widthV);
                            view.setBottom(view.getTop() + heightV);
                            break;
                    }
                }
                else{
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            RSX = event.getX();
                            RSY = event.getY();
                            disRSX = 0;
                            disRSY = 0;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            disRSX = event.getX() - RSX;
                            disRSY = event.getY() - RSY;
                            RSX = event.getX();
                            RSY = event.getY();
                            break;
                    }
                    try {
                        JSONObject packet = new JSONObject();
                        packet.put("type", "mouse");
                        packet.put("action", "TouchpadMove");
                        packet.put("deltaX", disRSX);
                        packet.put("deltaY", disRSY);
                        ConnectionManager.getInstance().sendPacket(packet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(editLayout) {
                    final int x = (int) event.getRawX();
                    final int y = (int) event.getRawY();

                    switch (event.getAction() & MotionEvent.ACTION_MASK) {

                        case MotionEvent.ACTION_DOWN:
                            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                    view.getLayoutParams();

                            xDelta = x - view.getLeft();
                            yDelta = y - view.getTop();
                            widthV = view.getWidth();
                            heightV = view.getHeight();
                            break;

                        case MotionEvent.ACTION_UP:

                            break;

                        case MotionEvent.ACTION_MOVE:
                            view.setLeft(x - xDelta);
                            view.setTop(y - yDelta);
                            view.setRight(view.getLeft() + widthV);
                            view.setBottom(view.getTop() + heightV);
                            break;
                    }
                }
                else{
                    while(event.getAction() != MotionEvent.ACTION_UP) sendKeyToServer("char",(String)view.getTag());
                }
                return true;
            }
        };
    }

    void init(){
        SharedPreferences sharedPref = getSharedPreferences("Hotkey/GamepadData", MODE_PRIVATE);
        String data;
        try {
            JSONObject buttonData;
            for(int i = 0; i < buttons.size(); i++){
                data = sharedPref.getString("buttonData" + Integer.toString(i), null);
                if(data != null){
                    buttonData = new JSONObject(data);
                    buttons.get(i).setTag(buttonData.getString("action"));
                    buttons.get(i).setTag(0,"i");
                    buttons.get(i).setLeft(buttonData.getInt("left"));
                    buttons.get(i).setRight(buttonData.getInt("right"));
                    buttons.get(i).setTop(buttonData.getInt("top"));
                    buttons.get(i).setBottom(buttonData.getInt("bottom"));
                }
            }
            data = sharedPref.getString("buttonDataRightStick", null);
            if(data != null) {
                 buttonData = new JSONObject(data);
                rightStick.setLeft(buttonData.getInt("left"));
                rightStick.setRight(buttonData.getInt("right"));
                rightStick.setTop(buttonData.getInt("top"));
                rightStick.setBottom(buttonData.getInt("bottom"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void sendKeyToServer(String action, String key) {
        JSONObject packet = new JSONObject();

        try {
            packet.put("type", "keyboard");
            packet.put("action", action);
            packet.put("key", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ConnectionManager.getInstance().sendPacket(packet);
    }
}
