/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
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
 * limitations under the License.
 */

package org.lineageos.settings.doze;

import static android.provider.Settings.Secure.DOZE_ALWAYS_ON;
import static android.provider.Settings.Secure.DOZE_ENABLED;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceManager;

import org.lineageos.settings.R;
import org.lineageos.settings.utils.FileUtils;

public final class DozeUtils {
    private static final String TAG = "DozeUtils";
    private static final boolean DEBUG = false;

    private static final String DOZE_INTENT = "com.android.systemui.doze.pulse";

    protected static final String DOZE_ENABLE = "doze_enable";
    protected static final String ALWAYS_ON_DISPLAY = "always_on_display";
    protected static final String DOZE_BRIGHTNESS_KEY = "doze_brightness";
    protected static final String WAKE_ON_GESTURE_KEY = "wake_on_gesture";
    protected static final String CATEG_PICKUP_SENSOR = "pickup_sensor";
    protected static final String CATEG_PROX_SENSOR = "proximity_sensor";

    protected static final String GESTURE_PICK_UP_KEY = "gesture_pick_up";
    protected static final String GESTURE_HAND_WAVE_KEY = "gesture_hand_wave";
    protected static final String GESTURE_POCKET_KEY = "gesture_pocket";

    protected static final String DOZE_MODE_PATH =
            "/sys/devices/platform/soc/soc:qcom,dsi-display/doze_mode";
    protected static final String DOZE_MODE_HBM = "1";
    protected static final String DOZE_MODE_LBM = "0";

    private static final String DOZE_STATUS_PATH =
            "/sys/devices/platform/soc/soc:qcom,dsi-display/doze_status";
    protected static final String DOZE_STATUS_ENABLED = "1";
    protected static final String DOZE_STATUS_DISABLED = "0";

    protected static final String DOZE_BRIGHTNESS_LBM = "0";
    protected static final String DOZE_BRIGHTNESS_HBM = "1";
    protected static final String DOZE_BRIGHTNESS_DARK = "2";
    protected static final String DOZE_BRIGHTNESS_AUTO = "3";

    public static void onBootCompleted(Context context) {
        checkDozeService(context);
        restoreDozeModes(context);
    }
    public static void startService(Context context) {
        if (DEBUG)
            Log.d(TAG, "Starting service");
        context.startServiceAsUser(new Intent(context, DozeService.class), UserHandle.CURRENT);
    }

    protected static void stopService(Context context) {
        if (DEBUG)
            Log.d(TAG, "Stopping service");
        context.stopServiceAsUser(new Intent(context, DozeService.class), UserHandle.CURRENT);
    }

    public static void checkDozeService(Context context) {
        if (isDozeEnabled(context) && (isAlwaysOnEnabled(context) || sensorsEnabled(context))) {
            startService(context);
        } else {
            stopService(context);
        }
    }

