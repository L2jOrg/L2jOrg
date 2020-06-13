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
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;
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
    @NonUpdatable
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
