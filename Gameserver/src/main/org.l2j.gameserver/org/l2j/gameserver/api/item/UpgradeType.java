package org.l2j.gameserver.api.item;

/**
 * @author JoeAlisson
 */
public enum UpgradeType {
    RARE,
    NORMAL,
    SPECIAL;

    public static UpgradeType ofId(int type) {
        return switch (type){
            case 0 -> RARE;
            case 1 -> NORMAL;
            case 2 -> SPECIAL;
            default -> null;
        };
    }
}