/*
 * Copyright (C) 2021 The LineageOS Project
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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AodSensor implements SensorEventListener {
    private static final boolean DEBUG = false;
    private static final String TAG = "AodSensor";

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Context mContext;
    private ExecutorService mExecutorService;

    public AodSensor(Context context) {
        mContext = context;
        mSensorManager = mContext.getSystemService(SensorManager.class);
        mSensor = DozeUtils.getSensor(mSensorManager, "xiaomi.sensor.aod");
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    private Future<?> submit(Runnable runnable) { return mExecutorService.submit(runnable); }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (DEBUG) {
            Log.d(TAG, "Got sensor event: " + event.values[0]);
        }

        if (event.values[0] == 3 || event.values[0] == 5) {
            DozeUtils.setDozeMode(DozeUtils.DOZE_MODE_LBM);
        } else if (event.values[0] == 4) {
            DozeUtils.setDozeMode(DozeUtils.DOZE_MODE_HBM);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        /* Empty */
    }

    protected void enable() {
        if (DEBUG) {
            Log.d(TAG, "Enabling");
        }
        submit(() -> {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        });
    }

    protected void disable() {
        if (DEBUG) {
            Log.d(TAG, "Disabling");
        }
        submit(() -> { mSensorManager.unregisterListener(this, mSensor); });
    }
}
