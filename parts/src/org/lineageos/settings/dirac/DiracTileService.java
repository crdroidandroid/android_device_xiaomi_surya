package org.lineageos.settings.dirac;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class DiracTileService extends TileService {

    @Override
    public void onStartListening() {

        Tile tile = getQsTile();
        if (DiracUtils.isDiracEnabled(this)) {
            tile.setState(Tile.STATE_ACTIVE);
        } else {
            tile.setState(Tile.STATE_INACTIVE);
        }

        tile.updateTile();

        super.onStartListening();
    }

    @Override
    public void onClick() {
        Tile tile = getQsTile();
        if (DiracUtils.isDiracEnabled(this)) {
            DiracUtils.setMusic(false);
            tile.setState(Tile.STATE_INACTIVE);
        } else {
            DiracUtils.setMusic(true);
            tile.setState(Tile.STATE_ACTIVE);
        }
        tile.updateTile();
        super.onClick();
    }
}
