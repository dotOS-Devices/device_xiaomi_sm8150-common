package com.xiaomi.parts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.xiaomi.parts.dirac.DiracUtils;
import com.xiaomi.parts.thermal.ThermalUtils;
import com.xiaomi.parts.soundcontrol.SoundControlSettings;
import com.xiaomi.parts.preferences.FileUtils;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final boolean DEBUG = false;
    private static final String TAG = "XiaomiParts";

    public void onReceive(Context context, Intent intent) {
        if (DEBUG) Log.d(TAG, "Received boot completed intent");
        DiracUtils.initialize(context);
        ThermalUtils.startService(context);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int gain = Settings.Secure.getInt(context.getContentResolver(),
                    SoundControlSettings.PREF_HEADPHONE_GAIN, 4);
            FileUtils.setValue(SoundControlSettings.HEADPHONE_GAIN_PATH, gain + " " + gain);
            FileUtils.setValue(SoundControlSettings.MICROPHONE_GAIN_PATH, Settings.Secure.getInt(context.getContentResolver(),
                    SoundControlSettings.PREF_MICROPHONE_GAIN, 0));
            FileUtils.setValue(SoundControlSettings.SPEAKER_GAIN_PATH, Settings.Secure.getInt(context.getContentResolver(),
                    SoundControlSettings.PREF_SPEAKER_GAIN, 0));

    }
}
