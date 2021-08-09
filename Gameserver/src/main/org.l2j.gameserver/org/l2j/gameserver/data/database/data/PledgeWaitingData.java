/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
import org.l2j.gameserver.world.World;

/**
 * @author Sdw
 * @author JoeAlisson
 */
@Table("pledge_waiting_list")
public class PledgeWaitingData {

    @Column("char_id")
    private int playerId;
    private int karma;

    @NonUpdatable
    @Column("base_class")
    private int classId;

    @NonUpdatable
    private int level;

    @NonUpdatable
    @Column("char_name")
    private String name;

    public PledgeWaitingData() {

    }

    public PledgeWaitingData(int playerId, int playerLvl, int karma, int classId, String playerName) {
        this.playerId = playerId;
        this.classId = classId;
        level = playerLvl;
        this.karma = karma;
        name = playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getPlayerClassId() {
        if (isOnline() && (getPlayerInstance().getBaseClass() != classId)) {
            classId = getPlayerInstance().getClassId().getId();
        }
        return classId;
    }

    public int getPlayerLvl() {
        if (isOnline() && (getPlayerInstance().getLevel() != level)) {
            level = getPlayerInstance().getLevel();
        }
        return level;
    }

    public int getKarma() {
        return karma;
    }

    public String getPlayerName() {
        if (isOnline() && !getPlayerInstance().getName().equalsIgnoreCase(name)) {
            name = getPlayerInstance().getName();
        }
        return name;
    }

    public Player getPlayerInstance() {
        return World.getInstance().findPlayer(playerId);
    }

    public boolean isOnline() {
        return (getPlayerInstance() != null) && (getPlayerInstance().isOnline());
    }
}
