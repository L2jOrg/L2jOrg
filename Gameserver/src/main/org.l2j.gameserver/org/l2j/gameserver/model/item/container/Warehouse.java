package org.l2j.gameserver.model.item.container;

/**
 * @author JoeAlisson
 */
public abstract class Warehouse extends ItemContainer {

    public boolean isPrivate() {
        return getType() == WarehouseType.PRIVATE;
    }

    public abstract WarehouseType getType();
}
