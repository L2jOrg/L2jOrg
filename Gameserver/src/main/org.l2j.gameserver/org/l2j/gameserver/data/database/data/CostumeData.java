package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.annotation.Transient;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author JoeAlisson
 */
@Table("player_costumes")
public class CostumeData {

    @Column("player_id")
    private int playerId;
    private int id;
    private long amount;
    private boolean locked;
    @Transient
    private boolean isNew;

    public void increaseAmount() {
        amount++;
    }

    public static CostumeData of(int costumeId, Player player) {
        var data = new CostumeData();
        data.playerId = player.getObjectId();
        data.id = costumeId;
        data.isNew = true;
        return data;
    }

    public int getId() {
        return id;
    }

    public long getAmount() {
        return amount;
    }

    public void setLocked(boolean lock) {
        this.locked = lock;
    }

    public boolean isLocked() {
        return locked;
    }

    public void reduceCount(long amount) {
        this.amount -= amount;
    }

    public boolean checkIsNewAndChange() {
        var ret = isNew;
        isNew = false;
        return ret;
    }
}
