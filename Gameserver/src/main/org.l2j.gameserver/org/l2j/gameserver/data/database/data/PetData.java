package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("pets")
public class PetData {

    @Column("item_obj_id")
    private int itemObjectId;
    private String name;
    private int level;
    private int curHp;
    private int curMp;
    private int exp;
    private int sp;
    private int fed;
    private int ownerId;
    private boolean restore;

    public int getItemObjectId() {
        return itemObjectId;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getCurHp() {
        return curHp;
    }

    public int getCurMp() {
        return curMp;
    }

    public int getExp() {
        return exp;
    }

    public int getSp() {
        return sp;
    }

    public int getFed() {
        return fed;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public boolean isRestore() {
        return restore;
    }
}
