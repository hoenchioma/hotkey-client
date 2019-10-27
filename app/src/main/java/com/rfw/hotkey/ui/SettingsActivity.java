package com.rfw.hotkey.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;

import com.rfw.hotkey.R;
import com.rfw.hotkey.util.ui.MyEditTextPref;

import java.util.Objects;

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