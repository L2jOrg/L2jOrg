package org.l2j.gameserver.enums;

/**
 * @author UnAfraid
 */
public enum ShotType {
    SOULSHOTS,
    SPIRITSHOTS,
    BLESSED_SOULSHOTS,
    BLESSED_SPIRITSHOTS,
    FISH_SOULSHOTS;

    private final int _mask;

    ShotType() {
        _mask = 1 << ordinal();
    }

    public int getMask() {
        return _mask;
    }
}