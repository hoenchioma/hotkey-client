package com.rfw.hotkey.ui.keyboard;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Fragment to show a virtual keyboard to emulate keypresses
 * on the desktop computer
 *
 * @author Shadman Wadith
 */
public class KeyboardFragment extends Fragment implements View.OnClickListener ,View.OnLongClickListener{

    private static final String TAG = KeyboardFragment.class.getCanonicalName();

    private KeyboardView keyboardView;

    private ImageButton copyButton;
    private ImageButton escButton;
    private ImageButton homeButton;
    private ImageButton tabButton;
    private ImageButton pasteButton;
    private ImageButton pgupButton;
    private ImageButton shiftButton;
    private ImageButton upButton;
    private ImageButton pgdnButton;
    private ImageButton leftButton;
    private ImageButton downButton;
    private ImageButton righButton;

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
//        pgupButton = rootView.findViewById(R.id.pgupButtonID);
//        shiftButton = rootView.findViewById(R.id.shiftButtonID);
        upButton = rootView.findViewById(R.id.upButtonID);
//        pgdnButton = rootView.findViewById(R.id.pgdnButtonID);
        leftButton = rootView.findViewById(R.id.leftButtonID);
        downButton = rootView.findViewById(R.id.downButtonID);
        righButton = rootView.findViewById(R.id.rightButtonID);
        copyButton.setOnClickListener(this);
//      homeButton.setOnTouchListener(this);
        homeButton.setOnClickListener(this);
        escButton.setOnClickListener(this);
        tabButton.setOnClickListener(this);
        pasteButton.setOnClickListener(this);
 //     pgupButton.setOnClickListener(this);
//      pgdnButton.setOnClickListener(this);
        upButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        downButton.setOnClickListener(this);
        righButton.setOnClickListener(this);
//      shiftButton.setOnTouchListener(this);
//      rootView.setOnTouchListener(this);
    
        copyButton.setOnLongClickListener(this);
//      homeButton.setOnTouchListener(this);
        homeButton.setOnLongClickListener(this);
        escButton.setOnLongClickListener(this);
        tabButton.setOnLongClickListener(this);
        pasteButton.setOnLongClickListener(this);
        //     pgupButton.setOnLongClickListener(this);
//      pgdnButton.setOnLongClickListener(this);
        upButton.setOnLongClickListener(this);
        leftButton.setOnLongClickListener(this);
        downButton.setOnLongClickListener(this);
        righButton.setOnLongClickListener(this);
//      shiftButton.setOnTouchListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.escButtonID:
                sendKeyToServer("modifier", "ESC");
                Log.d("onclick", "ESC");
                Toast.makeText(getActivity(), "ESC", Toast.LENGTH_SHORT).show();
                break;
            case R.id.copyButtonID:
                sendKeyToServer("command", "COPY");
                Toast.makeText(getActivity(), "Copy", Toast.LENGTH_SHORT).show();
                Log.d("onclick", "COPY");
                break;
            case R.id.homeButtonID:
                sendKeyToServer("modifier", "HOME");
                Toast.makeText(getActivity(), "Home", Toast.LENGTH_SHORT).show();
                Log.d("onclick", "HOME");
                break;
            case R.id.tabButtonID:
                sendKeyToServer("modifier", "TAB");
                Toast.makeText(getActivity(), "TAB", Toast.LENGTH_SHORT).show();
                Log.d("onclick", "TAB");
                break;
            case R.id.pasteButtonID:
                sendKeyToServer("command", "PASTE");
                Toast.makeText(getActivity(), "Paste", Toast.LENGTH_SHORT).show();
                Log.d("onclick", "PASTE");
                break;
//            case R.id.pgupButtonID:
//                sendKeyToServer("modifier", "PGUP");
//                Log.d("onclick", "PGUP");
//                break;
//            case R.id.shiftButtonID:
//                sendKeyToServer("TYPE_HOLD");
//                sendKeyToServer("SHIFT");
//                Log.d("onclick", "SHIFT");
//                break;
            case R.id.upButtonID:
                sendKeyToServer("modifier", "UP");
                Toast.makeText(getActivity(), "Up Arrow", Toast.LENGTH_SHORT).show();
                Log.d("onclick", "UP");
                break;
//            case R.id.pgdnButtonID:
//                sendKeyToServer("modifier", "PGDN");
//                Log.d("onclick", "PGDN");
//                break;
            case R.id.leftButtonID:
                sendKeyToServer("modifier", "LEFT");
                Toast.makeText(getActivity(), "Left Arrow", Toast.LENGTH_SHORT).show();
                Log.d("onclick", "LEFT");
                break;
            case R.id.downButtonID:
                sendKeyToServer("modifier", "DOWN");
                Toast.makeText(getActivity(), "Down Arrow", Toast.LENGTH_SHORT).show();
                Log.d("onclick", "DOWN");
                break;
            case R.id.rightButtonID:
                sendKeyToServer("modifier", "RIGHT");
                Toast.makeText(getActivity(), "Right Arrow", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onLongClick(View view) {

        switch (view.getId()) {
            case R.id.escButtonID:
                Toast.makeText(getActivity(), "ESC", Toast.LENGTH_SHORT).show();
                break;
            case R.id.copyButtonID:
                Toast.makeText(getActivity(), "Copy", Toast.LENGTH_SHORT).show();
                break;
            case R.id.homeButtonID:
                Toast.makeText(getActivity(), "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tabButtonID:
                Toast.makeText(getActivity(), "TAB", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pasteButtonID:
                Toast.makeText(getActivity(), "Paste", Toast.LENGTH_SHORT).show();
                break;
//            case R.id.pgupButtonID:
//                 break;
//            case R.id.shiftButtonID:
//
//                break;
            case R.id.upButtonID:
                Toast.makeText(getActivity(), "Up Arrow", Toast.LENGTH_SHORT).show();

                break;
//            case R.id.pgdnButtonID:
//                sendKeyToServer("modifier", "PGDN");
//                Log.d("onclick", "PGDN");
//                break;
            case R.id.leftButtonID:
                Toast.makeText(getActivity(), "Left Arrow", Toast.LENGTH_SHORT).show();
                break;
            case R.id.downButtonID:
                Toast.makeText(getActivity(), "Down Arrow", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rightButtonID:
                Toast.makeText(getActivity(), "Right Arrow", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }
}
