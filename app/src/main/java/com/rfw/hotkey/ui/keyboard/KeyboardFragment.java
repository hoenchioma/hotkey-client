package com.rfw.hotkey.ui.keyboard;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Fragment to show a virtual keyboard to emulate keypresses
 * on the desktop computer
 *
 * @author Shadman Wadith
 */
public class KeyboardFragment extends Fragment implements
        View.OnClickListener,
        View.OnTouchListener {

    private static final String TAG = "KeyboardFragment";

    private static final String ACTION_PRESS = "press";
    private static final String ACTION_RELEASE = "release";
    private static final String ACTION_TYPE = "type";

    private KeyboardView keyboardView;

    private Set<String> modifiers = new HashSet<>(); // set containing the active modifiers
    private Set<String> pressedKeys = new HashSet<>(); // set containing the currently pressed keys

    // saves the corresponding keyword for a button
    private static final SparseArray<String> keyword = new SparseArray<String>() {{
        append(R.id.escButtonID  , "ESC"   );
        append(R.id.tabButtonID  , "TAB"   );
        append(R.id.copyButtonID , "COPY"  );
        append(R.id.pasteButtonID, "PASTE" );
        append(R.id.pgupButtonID , "PGUP"  );
        append(R.id.pgdnButtonID , "PGDN"  );
        append(R.id.upButtonID   , "UP"    );
        append(R.id.leftButtonID , "LEFT"  );
        append(R.id.downButtonID , "DOWN"  );
        append(R.id.rightButtonID, "RIGHT" );
        append(R.id.shiftButtonID, "SHIFT" );
        append(R.id.ctrlButtonID , "CTRL"  );
    }};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_keyboard, container, false);

        init(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardView.performClick();
    }

    /**
     * Initialize the buttons and onclick listeners
     */
    private void init(View rootView) {
        keyboardView = rootView.findViewById(R.id.keyboardView);
        LinearLayout emptySpace = rootView.findViewById(R.id.emptySpace);

        // buttons
        Button copyButton = rootView.findViewById(R.id.copyButtonID);
        Button pasteButton = rootView.findViewById(R.id.pasteButtonID);
        Button escButton = rootView.findViewById(R.id.escButtonID);
        Button tabButton = rootView.findViewById(R.id.tabButtonID);
        Button shiftButton = rootView.findViewById(R.id.shiftButtonID);
        Button ctrlButton = rootView.findViewById(R.id.ctrlButtonID);
        Button pgupButton = rootView.findViewById(R.id.pgupButtonID);
        Button pgdnButton = rootView.findViewById(R.id.pgdnButtonID);
        Button upButton = rootView.findViewById(R.id.upButtonID);
        Button leftButton = rootView.findViewById(R.id.leftButtonID);
        Button downButton = rootView.findViewById(R.id.downButtonID);
        Button rightButton = rootView.findViewById(R.id.rightButtonID);

        // onClick listeners
        copyButton.setOnClickListener(this);
        escButton.setOnClickListener(this);
        tabButton.setOnClickListener(this);
        shiftButton.setOnClickListener(this);
        ctrlButton.setOnClickListener(this);
        pasteButton.setOnClickListener(this);
        pgupButton.setOnClickListener(this);
        pgdnButton.setOnClickListener(this);
//        upButton.setOnClickListener(this);
//        leftButton.setOnClickListener(this);
//        downButton.setOnClickListener(this);
//        rightButton.setOnClickListener(this);

        upButton.setOnTouchListener(this);
        downButton.setOnTouchListener(this);
        leftButton.setOnTouchListener(this);
        rightButton.setOnTouchListener(this);

        // handle raw key presses from soft keyboard
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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.shiftButtonID || id == R.id.ctrlButtonID) {
            toggleModifier(keyword.get(id), (MaterialButton) v); // toggle modifier
        } else {
            sendKey(keyword.get(id)); // send key press
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                sendKeyPress(keyword.get(id));
//                Log.d(TAG, "onTouch: action down on " + keyword.get(id));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                sendKeyRelease(keyword.get(id));
//                Log.d(TAG, "onTouch: action up on " + keyword.get(id));
                break;
        }
        return false; // always return false so the button can handle the event
    }

    private void toggleModifier(@NonNull String keyword, @NonNull MaterialButton button) {
        if (!modifiers.contains(keyword)) { // turn on modifier
            modifiers.add(keyword);

            // highlight button
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

            // if there's a currently pressed key (send modifier change)
            if (!pressedKeys.isEmpty()) {
                sendModifier(keyword, false);
            }
        } else { // turn off modifier
            modifiers.remove(keyword);

            // unhighlight button
            button.setBackgroundTintList(
                    ContextCompat.getColorStateList(
                            Objects.requireNonNull(getContext()),
                            android.R.color.transparent
                    )
            );
            button.setTextColor(
                    ContextCompat.getColorStateList(
                            Objects.requireNonNull(getContext()),
                            R.color.white
                    )
            );

            // if there's a currently pressed key (send modifier change)
            if (!pressedKeys.isEmpty()) {
                sendModifier(keyword, true);
            }
        }
    }

    private void sendKey(@NonNull String key) {
        send(ACTION_TYPE, key, modifiers);
    }

    private void sendKeyPress(@NonNull String key) {
        if (pressedKeys.isEmpty()) {
            send(ACTION_PRESS, key, modifiers);
        } else {
            send(ACTION_PRESS, key, null);
        }
        pressedKeys.add(key);
    }

    private void sendKeyRelease(@NonNull String key) {
        pressedKeys.remove(key);
        if (pressedKeys.isEmpty()) {
            send(ACTION_RELEASE, key, modifiers);
        } else {
            send(ACTION_RELEASE, key, null);
        }
    }

    private void sendModifier(@NonNull String modifier, boolean release) {
        if (!release) {
            send(ACTION_PRESS, null, Collections.singletonList(modifier));
        } else {
            send(ACTION_RELEASE, null, Collections.singletonList(modifier));
        }
    }

    /**
     * sends the message of specified action to Connection Manager
     *
     * @param action the type of action (type, press, release)
     * @param key    key being sent
     * @param modifiers modifiers to send with the packet
     */
    private void send(@NonNull String action, @Nullable String key, @Nullable Collection<String> modifiers) {
        try {
            JSONArray modifierArray = null;
            if (modifiers != null) {
                modifierArray = new JSONArray();
                for (String i : modifiers) modifierArray.put(i);
            }
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
