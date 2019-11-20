package com.rfw.hotkey.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;

import com.rfw.hotkey.R;
import com.rfw.hotkey.util.ui.MyEditTextPref;

import java.util.Objects;

/**
 * Activity for changing various preferences withing the app
 *
 * @author Raheeb Hassan
 */
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // exit with sliding animation to the right (to match with the animation for starting this activity)
        overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slide_out_right);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            // get default global preferences
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));

            MyEditTextPref connectTimeOutPref = findPreference(getString(R.string.settings_key_connect_timeout));
            assert connectTimeOutPref != null;
//            connectTimeOutPref.setText(String.valueOf(Constants.Net.CONNECT_TIMEOUT));

            MyEditTextPref fpsPref = findPreference(getString(R.string.settings_key_live_screen_fps));
            assert fpsPref != null;
//            fpsPref.setText(String.valueOf(Constants.LiveScreen.FPS));

            SeekBarPreference imageQualityPref = findPreference(getString(R.string.settings_key_live_screen_img_quality));
            assert imageQualityPref != null;
//            imageQualityPref.setValue(Constants.LiveScreen.IMAGE_QUALITY);
        }
    }
}