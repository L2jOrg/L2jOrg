package org.l2j.gameserver.model;

/**
 * @author UnAfraid
 */
public class ChanceLocation extends Location {
    private final double _chance;

    public ChanceLocation(int x, int y, int z, int heading, double chance) {
        super(x, y, z, heading);
        _chance = chance;
    }

    public double getChance() {
        return _chance;
    }
}
