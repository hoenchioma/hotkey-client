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


public class KeyboardFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    // TODO: change the log statement tags

    private View contextView;
    private Button copyButton;
    private String previousText = "";
    private EditText typeHere;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_keyboard, container, false);
        initialization(rootView);
        //Log.d("HALALA", "It works");
        typeHere.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.equals("")) {
                    //do your work here
                    char ch = newCharacter(s, previousText);
                    if (ch == 0) {
                        return;
                    }
                    Log.d("ONTEXTCHANGE", String.valueOf(ch));
                    Log.d("ONTEXTCHANGE", String.valueOf((int) ch));
                    sendMessageToServer("TYPE_CHARACTER");
                    sendMessageToServer(String.valueOf(ch));
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
        //System.out.println("Check");
        // Inflate the layout for this fragment
        return rootView;
    }

    private void initialization(View rootView) {

        typeHere = rootView.findViewById(R.id.keyboardInputID);
        copyButton = rootView.findViewById(R.id.copyButtonID);
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
        // Log.d("NEWCHARACTER", String.valueOf(ch));
        return ch;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.copyButtonID) {
            Log.d("onclick", "copy");
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        String action = "KEY_PRESS";
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            action = "KEY_PRESS";
            Log.d("Pressed", "Kaj kore"); // kaj korena
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            action = "KEY_RELEASE";
        }
        int keyCode = 17;//dummy initialization
        switch (view.getId()) {

            // TODO CTRL,ALT
            /* Example
            case com.example.fragmenttest.R.id.CTRLButton :
                keyCode = 17;
                break;
            */

        }
        Log.d("ONTOUCH", Integer.toString(keyCode));
        //System.out.println(keyCode);
        //sendKeyCodeToServer(action, keyCode);
        return false;
    }

    public void sendMessageToServer(String message) {
        //TODO
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
