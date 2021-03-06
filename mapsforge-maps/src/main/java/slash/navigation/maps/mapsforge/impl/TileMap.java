/*
    This file is part of RouteConverter.

    RouteConverter is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    RouteConverter is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with RouteConverter; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Copyright (C) 2007 Christian Pesch. All Rights Reserved.
*/
package slash.navigation.maps.mapsforge.impl;

import org.mapsforge.map.layer.download.tilesource.AbstractTileSource;
import slash.navigation.common.BoundingBox;
import slash.navigation.maps.mapsforge.LocalMap;

import java.io.File;
import java.util.prefs.Preferences;

import static java.util.prefs.Preferences.MAX_KEY_LENGTH;
import static slash.common.io.Transfer.encodeUmlauts;
import static slash.common.io.Transfer.trim;

/**
 * A {@link LocalMap} that is downloaded on request from an online service.
 *
 * @author Christian Pesch
 */

public class TileMap extends LocaleResourceImpl implements LocalMap {
    private static final Preferences preferences = Preferences.userNodeForPackage(TileMap.class);
    private static final String TILE_MAP_ACTIVE_PREFERENCE = "tileMapActive";
    private final AbstractTileSource tileSource;
    private final boolean active;

    public TileMap(String description, String url, boolean active, AbstractTileSource tileSource, String copyrightText) {
        super(description, url, copyrightText);
        this.tileSource = tileSource;
        this.active = active;
    }

    private String getActiveKey() {
        return trim(encodeUmlauts(TILE_MAP_ACTIVE_PREFERENCE + getUrl()), MAX_KEY_LENGTH);
    }

    public boolean isActive() {
        return preferences.getBoolean(getActiveKey(), active);
    }

    public void setActive(boolean active) {
        preferences.putBoolean(getActiveKey(), active);
    }

    public BoundingBox getBoundingBox() {
        throw new UnsupportedOperationException();
    }

    public File getFile() {
        throw new UnsupportedOperationException();
    }

    public AbstractTileSource getTileSource() {
        return tileSource;
    }

    public boolean isVector() {
        return false;
    }

    public String toString() {
        return getClass().getSimpleName() + "[description=" + getDescription() +
                ", url=" + getUrl() + ", active=" + isActive() + "]";
    }
}
