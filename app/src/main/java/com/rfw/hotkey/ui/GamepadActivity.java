package com.rfw.hotkey.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;
import com.rfw.hotkey.util.LoopedExecutor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamepadActivity extends AppCompatActivity {

    private ArrayList<ImageView> buttons;
    private ArrayList<String> actions;
    private boolean editLayout;
    private ImageView rightStick;
    private ImageView editButton;
    private ImageView saveButton;
    private GridView keyBoardGrid;
    private SeekBar seekBar;

    private int xDelta;
    private int yDelta;
    private int widthV;
    private int heightV;
    private float RSX;
    private float RSY;
    private float disRSX;
    private float disRSY;
    private int curIndex;

    static final String[] keyboardKeys = new String[]{
            "0","1", "2", "3", "4",
            "5","6", "7", "8", "9",

            "ESC", "ALT", "CTRL", "SHIFT", "DEL",
            "INS", "HOME", "END", "PGUP", "PGDN",

            "a", "b", "c", "d", "e",
            "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o",
            "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y",
            "z"

    };

    final List<String> gridKeys = new ArrayList<String>(Arrays.asList(keyboardKeys));

    private static final long BUTTON_PRESS_DELAY = 100; // milliseconds

    private LoopedExecutor buttonPresser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamepad);
        editLayout = false;


        buttons = new ArrayList<ImageView>();
        actions = new ArrayList<>();

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
        seekBar = findViewById(R.id.seekBarID);
        keyBoardGrid = findViewById(R.id.keyGridID);
        editButton = findViewById(R.id.editButtonID);
        saveButton = findViewById(R.id.saveButtonID);

        init();

        for(int i = 0; i < buttons.size(); i++) buttons.get(i).setOnTouchListener(onTouchListener());


        ///////////handle settings button activity

        Intent intent = getIntent();
        if(intent!= null) editLayout = true;


        //////////////////

        if(editLayout){
            editButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        saveData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(curIndex != -1) {
                        keyBoardGrid.setVisibility(View.VISIBLE);

                        keyBoardGrid.setAdapter(new ArrayAdapter<String>(
                                getApplicationContext(), android.R.layout.simple_list_item_1, gridKeys) {
                            public View getView(int position, View convertView, ViewGroup parent) {

                                View view = super.getView(position, convertView, parent);

                                TextView tv = (TextView) view;


                                tv.setTextColor(Color.WHITE);

                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
                                );
                                tv.setLayoutParams(lp);

                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();

                                tv.setLayoutParams(params);

                                tv.setGravity(Gravity.CENTER);

                                tv.setText(gridKeys.get(position));

                                if (tv.getText().toString().equals(actions.get(curIndex)))
                                    tv.setBackgroundColor(Color.parseColor("#7289da"));
                                else tv.setBackgroundColor(Color.parseColor("#23272a"));

                                return tv;
                            }
                        });

                        keyBoardGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String buttonName = parent.getItemAtPosition(position).toString();
                                actions.set(curIndex, buttonName);
                                Toast.makeText(getApplicationContext(),
                                        "Altered with " + buttonName, Toast.LENGTH_SHORT).show();
                                keyBoardGrid.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    else Toast.makeText(getApplicationContext(),
                            "Invalid key selected", Toast.LENGTH_SHORT).show();
                }
            });

        }




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
                }////////////////////////implement thread
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
                    curIndex = Integer.parseInt((String) view.getTag());
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
                else {
                    if(event.getAction() != MotionEvent.ACTION_UP) {
                        if (buttonPresser == null) {
                            buttonPresser = new LoopedExecutor(BUTTON_PRESS_DELAY) {
                                @Override
                                public void task() {
                                    sendKeyToServer("char",
                                            actions.get(Integer.parseInt((String) view.getTag())));
                                }
                            };
                            buttonPresser.start();
                        }
//                        Toast.makeText(getApplicationContext(),
//                                (String)view.getTag(), Toast.LENGTH_SHORT).show();
                        keyBoardGrid.setVisibility(View.INVISIBLE);
                    } else {
                        if (buttonPresser != null) {
                            buttonPresser.end();
                            buttonPresser = null;
                        }
                    }
                }
                return true;
            }
        };
    }

    void init(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("HotkeyGamepadData", MODE_PRIVATE);
        keyBoardGrid.setVisibility(View.INVISIBLE);
        editButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.INVISIBLE);
        curIndex = -1;
        String data;
        try {
            JSONObject buttonData;
            for(int i = 0; i < buttons.size(); i++){
                actions.add("");
                data = sharedPref.getString("buttonData" + Integer.toString(i), null);
                if(data != null){
                    buttonData = new JSONObject(data);
                    actions.set( i, buttonData.getString("action"));
                    buttons.get(i).setLeft(buttonData.getInt("left"));
                    buttons.get(i).setRight(buttonData.getInt("right"));
                    buttons.get(i).setTop(buttonData.getInt("top"));
                    buttons.get(i).setBottom(buttonData.getInt("bottom"));
                }
                buttons.get(i).setTag(Integer.toString(i));
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

    void saveData() throws JSONException {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("HotkeyGamepadData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        JSONObject buttonData;
        for(int i = 0; i < buttons.size(); i++){
            buttonData = new JSONObject();
            buttonData.put("action",actions.get(i));
            buttonData.put("left",(Integer)buttons.get(i).getLeft());
            buttonData.put("right",(Integer)buttons.get(i).getRight());
            buttonData.put("top",(Integer)buttons.get(i).getTop());
            buttonData.put("bottom",(Integer)buttons.get(i).getBottom());

            editor.putString("buttonData" + Integer.toString(i), buttonData.toString());
        }
        buttonData = new JSONObject();
        buttonData.put("left",(Integer)rightStick.getLeft());
        buttonData.put("right",(Integer)rightStick.getRight());
        buttonData.put("top",(Integer)rightStick.getTop());
        buttonData.put("bottom",(Integer)rightStick.getBottom());
        editor.putString("buttonDataRightStick", buttonData.toString());
        editor.apply();
        Toast.makeText(getApplicationContext(),
                "Data Saved", Toast.LENGTH_SHORT).show();
    }
}
