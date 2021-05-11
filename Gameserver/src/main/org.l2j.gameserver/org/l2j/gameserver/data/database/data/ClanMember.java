/*
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
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.settings.ClanSettings;

import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.emptyIfNullOrElse;

/**
 * @author JoeAlisson
 */
public class ClanMember {

    private Clan clan;
    private Player player;

    @Column("charId")
    private int objectId;

    @Column("char_name")
    private String name;

    @Column("power_grade")
    private int powerGrade;

    @Column("classid")
    private int classId;

    @Column("race")
    private int raceOrdinal;

    @Column("last_reputation_level")
    private int lastReputationLevel;

    private String title;
    private boolean sex;
    private int apprentice;
    private int sponsor;
    private int level;

    private long onlineTime;

    public ClanMember () {
        // default
    }

    public void login(Player player) {
        this.player = player;
   }

    public void logout() {
        if(player != null) {
            name = player.getName();
            level = player.getLevel();
            classId = player.getClassId().getId();
            objectId = player.getObjectId();
            powerGrade = player.getPowerGrade();
            title = player.getTitle();
            apprentice = player.getApprentice();
            sponsor = player.getSponsor();
            sex = player.getAppearance().isFemale();
            raceOrdinal = player.getRace().ordinal();
            player = null;
        }
    }

    public String getApprenticeOrSponsorName() {
        if (player != null) {
            apprentice = player.getApprentice();
            sponsor = player.getSponsor();
        }

        String memberName = "";
        if (apprentice != 0) {
            memberName = emptyIfNullOrElse(clan.getClanMember(apprentice), ClanMember::getName);
        } else if (sponsor != 0) {
            memberName = emptyIfNullOrElse(clan.getClanMember(sponsor), ClanMember::getName);
        }
        return memberName;
    }

    public void setPowerGrade(int powerGrade) {
        this.powerGrade = powerGrade;
        if (player != null) {
            player.setPowerGrade(powerGrade);
        } else {
            getDAO(PlayerDAO.class).updatePowerGrade(objectId, this.powerGrade);
        }
    }

    public void resetBonus() {
        onlineTime = 0;
        getDAO(PlayerVariablesDAO.class).resetClaimedClanReward();
    }

    public void saveApprenticeAndSponsor(int apprentice, int sponsor) {
        getDAO(PlayerDAO.class).updateApprenticeAndSponsor(objectId, apprentice, sponsor);
    }

    public void setApprenticeAndSponsor(int apprenticeID, int sponsorID) {
        apprentice = apprenticeID;
        sponsor = sponsorID;
    }

    /**
     * Verifies if the clan member is online.
     *
     * @return {@code true} if is online
     */
    public boolean isOnline() {
        return player != null;
    }

    /**
     * Gets the class id.
     *
     * @return the classId
     */
    public int getClassId() {
        return player != null ? player.getClassId().getId() : classId;
    }

    /**
     * Gets the level.
     *
     * @return the level
     */
    public int getLevel() {
        return player != null ? player.getLevel() : level;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return player != null ? player.getName() : name;
    }

    /**
     * Gets the object id.
     *
     * @return Returns the objectId.
     */
    public int getObjectId() {
        return player != null ? player.getObjectId() : objectId;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return player != null ? player.getTitle() : title;
    }

    /**
     * Gets the power grade.
     *
     * @return the power grade
     */
    public int getPowerGrade() {
        return player != null ? player.getPowerGrade() : powerGrade;
    }

    /**
     * Gets the player's race ordinal.
     *
     * @return the race ordinal
     */
    public int getRaceOrdinal() {
        return player != null ? player.getRace().ordinal() : raceOrdinal;
    }

    /**
     * Gets the player's sex.
     *
     * @return the sex
     */
    public boolean getSex() {
        return player != null ? player.getAppearance().isFemale() : sex;
    }

    /**
     * Gets the sponsor.
     *
     * @return the sponsor
     */
    public int getSponsor() {
        return player != null ? player.getSponsor() : sponsor;
    }

    /**
     * Gets the apprentice.
     *
     * @return the apprentice
     */
    public int getApprentice() {
        return player != null ? player.getApprentice() : apprentice;
    }

    /**
     * Gets the clan.
     *
     * @return the clan
     */
    public Clan getClan() {
        return clan;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public int getOnlineStatus() {
        if(!isOnline()) {
            return 0;
        }
        return onlineTime >= ClanSettings.onlineTimeForBonus() ? 2 : 1;
    }

    public int getLastReputationLevel() {
        return lastReputationLevel;
    }

    public void setLastReputationLevel(int lastReputationLevel) {
        this.lastReputationLevel = lastReputationLevel;
    }

    public Player getPlayerInstance() {
        return player;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public static ClanMember of(Clan clan, Player player) {
        var member = new ClanMember();
        member.clan = clan;
        member.player = player;
        return member;
    }
}
