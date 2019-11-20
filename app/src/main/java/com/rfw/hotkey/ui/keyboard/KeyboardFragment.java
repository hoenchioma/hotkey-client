package com.rfw.hotkey.ui.keyboard;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Shadman Wadith
 */

public class KeyboardFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = KeyboardFragment.class.getCanonicalName();

    private KeyboardView keyboardView;

    private Button copyButton;
    private Button escButton;
    private Button homeButton;
    private Button tabButton;
    private Button pasteButton;
    private Button pgupButton;
    private Button shiftButton;
    private Button upButton;
    private Button pgdnButton;
    private Button leftButton;
    private Button downButton;
    private Button righButton;

    private LinearLayout emptySpace;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_keyboard, container, false);
        init(rootView);

        // handle key presses
        keyboardView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.getUnicodeChar() == 0) { // control character
                    if (keyCode == KeyEvent.KEYCODE_DEL) { // backspace
                        sendKeyToServer("char", String.valueOf('\b'));
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) { // enter
                        sendKeyToServer("char", String.valueOf('\n'));
                        return true;
                    }
                } else { // text character
                    sendKeyToServer("char", String.valueOf((char) event.getUnicodeChar()));
                    return true;
                }
            }
            return false;
        });

        emptySpace.setOnClickListener(v -> keyboardView.performClick());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardView.performClick();
    }

    private void init(View rootView) {
        keyboardView = rootView.findViewById(R.id.keyboardView);

        emptySpace = rootView.findViewById(R.id.emptySpace);

        copyButton = rootView.findViewById(R.id.copyButtonID);
        escButton = rootView.findViewById(R.id.escButtonID);
        homeButton = rootView.findViewById(R.id.homeButtonID);
        tabButton = rootView.findViewById(R.id.tabButtonID);
        pasteButton = rootView.findViewById(R.id.pasteButtonID);
        pgupButton = rootView.findViewById(R.id.pgupButtonID);
        shiftButton = rootView.findViewById(R.id.shiftButtonID);
        upButton = rootView.findViewById(R.id.upButtonID);
        pgdnButton = rootView.findViewById(R.id.pgdnButtonID);
        leftButton = rootView.findViewById(R.id.leftButtonID);
        downButton = rootView.findViewById(R.id.downButtonID);
        righButton = rootView.findViewById(R.id.rightButtonID);
        copyButton.setOnClickListener(this);
//      homeButton.setOnTouchListener(this);
        homeButton.setOnClickListener(this);
        escButton.setOnClickListener(this);
        tabButton.setOnClickListener(this);
        pasteButton.setOnClickListener(this);
        pgupButton.setOnClickListener(this);
        upButton.setOnClickListener(this);
        pgdnButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        downButton.setOnClickListener(this);
        righButton.setOnClickListener(this);
//      shiftButton.setOnTouchListener(this);
//      rootView.setOnTouchListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.escButtonID:
                sendKeyToServer("modifier", "ESC");
                Log.d("onclick", "ESC");
                break;
            case R.id.copyButtonID:
                sendKeyToServer("command", "COPY");
                Log.d("onclick", "COPY");
                break;
            case R.id.homeButtonID:
                sendKeyToServer("modifier", "HOME");
                Log.d("onclick", "HOME");
                break;
            case R.id.tabButtonID:
                sendKeyToServer("modifier", "TAB");
                Log.d("onclick", "TAB");
                break;
            case R.id.pasteButtonID:
                sendKeyToServer("command", "PASTE");
                Log.d("onclick", "PASTE");
                break;
            case R.id.pgupButtonID:
                sendKeyToServer("modifier", "PGUP");
                Log.d("onclick", "PGUP");
                break;
//            case R.id.shiftButtonID:
//                sendKeyToServer("TYPE_HOLD");
//                sendKeyToServer("SHIFT");
//                Log.d("onclick", "SHIFT");
//                break;
            case R.id.upButtonID:
                sendKeyToServer("modifier", "UP");
                Log.d("onclick", "UP");
                break;
            case R.id.pgdnButtonID:
                sendKeyToServer("modifier", "PGDN");
                Log.d("onclick", "PGDN");
                break;
            case R.id.leftButtonID:
                sendKeyToServer("modifier", "LEFT");
                Log.d("onclick", "LEFT");
                break;
            case R.id.downButtonID:
                sendKeyToServer("modifier", "DOWN");
                Log.d("onclick", "DOWN");
                break;
            case R.id.rightButtonID:
                sendKeyToServer("modifier", "RIGHT");
                Log.d("onclick", "RIGHT");
                break;
        }
    }

    /**
     * sends the message of specified action to Connection Manager
     *
     * @param action type of the key being sent (char, modifier, etc.)
     * @param key    key being sent
     */
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
