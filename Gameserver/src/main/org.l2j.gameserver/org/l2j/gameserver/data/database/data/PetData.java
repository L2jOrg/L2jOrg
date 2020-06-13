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