    private static void restoreDozeModes(Context context) {
        if (isAlwaysOnEnabled(context) && !isDozeAutoBrightnessEnabled(context)) {
            setDozeMode(PreferenceManager.getDefaultSharedPreferences(context).getString(
                    DOZE_BRIGHTNESS_KEY, String.valueOf(DOZE_BRIGHTNESS_LBM)));
        }
    }
    protected static boolean getProxCheckBeforePulse(Context context) {
        try {
            Context con = context.createPackageContext("com.android.systemui", 0);
            int id = con.getResources().getIdentifier(
                    "doze_proximity_check_before_pulse", "bool", "com.android.systemui");
            return con.getResources().getBoolean(id);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    protected static boolean enableDoze(Context context, boolean enable) {
        return Settings.Secure.putInt(context.getContentResolver(), DOZE_ENABLED, enable ? 1 : 0);
    }

    public static boolean isDozeEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), DOZE_ENABLED, 1) != 0;
    }

    protected static void wakeOrLaunchDozePulse(Context context) {
        if (isWakeOnGestureEnabled(context)) {
            if (DEBUG) {
                Log.d(TAG, "Wake up display");
            }
            PowerManager powerManager = context.getSystemService(PowerManager.class);
            powerManager.wakeUp(SystemClock.uptimeMillis(), PowerManager.WAKE_REASON_GESTURE, TAG);
        } else {
            if (DEBUG) {
                Log.d(TAG, "Launch doze pulse");
            }
            context.sendBroadcastAsUser(
                    new Intent(DOZE_INTENT), new UserHandle(UserHandle.USER_CURRENT));
        }
    }

    protected static boolean enableAlwaysOn(Context context, boolean enable) {
        return Settings.Secure.putIntForUser(context.getContentResolver(), DOZE_ALWAYS_ON,
                enable ? 1 : 0, UserHandle.USER_CURRENT);
    }

    protected static boolean isAlwaysOnEnabled(Context context) {
        final boolean enabledByDefault = context.getResources().getBoolean(
                com.android.internal.R.bool.config_dozeAlwaysOnEnabled);

        return Settings.Secure.getIntForUser(context.getContentResolver(), DOZE_ALWAYS_ON,
                       alwaysOnDisplayAvailable(context) && enabledByDefault ? 1 : 0,
                       UserHandle.USER_CURRENT)
                != 0;
    }

    protected static boolean alwaysOnDisplayAvailable(Context context) {
        return new AmbientDisplayConfiguration(context).alwaysOnAvailable();
    }

    protected static boolean setDozeMode(String value) {
        return FileUtils.writeLine(DOZE_MODE_PATH, value);
    }

    protected static boolean setDozeStatus(String value) {
        return FileUtils.writeLine(DOZE_STATUS_PATH, value);
    }

    protected static boolean isDozeAutoBrightnessEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(DOZE_BRIGHTNESS_KEY, DOZE_BRIGHTNESS_LBM)
                .equals(DOZE_BRIGHTNESS_AUTO);
    }

    protected static boolean isGestureEnabled(Context context, String gesture) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(gesture, false);
    }

    protected static boolean isWakeOnGestureEnabled(Context context) {
        return isGestureEnabled(context, WAKE_ON_GESTURE_KEY);
    }

    protected static boolean isPickUpEnabled(Context context) {
        return isGestureEnabled(context, GESTURE_PICK_UP_KEY);
    }

    protected static boolean isHandwaveGestureEnabled(Context context) {
        return isGestureEnabled(context, GESTURE_HAND_WAVE_KEY);
    }

    protected static boolean isPocketGestureEnabled(Context context) {
        return isGestureEnabled(context, GESTURE_POCKET_KEY);
    }

    public static boolean sensorsEnabled(Context context) {
        return isDozeAutoBrightnessEnabled(context) || isHandwaveGestureEnabled(context)
                || isPickUpEnabled(context) || isPocketGestureEnabled(context);
    }

    protected static Sensor getSensor(SensorManager sm, String type) {
        for (Sensor sensor : sm.getSensorList(Sensor.TYPE_ALL)) {
            if (type.equals(sensor.getStringType())) {
                return sensor;
            }
        }
        return null;
    }

    protected static void updateDozeBrightnessIcon(Context context, ListPreference preference) {
        switch (PreferenceManager.getDefaultSharedPreferences(context).getString(
                DOZE_BRIGHTNESS_KEY, DOZE_BRIGHTNESS_LBM)) {
            case DozeUtils.DOZE_BRIGHTNESS_LBM:
                preference.setIcon(R.drawable.ic_doze_brightness_low);
                break;
            case DozeUtils.DOZE_BRIGHTNESS_HBM:
                preference.setIcon(R.drawable.ic_doze_brightness_high);
                break;
            case DozeUtils.DOZE_BRIGHTNESS_DARK:
                preference.setIcon(R.drawable.ic_doze_brightness_dark);
                break;
            case DozeUtils.DOZE_BRIGHTNESS_AUTO:
                preference.setIcon(R.drawable.ic_doze_brightness_auto);
                break;
        }
    }
}
