/*
 * Copyright (C) 2021 Paranoid Android
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

package org.lineageos.settings.display;

import android.content.Context;
import android.os.SystemProperties;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import java.util.Arrays;

import org.lineageos.settings.R;

public class CabcTileService extends TileService {

    private Context context;
    private Tile tile;

    private String[] CabcModes;
    private String[] CabcValues;
    private int currentCabcMode;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        CabcModes = context.getResources().getStringArray(R.array.lcd_cabc_modes);
        CabcValues = context.getResources().getStringArray(R.array.lcd_cabc_values);
    }

    private void updateCurrentCabcMode() {
        currentCabcMode = Arrays.asList(CabcValues).indexOf(SystemProperties.get(LcdFeaturesPreferenceFragment.CABC_PROP, "0"));
    }

    private void updateCabcTile() {
        tile.setState(currentCabcMode > 0 ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setContentDescription(CabcModes[currentCabcMode]);
        tile.setSubtitle(CabcModes[currentCabcMode]);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        tile = getQsTile();
        updateCurrentCabcMode();
        updateCabcTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        updateCurrentCabcMode();
        if (currentCabcMode == CabcModes.length - 1) {
            currentCabcMode = 0;
        } else {
            currentCabcMode++;
        }
        SystemProperties.set(LcdFeaturesPreferenceFragment.CABC_PROP, CabcValues[currentCabcMode]);
        updateCabcTile();
    }
}
