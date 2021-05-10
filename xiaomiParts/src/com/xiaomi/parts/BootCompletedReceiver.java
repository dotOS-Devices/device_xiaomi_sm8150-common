package com.xiaomi.parts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.xiaomi.parts.utils.FileUtils;
import com.xiaomi.parts.dirac.DiracUtils;
import com.xiaomi.parts.thermal.ThermalUtils;
import com.xiaomi.parts.soundcontrol.SoundControlSettings;
import com.xiaomi.parts.touchsampling.TouchSamplingUtils;

public class BootCompletedReceiver extends BroadcastReceiver implements Controller {

    private static final boolean DEBUG = false;
    private static final String TAG = "XiaomiParts";

    public void onReceive(Context context, Intent intent) {
        if (DEBUG) Log.d(TAG, "Received boot completed intent");
        DiracUtils.initialize(context);
        ThermalUtils.startService(context);
        TouchSamplingUtils.restoreSamplingValue(context);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int gain = Settings.Secure.getInt(context.getContentResolver(),
                    SoundControlSettings.PREF_HEADPHONE_GAIN, 4);
            FileUtils.setValue(SoundControlSettings.HEADPHONE_GAIN_PATH, gain + " " + gain);
            FileUtils.setValue(SoundControlSettings.MICROPHONE_GAIN_PATH, Settings.Secure.getInt(context.getContentResolver(),
                    SoundControlSettings.PREF_MICROPHONE_GAIN, 0));
            FileUtils.setValue(SoundControlSettings.SPEAKER_GAIN_PATH, Settings.Secure.getInt(context.getContentResolver(),
                    SoundControlSettings.PREF_SPEAKER_GAIN, 0));

        if (Settings.Secure.getInt(context.getContentResolver(), PREF_ENABLED, 0) == 1) {
                FileUtils.setValue(KCAL_ENABLE, Settings.Secure.getInt(context.getContentResolver(),
                        PREF_ENABLED, 0));
            
                String rgbValue = Settings.Secure.getInt(context.getContentResolver(),
                        PREF_RED, RED_DEFAULT) + " " +
                Settings.Secure.getInt(context.getContentResolver(), PREF_GREEN,
                        GREEN_DEFAULT) + " " +
                Settings.Secure.getInt(context.getContentResolver(), PREF_BLUE,
                        BLUE_DEFAULT);
            
                FileUtils.setValue(KCAL_RGB, rgbValue);
                FileUtils.setValue(KCAL_MIN, Settings.Secure.getInt(context.getContentResolver(),
                        PREF_MINIMUM, MINIMUM_DEFAULT));
                FileUtils.setValue(KCAL_SAT, Settings.Secure.getInt(context.getContentResolver(),
                        PREF_GRAYSCALE, 0) == 1 ? 128 :
                        Settings.Secure.getInt(context.getContentResolver(),
                        PREF_SATURATION, SATURATION_DEFAULT) + SATURATION_OFFSET);
                FileUtils.setValue(KCAL_VAL, Settings.Secure.getInt(context.getContentResolver(),
                        PREF_VALUE, VALUE_DEFAULT) + VALUE_OFFSET);
                FileUtils.setValue(KCAL_CONT, Settings.Secure.getInt(context.getContentResolver(),
                        PREF_CONTRAST, CONTRAST_DEFAULT) + CONTRAST_OFFSET);
                FileUtils.setValue(KCAL_HUE, Settings.Secure.getInt(context.getContentResolver(),
                        PREF_HUE, HUE_DEFAULT));

        }
    }
}
