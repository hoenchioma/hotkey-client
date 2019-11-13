package com.rfw.hotkey.ui.live_screen;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.preference.PreferenceManager;

import com.rfw.hotkey.R;
import com.rfw.hotkey.live_screen.LiveScreenReceiver;
import com.rfw.hotkey.live_screen.WiFiLiveScreenReceiver;
import com.rfw.hotkey.net.Connection;
import com.rfw.hotkey.net.ConnectionManager;
import com.rfw.hotkey.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import static com.rfw.hotkey.util.Utils.getFloatPref;
import static com.rfw.hotkey.util.Utils.getIntPref;

/**
 * An example full-live_screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LiveScreenActivity extends AppCompatActivity {
    private static final String TAG = "LiveScreenActivity";

    private float mouseInitX = 0;
    private float mouseInitY = 0;
    private float mouseDisX;
    private float mouseDisY;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private LiveScreenView mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private LiveScreenReceiver liveScreenReceiver;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live_screen);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        // close activity if connection type is not wifi or is not connected
        if (ConnectionManager.getInstance().getConnectionType() != Connection.Type.WIFI) {
            Toast.makeText(getApplicationContext(),
                    ConnectionManager.getInstance().isConnectionActive() ? R.string.live_screen_only_wifi : R.string.not_connected_msg,
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // initiate live screen receiver
        liveScreenReceiver = new WiFiLiveScreenReceiver(this) {
            @Override
            public void onFrameReceive(@NonNull Bitmap bitmap) {
                mContentView.updateBitMap(bitmap);
            }

            @Override
            public void onError(@Nullable Exception e, boolean isFatal) {
                if (e != null && e.getMessage() != null && !e.getMessage().isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                            e.getMessage(), Toast.LENGTH_SHORT).show());
                }
                if (isFatal) finish();
            }
        };

        // mouse control logic
        mContentView.setOnTouchListener((v, event) -> {
            if (event.getPointerCount() > 1) {
                //Toast.makeText(getContext(),"RightClick", Toast.LENGTH_SHORT).show();
                try {
                    sendToServer("RightClick", 0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    mouseInitX = event.getX();
                    mouseInitY = event.getY();
                    mouseDisX = 0;
                    mouseDisY = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    mouseDisX = event.getX() - mouseInitX;
                    mouseDisY = event.getY() - mouseInitY;
                    mouseInitX = event.getX();
                    mouseInitY = event.getY();
                    try {
                        sendToServer("TouchpadMove", (int) mouseDisX, (int) mouseDisY);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    if (mouseDisX == 0 && mouseDisY == 0) {
                        try {
                            sendToServer("LeftClick", 0, 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
            return true;
        });

        // make screen orientation landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

            float fps = getFloatPref(sharedPref, getString(R.string.settings_key_live_screen_fps), Constants.LiveScreen.FPS);
            float compressRatio = getIntPref(sharedPref, getString(R.string.settings_key_live_screen_img_quality),
                    Constants.LiveScreen.IMAGE_QUALITY) / 100.0f; // compress ratio = image quality / 100.0

            // start when activity is visible
            mContentView.post(() -> liveScreenReceiver.start(mContentView.getWidth(), mContentView.getHeight(), fps, compressRatio));
        } catch (Exception e) {
            Log.e(TAG, "onResume: error getting fps and compress ratio from shared pref", e);
            mContentView.post(() -> liveScreenReceiver.start(mContentView.getWidth(), mContentView.getHeight()));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop when activity is paused/stopped
        liveScreenReceiver.stop();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    private void sendToServer(String action, int moveX, int moveY) throws JSONException {
        JSONObject packet = new JSONObject();
        packet.put("type", "mouse");
        switch (action) {
            case "TouchpadMove":
                try {
                    packet.put("action", action);
                    packet.put("deltaX", moveX);
                    packet.put("deltaY", moveY);
                } catch (JSONException e) {
                    Log.e("MouseFragment", "sendToServer: error sending mouse movement", e);
                }
                break;
            case "RightClick":
                try {
                    packet.put("action", action);
                } catch (JSONException e) {
                    Log.e("MouseFragment", "sendToServer: error sending right click", e);
                }
                break;
            case "LeftClick":
                try {
                    packet.put("action", action);
                } catch (JSONException e) {
                    Log.e("MouseFragment", "sendToServer: error sending left click", e);
                }
                break;
            case "ScrollMove":
                try {
                    packet.put("action", action);
                    packet.put("deltaY", moveY);
                } catch (JSONException e) {
                    Log.e("MouseFragment", "sendToServer: error sending scroll movement", e);
                }
                break;
            case "ScrollClick":
                try {
                    packet.put("action", action);
                } catch (JSONException e) {
                    Log.e("MouseFragment", "sendToServer: error sending scroll click", e);
                }
                break;

        }
        ConnectionManager.getInstance().sendPacket(packet);
    }
}
