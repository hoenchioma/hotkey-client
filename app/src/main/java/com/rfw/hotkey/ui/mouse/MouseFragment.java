package com.rfw.hotkey.ui.mouse;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;
import com.rfw.hotkey.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.rfw.hotkey.util.Utils.getIntPref;

/**
 * Fragment which creates a touchpad screen
 * on the device and emulates as one
 *
 * @author Farhan Kabir
 */
public class MouseFragment extends Fragment {

    private static final String TAG = "MouseFragment";

    private double mouseSensitivity;

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
        ImageButton leftClick = (ImageButton) v.findViewById(R.id.leftClickID);
        ImageButton rightClick = (ImageButton) v.findViewById(R.id.rightClickID);
        ImageButton scrollButton = (ImageButton) v.findViewById(R.id.scrollbuttonClickID);

        touchpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    send("leftClick", 0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        touchpad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 2) {
                    //Toast.makeText(getContext(),"rightClick", Toast.LENGTH_SHORT).show();
                    try {
                        send("rightClick", 0, 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (event.getPointerCount() == 3) {
                    //Toast.makeText(getContext(),"rightClick", Toast.LENGTH_SHORT).show();
                    try {
                        send("scrollClick", 0, 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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
                            send("touchpadMove", (int) disX, (int) disY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mouseMoved = true;
                        break;
                    case MotionEvent.ACTION_UP:

                        if (disX == 0 && disY == 0) {
                            try {
                                send("leftClick", 0, 0);
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
                                send("scrollMove", 0, (int) disY);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        scrollMoved = true;
                        break;
                    case MotionEvent.ACTION_UP:

                        if (!scrollMoved) {
                            try {
                                send("scrollClick", 0, 0);
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
            public void onClick(View view) {
                try {
                    send("leftClick", 0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    send("scrollClick", 0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        leftClick.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return true;
            }
        });

        rightClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    send("rightClick", 0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        rightClick.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return true;
            }
        });

        // load and set mouse sensitivity
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
        int mouseSensitivityPerc = getIntPref(sharedPref,
                getString(R.string.settings_key_mouse_sensitivity),
                Constants.Mouse.MOUSE_SENSITIVITY_PERC);
        mouseSensitivity = mouseSensitivityPerc / 100.0;

        return v;
    }


    private void send(String action, int moveX, int moveY) throws JSONException {
        JSONObject packet = new JSONObject();
        packet.put("type", "mouse");
        switch (action) {
            case "touchpadMove":
                try {
                    packet.put("action", action);
                    packet.put("deltaX", (int) (moveX * mouseSensitivity));
                    packet.put("deltaY", (int) (moveY * mouseSensitivity));
                } catch (JSONException e) {
                    Log.e("MouseFragment", "send: error sending mouse movement", e);
                }
                break;
            case "rightClick":
                try {
                    packet.put("action", action);
                } catch (JSONException e) {
                    Log.e("MouseFragment", "send: error sending right click", e);
                }
                break;
            case "leftClick":
                try {
                    packet.put("action", action);
                } catch (JSONException e) {
                    Log.e("MouseFragment", "send: error sending left click", e);
                }
                break;
            case "scrollMove":
                try {
                    packet.put("action", action);
                    packet.put("deltaY", moveY);
                } catch (JSONException e) {
                    Log.e("MouseFragment", "send: error sending scroll movement", e);
                }
                break;
            case "scrollClick":
                try {
                    packet.put("action", action);
                } catch (JSONException e) {
                    Log.e("MouseFragment", "send: error sending scroll click", e);
                }
                break;

        }
        ConnectionManager.getInstance().sendPacket(packet);
    }
}
