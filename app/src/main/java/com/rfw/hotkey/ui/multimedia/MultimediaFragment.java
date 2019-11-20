package com.rfw.hotkey.ui.multimedia;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;

import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;
import com.rfw.hotkey.util.misc.LoopedExecutor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;

/**
 * Fragment which emulates media controls
 * of a keyboard
 *
 * @author Raheeb Hassan
 * @author Farhan Kabir
 */

public class MultimediaFragment extends Fragment {
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

        playPauseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessageToServer("playPause");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessageToServer("next");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        prevIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessageToServer("prev");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                                try {
                                    sendMessageToServer("volumeUp");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
                                try {
                                    sendMessageToServer("volumeDown");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
                try {
                    sendMessageToServer("mute");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }

    private void sendMessageToServer(String action) throws JSONException {
        try {
            JSONObject packet = new JSONObject();
            packet.put("type", "media");
            packet.put("action", action);
            ConnectionManager.getInstance().sendPacket(packet);
        } catch (JSONException e) {
            Log.e("MediaFragment", "sendMessageToServer: error sending media info", e);
        }
    }
}
