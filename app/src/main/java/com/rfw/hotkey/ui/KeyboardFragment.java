package com.rfw.hotkey.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.rfw.hotkey.R;


public class KeyboardFragment extends Fragment implements View.OnClickListener , View.OnTouchListener {

    private View contextView;
    private Button copyButton,escButton,homeButton,tabButton,pasteButton,pgupButton,shiftButton,upButton,pgdnButton,leftButton,downButton,righButton;
    private String previousText = "";
    private EditText typeHere;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
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
                    Log.d("ONTEXTCHANGE", Character.toString(ch));
                    Log.d("ONTEXTCHANGE", String.valueOf((int)ch));
                    sendMessageToServer("TYPE_CHARACTER");
                    sendMessageToServer(String.valueOf((int)ch));
                    //sendMessageToServer(Character.toString(ch));
                    //MainActivity.sendMessageToServer("TYPE_CHARACTER");
                    //MainActivity.sendMessageToServer(Character.toString(ch));
                    previousText = s.toString();
                }
            }


            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });
        //System.out.println("Check");
        // Inflate the layout for this fragment
        return rootView;
    }

    private void initialization(View rootView){

        typeHere = (EditText) rootView.findViewById(R.id.keyboardInputID);
        copyButton = (Button) rootView.findViewById(R.id.copyButtonID);
        escButton = (Button) rootView.findViewById(R.id.escButtonID);
        homeButton = (Button) rootView.findViewById(R.id.homeButtonID);
        tabButton = (Button) rootView.findViewById(R.id.tabButtonID);
        pasteButton = (Button) rootView.findViewById(R.id.pasteButtonID);
        pgupButton = (Button) rootView.findViewById(R.id.pgupButtonID);
        shiftButton = (Button) rootView.findViewById(R.id.shiftButtonID);
        upButton = (Button) rootView.findViewById(R.id.upButtonID);
        pgdnButton = (Button) rootView.findViewById(R.id.pgdnButtonID);
        leftButton = (Button) rootView.findViewById(R.id.leftButtonID);
        downButton = (Button) rootView.findViewById(R.id.downButtonID);
        righButton = (Button) rootView.findViewById(R.id.rightButtonID);
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
       // Log.d("NEWCHARACTER", String.valueOf(ch));
        return ch;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.escButtonID){
            sendMessageToServer("TYPE_MODIFIER");
            sendMessageToServer("ESC");
            Log.d("onclick","ESC");
        }
        if(id == R.id.copyButtonID){
            sendMessageToServer("TYPE_COMMAND");
            sendMessageToServer("COPY");
            Log.d("onclick","COPY");
        }
        if(id == R.id.homeButtonID){
            sendMessageToServer("TYPE_MODIFIER");
            sendMessageToServer("HOME");
            Log.d("onclick","HOME");
        }
        if(id == R.id.tabButtonID){
            sendMessageToServer("TYPE_MODIFIER");
            sendMessageToServer("TAB");
            Log.d("onclick","TAB");
        }
        if(id == R.id.pasteButtonID){
            sendMessageToServer("TYPE_COMMAND");
            sendMessageToServer("PASTE");
            Log.d("onclick","PASTE");
        }
        if(id == R.id.pgupButtonID){
            sendMessageToServer("TYPE_MODIFIER");
            sendMessageToServer("PGUP");
            Log.d("onclick","PGUP");
        }
        /*if(id == R.id.shiftButtonID){
            sendMessageToServer("TYPE_HOLD");
            sendMessageToServer("SHIFT");
            Log.d("onclick","SHIFT");
        }*/
        if(id == R.id.upButtonID){
            sendMessageToServer("TYPE_MODIFIER");
            sendMessageToServer("UP");
            Log.d("onclick","UP");
        }
        if(id == R.id.pgdnButtonID){
            sendMessageToServer("TYPE_MODIFIER");
            sendMessageToServer("PGDN");
            Log.d("onclick","PGDN");
        }
        if(id == R.id.leftButtonID){
            sendMessageToServer("TYPE_MODIFIER");
            sendMessageToServer("LEFT");
            Log.d("onclick","LEFT");
        }
        if(id == R.id.downButtonID){
            sendMessageToServer("TYPE_MODIFIER");
            sendMessageToServer("DOWN");
            Log.d("onclick","DOWN");
        }
        if(id == R.id.rightButtonID){
            sendMessageToServer("TYPE_MODIFIER");
            sendMessageToServer("RIGHT");
            Log.d("onclick","RIGHT");
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        String action = "KEY_PRESS";
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            action = "KEY_PRESS";
            Log.d("Pressed","Kaj kore");
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            action = "KEY_RELEASE";
        }
        int keyCode = 17;//dummy initialization
        switch (view.getId()) {
            case R.id.shiftButtonID:
                Log.d("keyboardFragmentPress","shift");
                break;
            // TODO CTRL,ALT

        }
        //Log.d("ONTOUCH", Integer.toString(keyCode));
        //System.out.println(keyCode);
        //sendKeyCodeToServer(action, keyCode);
        return false;
    }

    public void sendMessageToServer(String message)
    {
        //TODO Raheeb
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
