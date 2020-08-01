/*
 * Copyright © 2019 L2J Mobius
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
import org.l2j.gameserver.world.World;

/**
 * @author Sdw
 * @author JoeAlisson
 */
@Table("pledge_applicant")
public class PledgeApplicantData {

    @Column("charId")
    private int playerId;
    private int clanId;
    private int karma;
    private String message;

    @NonUpdatable
    @Column("base_class")
    private int classId;

    @NonUpdatable
    @Column("level")
    private int playerLevel;

    @NonUpdatable
    @Column("char_name")
    private String playerName;

    public PledgeApplicantData() {

    }

    public PledgeApplicantData(int playerId, String playerName, int playerLevel, int karma, int requestClanId, String message) {
        this.playerId = playerId;
        clanId = requestClanId;
        this.playerName = playerName;
        this.playerLevel = playerLevel;
        this.karma = karma;
        this.message = message;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getRequestClanId() {
        return clanId;
    }

    public String getPlayerName() {
        if (isOnline() && !getPlayerInstance().getName().equalsIgnoreCase(playerName)) {
            playerName = getPlayerInstance().getName();
        }
        return playerName;
    }

    public int getPlayerLvl() {
        if (isOnline() && (getPlayerInstance().getLevel() != playerLevel)) {
            playerLevel = getPlayerInstance().getLevel();
        }
        return playerLevel;
    }

    public int getClassId() {
        if (isOnline() && (getPlayerInstance().getBaseClass() != classId)) {
            classId = getPlayerInstance().getClassId().getId();
        }
        return classId;
    }

    public String getMessage() {
        return message;
    }

    public int getKarma() {
        return karma;
    }

    public Player getPlayerInstance() {
        return World.getInstance().findPlayer(playerId);
    }

    public boolean isOnline() {
        return (getPlayerInstance() != null) && (getPlayerInstance().isOnlineInt() > 0);
    }
}