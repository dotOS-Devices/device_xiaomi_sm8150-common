package com.xiaomi.parts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.xiaomi.parts.dirac.DiracUtils;
import com.xiaomi.parts.kcal.Utils;
import com.xiaomi.parts.ambient.SensorsDozeService;
import com.xiaomi.parts.thermal.ThermalUtils;
import com.xiaomi.parts.soundcontrol.SoundControlSettings;
import com.xiaomi.parts.preferences.FileUtils;

public class BootReceiver extends BroadcastReceiver implements Utils {

    private static final String TAG = "PocoParts";

    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

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

        int gain = Settings.Secure.getInt(context.getContentResolver(),
                    SoundControlSettings.PREF_HEADPHONE_GAIN, 4);
            FileUtils.setValue(SoundControlSettings.HEADPHONE_GAIN_PATH, gain + " " + gain);
            FileUtils.setValue(SoundControlSettings.MICROPHONE_GAIN_PATH, Settings.Secure.getInt(context.getContentResolver(),
                    SoundControlSettings.PREF_MICROPHONE_GAIN, 0));
            FileUtils.setValue(SoundControlSettings.SPEAKER_GAIN_PATH, Settings.Secure.getInt(context.getContentResolver(),
                    SoundControlSettings.PREF_SPEAKER_GAIN, 0));
        }

	//Ambient
        context.startService(new Intent(context, SensorsDozeService.class));

        //Thermal
        ThermalUtils.startService(context);

        //Dirac
        try {
        DiracUtils.initialize(context);
        } catch (Exception e) {
            Log.d(TAG, "Dirac is not present in system");
        }

        boolean enabled = sharedPrefs.getBoolean(DeviceSettings.PREF_KEY_FPS_INFO, false);
        if (enabled) {
            context.startService(new Intent(context, FPSInfoService.class));
        }
    }
}
