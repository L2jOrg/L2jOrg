package org.l2j.gameserver.engine.geo.geodata;

/**
 * @author Hasha
 */
public enum GeoFormat {
    L2J("%d_%d.l2j"),
    L2OFF("%d_%d_conv.dat"),
    L2D("%d_%d.l2d");

    private final String _filename;

    private GeoFormat(String filename) {
        _filename = filename;
    }

    public String getFilename() {
        return _filename;
    }
}