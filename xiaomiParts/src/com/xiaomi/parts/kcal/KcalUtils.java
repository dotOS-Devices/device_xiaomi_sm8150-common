/*
 * Copyright (C) 2021 chaldeaprjkt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.xiaomi.parts.kcal;

import android.content.Context;
import android.provider.Settings;

import com.xiaomi.parts.utils.FileUtils;

public class KcalUtils {

    public static final String PREF_ENABLED = "kcal_enabled";
    public static final String PREF_MINIMUM = "kcal_color_minimum";
    public static final String PREF_RED = "kcal_color_red";
    public static final String PREF_GREEN = "kcal_color_green";
    public static final String PREF_BLUE = "kcal_color_blue";
    public static final String PREF_SATURATION = "kcal_saturation";
    public static final String PREF_VALUE = "kcal_value";
    public static final String PREF_CONTRAST = "kcal_contrast";
    public static final String PREF_HUE = "kcal_hue";
    public static final String PREF_GRAYSCALE = "kcal_grayscale";

    public static final int DEFAULT_MINIMUM = 35;
    public static final int DEFAULT_RED = 255;
    public static final int DEFAULT_GREEN = 255;
    public static final int DEFAULT_BLUE = 255;
    public static final int DEFAULT_SATURATION = 35;
    public static final int DEFAULT_VALUE = 127;
    public static final int DEFAULT_CONTRAST = 127;
    public static final int DEFAULT_HUE = 0;

    private static final int OFFSET_SATURATION = 225;
    private static final int OFFSET_VALUE = 128;
    private static final int OFFSET_CONTRAST = 128;

    private static final String NODE_KCAL = "/sys/devices/platform/kcal_ctrl.0";
    private static final String NODE_ENABLE = NODE_KCAL + "/kcal_enable";
    private static final String NODE_CONT = NODE_KCAL + "/kcal_cont";
    private static final String NODE_HUE = NODE_KCAL + "/kcal_hue";
    private static final String NODE_MIN = NODE_KCAL + "/kcal_min";
    private static final String NODE_RGB = NODE_KCAL + "/kcal";
    private static final String NODE_SAT = NODE_KCAL + "/kcal_sat";
    private static final String NODE_VAL = NODE_KCAL + "/kcal_val";

    private final Context mContext;

    public KcalUtils(Context context) {
        mContext = context;
    }

    public void setKcalData(String key, Object value) {
        switch (key) {
            case PREF_ENABLED:
                FileUtils.writeLine(NODE_ENABLE, ((boolean) value) ? "1" : "0");
                break;
            case PREF_MINIMUM:
                FileUtils.writeLine(NODE_MIN, String.valueOf((int) value));
                break;
            case PREF_RED:
            case PREF_GREEN:
            case PREF_BLUE:
                FileUtils.writeLine(NODE_RGB, (String) value);
                break;
            case PREF_SATURATION:
                if (getInt(PREF_GRAYSCALE, 0) != 1) {
                    FileUtils.writeLine(NODE_SAT, String.valueOf((int) value + OFFSET_SATURATION));
                }
                break;
            case PREF_VALUE:
                FileUtils.writeLine(NODE_VAL, String.valueOf((int) value + OFFSET_VALUE));
                break;
            case PREF_CONTRAST:
                FileUtils.writeLine(NODE_CONT, String.valueOf((int) value + OFFSET_CONTRAST));
                break;
            case PREF_HUE:
                FileUtils.writeLine(NODE_HUE, String.valueOf((int) value));
                break;
            case PREF_GRAYSCALE:
                int sat = getInt(PREF_SATURATION, DEFAULT_SATURATION);
                sat = ((boolean) value) ? 128 : sat + OFFSET_SATURATION;
                FileUtils.writeLine(NODE_SAT, String.valueOf(sat));
            default:
                break;
        }
    }

    public int getInt(String key, int def) {
        return Settings.Secure.getInt(mContext.getContentResolver(), key, def);
    }

    public void restoreSettings() {
        FileUtils.writeLine(NODE_ENABLE, String.valueOf(getInt(PREF_ENABLED, 0)));
        FileUtils.writeLine(NODE_RGB, getInt(PREF_RED, DEFAULT_RED) + " " +
                getInt(PREF_GREEN, DEFAULT_GREEN) + " " +
                getInt(PREF_BLUE, DEFAULT_BLUE));
        FileUtils.writeLine(NODE_MIN, String.valueOf(getInt(PREF_MINIMUM, DEFAULT_MINIMUM)));
        FileUtils.writeLine(NODE_SAT, String.valueOf(getInt(PREF_GRAYSCALE, 0) == 1 ? 128 :
                getInt(PREF_SATURATION, DEFAULT_SATURATION) + OFFSET_SATURATION));
        FileUtils.writeLine(NODE_VAL, String.valueOf(getInt(PREF_VALUE, DEFAULT_VALUE) + OFFSET_VALUE));
        FileUtils.writeLine(NODE_CONT, String.valueOf(getInt(PREF_CONTRAST, DEFAULT_CONTRAST) + OFFSET_CONTRAST));
        FileUtils.writeLine(NODE_HUE, String.valueOf(getInt(PREF_HUE, DEFAULT_HUE)));
    }
}
