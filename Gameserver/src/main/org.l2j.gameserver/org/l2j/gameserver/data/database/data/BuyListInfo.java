package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("buylists")
public class BuyListInfo {

    @Column("buylist_id")
    private int id;

    @Column("item_id")
    private int itemId;

    private long count;

    @Column("next_restock_time")
    private long nextRestock;

    public static BuyListInfo of(int itemId, int buyListId) {
        final var info = new BuyListInfo();
        info.id = buyListId;
        info.itemId = itemId;
        return info;
    }

    public int getId() {
        return id;
    }

    public int getItemId() {
        return itemId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getNextRestock() {
        return nextRestock;
    }

    public void setNextRestock(long nextRestock) {
        this.nextRestock = nextRestock;
    }
}
