package com.rfw.hotkey.ui.keyboard;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Fragment to show a virtual keyboard to emulate keypresses
 * on the desktop computer
 *
 * @author Shadman Wadith
 */
public class KeyboardFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = KeyboardFragment.class.getCanonicalName();

    private KeyboardView keyboardView;

    private Button copyButton;
    private Button escButton;
    private Button tabButton;
    private Button pasteButton;
    private Button pgupButton;
    private Button shiftButton;
    private Button ctrlButton;
    private Button upButton;
    private Button pgdnButton;
    private Button leftButton;
    private Button downButton;
    private Button righButton;

    private LinearLayout emptySpace;

    private Set<String> modifiers = new HashSet<>();

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
                        sendKey(String.valueOf('\b'));
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) { // enter
                        sendKey(String.valueOf('\n'));
                        return true;
                    }
                } else { // text character
                    sendKey(String.valueOf((char) event.getUnicodeChar()));
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

        // buttons
        copyButton = rootView.findViewById(R.id.copyButtonID);
        pasteButton = rootView.findViewById(R.id.pasteButtonID);
        escButton = rootView.findViewById(R.id.escButtonID);
        tabButton = rootView.findViewById(R.id.tabButtonID);
        shiftButton = rootView.findViewById(R.id.shiftButtonID);
        ctrlButton = rootView.findViewById(R.id.ctrlButtonID);
        pgupButton = rootView.findViewById(R.id.pgupButtonID);
        pgdnButton = rootView.findViewById(R.id.pgdnButtonID);
        upButton = rootView.findViewById(R.id.upButtonID);
        leftButton = rootView.findViewById(R.id.leftButtonID);
        downButton = rootView.findViewById(R.id.downButtonID);
        righButton = rootView.findViewById(R.id.rightButtonID);

        // onClick listeners
        copyButton.setOnClickListener(this);
        escButton.setOnClickListener(this);
        tabButton.setOnClickListener(this);
        shiftButton.setOnClickListener(this);
        ctrlButton.setOnClickListener(this);
        pasteButton.setOnClickListener(this);
        pgupButton.setOnClickListener(this);
        upButton.setOnClickListener(this);
        pgdnButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        downButton.setOnClickListener(this);
        righButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.escButtonID:   sendKey("ESC");   break;
            case R.id.tabButtonID:   sendKey("TAB");   break;
            case R.id.copyButtonID:  sendKey("COPY");  break;
            case R.id.pasteButtonID: sendKey("PASTE"); break;
            case R.id.pgupButtonID:  sendKey("PGUP");  break;
            case R.id.pgdnButtonID:  sendKey("PGDN");  break;
            case R.id.upButtonID:    sendKey("UP");    break;
            case R.id.leftButtonID:  sendKey("LEFT");  break;
            case R.id.downButtonID:  sendKey("DOWN");  break;
            case R.id.rightButtonID: sendKey("RIGHT"); break;
            case R.id.shiftButtonID:
                toggleModifier("SHIFT", (MaterialButton) view);
                break;
            case R.id.ctrlButtonID:
                toggleModifier("CTRL", (MaterialButton) view);
                break;
            default:
        }
    }

    private void toggleModifier(String keyword, MaterialButton button) {
        if (!modifiers.contains(keyword)) {
            modifiers.add(keyword);
            button.setBackgroundTintList(
                    ContextCompat.getColorStateList(
                            Objects.requireNonNull(getContext()),
                            R.color.white
                    )
            );
            button.setTextColor(
                    ContextCompat.getColorStateList(
                            Objects.requireNonNull(getContext()),
                            R.color.colorAccent
                    )
            );
        } else {
            modifiers.remove(keyword);
            button.setBackgroundTintList(
                    ContextCompat.getColorStateList(
                            Objects.requireNonNull(getContext()),
                            R.color.colorAccent
                    )
            );
            button.setTextColor(
                    ContextCompat.getColorStateList(
                            Objects.requireNonNull(getContext()),
                            R.color.white
                    )
            );
        }
    }

    private void sendKey(String key) {
        send("type", key);
    }

    private void sendKeyPress(String key) {
        send("press", key);
    }

    private void sendKeyRelease(String key) {
        send("release", key);
    }

    /**
     * sends the message of specified action to Connection Manager
     *
     * @param action the type of action (type, press, release)
     * @param key    key being sent
     */
    private void send(String action, String key) {
        try {
            JSONArray modifierArray = new JSONArray();
            for (String i: modifiers) modifierArray.put(i);
            JSONObject packet = new JSONObject()
                    .put("type", "keyboard")
                    .put("action", action)
                    .put("key", key)
                    .put("modifiers", modifierArray);
            ConnectionManager.getInstance().sendPacket(packet);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
