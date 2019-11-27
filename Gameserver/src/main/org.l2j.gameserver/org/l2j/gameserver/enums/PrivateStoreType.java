package org.l2j.gameserver.enums;

/**
 * @author NosBit
 */
public enum PrivateStoreType {
    NONE(0),
    SELL(1),
    SELL_MANAGE(2),
    BUY(3),
    BUY_MANAGE(4),
    MANUFACTURE(5),
    PACKAGE_SELL(8),
    SELL_BUFFS(9);

    private int _id;

    PrivateStoreType(int id) {
        _id = id;
    }

    public static PrivateStoreType findById(int id) {
        for (PrivateStoreType privateStoreType : values()) {
            if (privateStoreType.getId() == id) {
                return privateStoreType;
            }
        }
        return null;
    }

    public int getId() {
        return _id;
    }
}
