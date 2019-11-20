package com.rfw.hotkey.ui.multimedia;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;


public class MultimediaFragment extends Fragment {
    private static final String TAG = "MultimediaFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_multimedia, container, false);

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

        volumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessageToServer("volumeUp");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        volumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessageToServer("volumeDown");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    /**
     * Method to be invoked by dispatchKeyEvent from enclosing activity
     * (return null means not handled)
     */
    public Boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                try {
                    sendMessageToServer("volumeUp");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                try {
                    sendMessageToServer("volumeDown");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return null;
        }
    }

    private void sendMessageToServer(String action) throws JSONException {
        try{
            JSONObject packet = new JSONObject();
            packet.put("type", "media");
            packet.put("action", action);
            ConnectionManager.getInstance().sendPacket(packet);
        }catch (JSONException e){
            Log.e("MediaFragment", "sendMessageToServer: error sending media info", e);
        }
    }
}
