package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author JoeAlisson
 */
@Table("itemsonground")
public class ItemOnGroundData {

    @Column("object_id")
    private int objectId;

    @Column("item_id")
    private int itemId;

    private long count;

    @Column("enchant_level")
    private int enchantLevel;

    private int x;
    private int y;
    private int z;

    @Column("drop_time")
    private long dropTime;
    private int equipable;

    public static ItemOnGroundData of(Item item) {
        final var data = new ItemOnGroundData();
        data.objectId = item.getObjectId();
        data.itemId = item.getId();
        data.count = item.getCount();
        data.enchantLevel = item.getEnchantLevel();
        data.x = item.getX();
        data.y = item.getY();
        data.z = item.getZ();
        data.dropTime = item.isProtected() ? -1 : item.getDropTime();
        data.equipable = item.isEquipable() ? 1 : 0;
        return data;
    }

    public int getObjectId() {
        return objectId;
    }

    public int getItemId() {
        return itemId;
    }

    public long getCount() {
        return count;
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public long getDropTime() {
        return dropTime;
    }
}
