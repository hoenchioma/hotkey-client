package com.rfw.hotkey.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;


public class KeyboardFragment extends Fragment
        implements View.OnClickListener, View.OnTouchListener {

    private static final String TAG = KeyboardFragment.class.getCanonicalName();

    private View contextView;
    private Button copyButton, escButton, homeButton, tabButton, pasteButton,
            pgupButton, shiftButton, upButton, pgdnButton, leftButton, downButton, righButton;
    private String previousText = "";
    private EditText typeHere;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_keyboard, container, false);
        initialization(rootView);

        typeHere.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    char ch = newCharacter(s, previousText);
                    if (ch == 0) return;
                    sendMessageToServer(String.valueOf(ch), "char");
                    //sendMessageToServer(Character.toString(ch));
                    //MainActivity.sendMessageToServer("TYPE_CHARACTER");
                    //MainActivity.sendMessageToServer(Character.toString(ch));
                    previousText = s.toString();
                }
            }


            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }

    private void initialization(View rootView) {
        typeHere = rootView.findViewById(R.id.keyboardInputID);
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
        //homeButton.setOnTouchListener(this);
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

        shiftButton.setOnTouchListener(this);
        rootView.setOnTouchListener(this);
    }

    private char newCharacter(CharSequence currentText, CharSequence previousText) {
        char ch = 0;
        int currentTextLength = currentText.length();
        int previousTextLength = previousText.length();
        int difference = currentTextLength - previousTextLength;
        if (currentTextLength > previousTextLength) {
            if (1 == difference) {
                ch = currentText.charAt(previousTextLength);
            }
        } else if (currentTextLength < previousTextLength) {
            if (-1 == difference) {
                ch = '\b';
            } else {
                ch = ' ';
            }
        }
        return ch;
    }


    @Override
    public void onClick(View view) {

//        int id = view.getId();
//        if (id == R.id.escButtonID) {
//            //sendMessageToServer("TYPE_MODIFIER");
//            sendMessageToServer("ESC","TYPE_MODIFIER");
//            Log.d("onclick", "ESC");
//        }
//        if (id == R.id.copyButtonID) {
//            //sendMessageToServer("TYPE_COMMAND");
//            sendMessageToServer("COPY","TYPE_COMMAND");
//            Log.d("onclick", "COPY");
//        }
//        if (id == R.id.homeButtonID) {
//           // sendMessageToServer("TYPE_MODIFIER");
//            sendMessageToServer("HOME","TYPE_MODIFIER");
//            Log.d("onclick", "HOME");
//        }
//        if (id == R.id.tabButtonID) {
//            //sendMessageToServer("TYPE_MODIFIER");
//            sendMessageToServer("TAB","TYPE_MODIFIER");
//            Log.d("onclick", "TAB");
//        }
//        if (id == R.id.pasteButtonID) {
//            //sendMessageToServer("TYPE_COMMAND");
//            sendMessageToServer("PASTE","TYPE_COMMAND");
//            Log.d("onclick", "PASTE");
//        }
//        if (id == R.id.pgupButtonID) {
//           // sendMessageToServer("TYPE_MODIFIER");
//            sendMessageToServer("PGUP","TYPE_MODIFIER");
//            Log.d("onclick", "PGUP");
//        }
//        /*if(id == R.id.shiftButtonID){
//            sendMessageToServer("TYPE_HOLD");
//            sendMessageToServer("SHIFT");
//            Log.d("onclick","SHIFT");
//        }*/
//        if (id == R.id.upButtonID) {
//           // sendMessageToServer("TYPE_MODIFIER");
//            sendMessageToServer("UP","TYPE_MODIFIER");
//            Log.d("onclick", "UP");
//        }
//        if (id == R.id.pgdnButtonID) {
//           // sendMessageToServer("TYPE_MODIFIER");
//            sendMessageToServer("PGDN","TYPE_MODIFIER");
//            Log.d("onclick", "PGDN");
//        }
//        if (id == R.id.leftButtonID) {
//            //sendMessageToServer("TYPE_MODIFIER");
//            sendMessageToServer("LEFT","TYPE_MODIFIER");
//            Log.d("onclick", "LEFT");
//        }
//        if (id == R.id.downButtonID) {
//           // sendMessageToServer("TYPE_MODIFIER");
//            sendMessageToServer("DOWN","TYPE_MODIFIER");
//            Log.d("onclick", "DOWN");
//        }
//        if (id == R.id.rightButtonID) {
//            //sendMessageToServer("TYPE_MODIFIER");
//            sendMessageToServer("RIGHT","TYPE_MODIFIER");
//            Log.d("onclick", "RIGHT");
//        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // TODO: (Wadith) fix send to server method
//        String action = "KEY_PRESS";
//        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//            action = "KEY_PRESS";
//            Log.d("Pressed", "Kaj kore");
//        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//            action = "KEY_RELEASE";
//        }
//        int keyCode = 17; //dummy initialization
//        switch (view.getId()) {
//            case R.id.shiftButtonID:
//                Log.d("keyboardFragmentPress", "shift");
//                break;
//            // TODO CTRL,ALT
//
//        }
//        //Log.d("ONTOUCH", Integer.toString(keyCode));
//        //System.out.println(keyCode);
//        //sendKeyCodeToServer(action, keyCode);
        return false;
    }

    /**
     * sends the message of specified action to Connection Manager
     * @param message message (key press type)
     * @param action type of the message
     */
    private void sendMessageToServer(String message, String action) {
        JSONObject packet = new JSONObject();

        try {
            packet.put("type", "keyboard");
            packet.put("action", action);
            packet.put("key", message);

            new ConnectionManager.SendTask(packet).execute();

        } catch (JSONException e) {
            Log.e("KeyboardFragment", "sendMessageToServer: error sending key-press", e);
        }
    }

/*    public void onTextChanged(CharSequence s, int start, int before, int count) {
        char ch = newCharacter(s, previousText);
        if (ch == 0) {
            return;
        }
        Log.d("ONTEXTCHANGE", String.valueOf(ch));

        //MainActivity.sendMessageToServer("TYPE_CHARACTER");
        //MainActivity.sendMessageToServer(Character.toString(ch));
        previousText = s.toString();
    }*/
}
