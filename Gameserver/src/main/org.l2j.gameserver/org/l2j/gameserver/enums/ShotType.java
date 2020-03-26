package org.l2j.gameserver.enums;

/**
 * @author UnAfraid
 * @author  JoeAlisson
 */
public enum ShotType {
    SOULSHOTS(0),
    SPIRITSHOTS(1),
    BEAST_SOULSHOTS(2),
    BEAST_SPIRITSHOTS(3);

    private static final ShotType[] CACHED = values();

    private final int clientType;

    ShotType(int clientType) {
        this.clientType = clientType;
    }

    public static ShotType of(int type) {
        for (ShotType shotType : CACHED) {
            if(shotType.clientType == type) {
                return shotType;
            }
        }
        return null;
    }

    public int getClientType() {
        return clientType;
    }
}