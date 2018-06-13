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
package slash.navigation.maps.mapsforge.helpers;

import org.mapsforge.map.layer.download.tilesource.AbstractTileSource;
import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;
import slash.navigation.common.BoundingBox;
import slash.navigation.maps.mapsforge.LocalMap;

import java.io.File;

/**
 * Default {@link LocalMap} to display a map in the Offline Edition without any downloads.
 *
 * @author Christian Pesch
 */

public class OpenStreetMap implements LocalMap {
    public static final String OPENSTREETMAP_URL = "http://www.openstreetmap.org/";

    public boolean isVector() {
        return false;
    }

    public File getFile() {
        throw new UnsupportedOperationException();
    }

    public AbstractTileSource getTileSource() {
        return OpenStreetMapMapnik.INSTANCE;
    }

    public BoundingBox getBoundingBox() {
        throw new UnsupportedOperationException();
    }

    public String getDescription() {
        return "OpenStreetMap";
    }

    public String getUrl() {
        return OPENSTREETMAP_URL;
    }
}
