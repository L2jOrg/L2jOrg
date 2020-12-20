/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.model.actor.instance.Pet;

/**
 * @author JoeAlisson
 */
@Table("pets")
public class PetData {

    @Column("item_obj_id")
    private int itemObjectId;
    private String name;
    private byte level;
    private int curHp;
    private int curMp;
    private long exp;
    private long sp;
    private int fed;
    private int ownerId;
    private boolean restore;

    public int getItemObjectId() {
        return itemObjectId;
    }

    public String getName() {
        return name;
    }

    public byte getLevel() {
        return level;
    }

    public int getCurHp() {
        return curHp;
    }

    public int getCurMp() {
        return curMp;
    }

    public long getExp() {
        return exp;
    }

    public long getSp() {
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

    public static PetData of(Pet pet, boolean restore) {
        var data = new PetData();
        data.name = pet.getName();
        data.level = (byte) pet.getLevel();
        data.curHp = (int) pet.getCurrentHp();
        data.curMp = (int) pet.getCurrentMp();
        data.exp = pet.getStats().getExp();
        data.sp = pet.getStats().getSp();
        data.fed = pet.getCurrentFed();
        data.ownerId = pet.getOwner().getObjectId();
        data.restore = restore;
        data.itemObjectId = pet.getCON();
        return data;
    }
}
