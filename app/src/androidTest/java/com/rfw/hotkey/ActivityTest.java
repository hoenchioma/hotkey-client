package com.rfw.hotkey;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.rfw.hotkey.ui.SettingsActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ActivityTest {
    @Test
    public void settingsTest() throws InterruptedException {
        ActivityScenario.launch(SettingsActivity.class);
        Thread.sleep(Long.MAX_VALUE);
    }
}
