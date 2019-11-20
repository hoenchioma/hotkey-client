package com.rfw.hotkey.ui.multimedia;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;
import com.rfw.hotkey.util.misc.DispatchKeyEventHandler;
import com.rfw.hotkey.util.misc.LoopedExecutor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Fragment which emulates media controls
 * of a keyboard
 *
 * @author Raheeb Hassan
 * @author Farhan Kabir
 */
public class MultimediaFragment extends Fragment implements DispatchKeyEventHandler {
    private static final String TAG = "MultimediaFragment";

    private LoopedExecutor buttonPresser;

    private static final long BUTTON_PRESS_DELAY = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_multimedia, container, false);
        buttonPresser = null;

        ImageView playPauseIcon = (ImageView) v.findViewById(R.id.playPauseID);
        ImageView volumeUp = (ImageView) v.findViewById(R.id.volumeUpID);
        ImageView volumeDown = (ImageView) v.findViewById(R.id.volumeDownID);
        ImageView mute = (ImageView) v.findViewById(R.id.muteID);
        ImageView nextIcon = (ImageView) v.findViewById(R.id.nextID);
        ImageView prevIcon = (ImageView) v.findViewById(R.id.prevID);

        playPauseIcon.setColorFilter(getResources().getColor(R.color.colorAccent));

        playPauseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToServer("playPause");
            }
        });

        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToServer("next");
            }
        });

        prevIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToServer("prev");
            }
        });

        volumeUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    if (buttonPresser == null) {
                        buttonPresser = new LoopedExecutor(BUTTON_PRESS_DELAY) {
                            @Override
                            public void task() {
                                sendMessageToServer("volumeUp");
                            }
                        };
                        buttonPresser.start();
                    }
                } else {
                    if (buttonPresser != null) {
                        buttonPresser.end();
                        buttonPresser = null;
                    }
                }
                return false;
            }
        });

        volumeDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    if (buttonPresser == null) {
                        buttonPresser = new LoopedExecutor(BUTTON_PRESS_DELAY) {
                            @Override
                            public void task() {
                                sendMessageToServer("volumeDown");
                            }
                        };
                        buttonPresser.start();
                    }
                } else {
                    if (buttonPresser != null) {
                        buttonPresser.end();
                        buttonPresser = null;
                    }
                }

                return false;
            }
        });

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToServer("mute");
            }
        });

        return v;
    }

    /**
     * Method to be invoked by dispatchKeyEvent from enclosing activity
     * (return null means not handled)
     */
    @Override
    public Boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                sendMessageToServer("volumeUp");
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                sendMessageToServer("volumeDown");
                return true;
            default:
                return DispatchKeyEventHandler.super.dispatchKeyEvent(event);
        }
    }

    private void sendMessageToServer(String action) {
        try{
            JSONObject packet = new JSONObject();
            packet.put("type", "media");
            packet.put("action", action);
            ConnectionManager.getInstance().sendPacket(packet);
        } catch (JSONException e) {
            Log.e("MediaFragment", "sendMessageToServer: error sending media info", e);
        }
    }
}
