package org.l2j.gameserver.model.item.container;

/**
 * @author JoeAlisson
 */
public enum WarehouseType {
    PRIVATE,
    CLAN,
    CASTLE,
    FREIGHT;

    public int clientId() {
        return 1 + ordinal();
    }
}
