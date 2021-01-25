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
package org.l2j.gameserver.model;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.actor.instance.Player;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * This class holds the clan members data.
 */
public class ClanMember {

    private final Clan clan;
    private int _objectId;
    private String _name;
    private String _title;
    private int _powerGrade;
    private int _level;
    private int _classId;
    private boolean _sex;
    private int _raceOrdinal;
    private Player _player;
    private int _pledgeType;
    private int _apprentice;
    private int _sponsor;
    private long _onlineTime;

    public ClanMember(Clan clan, PlayerData memberData) {
        this.clan = clan;
        _name = memberData.getName();
        _level = memberData.getLevel();
        _classId = memberData.getClassId();
        _objectId = memberData.getCharId();
        _pledgeType = memberData.getSubPledge();
        _title = memberData.getTitle();
        _powerGrade = memberData.getPowerGrade();
        _apprentice = memberData.getApprentice();
        _sponsor = memberData.getSponsor();
        _sex = memberData.isFemale();
        _raceOrdinal = memberData.getRace();
    }

    /**
     * Creates a clan member from a player instance.
     *
     * @param clan   the clan where the player belongs
     * @param player the player from which the clan member will be created
     */
    public ClanMember(Clan clan, Player player) {
        if (clan == null) {
            throw new IllegalArgumentException("Cannot create a Clan Member if player has a null clan.");
        }
        _player = player;
        this.clan = clan;
        _name = player.getName();
        _level = player.getLevel();
        _classId = player.getClassId().getId();
        _objectId = player.getObjectId();
        _pledgeType = player.getPledgeType();
        _powerGrade = player.getPowerGrade();
        _title = player.getTitle();
        _sex = player.getAppearance().isFemale();
        _raceOrdinal = player.getRace().ordinal();
    }

    /**
     * Calculate pledge class.
     *
     * @param player the player
     * @return the int
     */
    public static int calculatePledgeClass(Player player) {
        int pledgeClass = 0;
        if (player == null) {
            return pledgeClass;
        }

        final Clan clan = player.getClan();
        if (clan != null) {
            switch (clan.getLevel()) {
                case 4: {
                    if (player.isClanLeader()) {
                        pledgeClass = 3;
                    }
                    break;
                }
                case 5: {
                    if (player.isClanLeader()) {
                        pledgeClass = 4;
                    } else {
                        pledgeClass = 2;
                    }
                    break;
                }
                case 6: {
                    switch (player.getPledgeType()) {
                        case -1: {
                            pledgeClass = 1;
                            break;
                        }
                        case 100:
                        case 200: {
                            pledgeClass = 2;
                            break;
                        }
                        case 0: {
                            if (player.isClanLeader()) {
                                pledgeClass = 5;
                            } else {
                                switch (clan.getLeaderSubPledge(player.getObjectId())) {
                                    case 100:
                                    case 200: {
                                        pledgeClass = 4;
                                        break;
                                    }
                                    case -1:
                                    default: {
                                        pledgeClass = 3;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (player.getPledgeType()) {
                        case -1: {
                            pledgeClass = 1;
                            break;
                        }
                        case 100:
                        case 200: {
                            pledgeClass = 3;
                            break;
                        }
                        case 1001:
                        case 1002:
                        case 2001:
                        case 2002: {
                            pledgeClass = 2;
                            break;
                        }
                        case 0: {
                            if (player.isClanLeader()) {
                                pledgeClass = 7;
                            } else {
                                switch (clan.getLeaderSubPledge(player.getObjectId())) {
                                    case 100:
                                    case 200: {
                                        pledgeClass = 6;
                                        break;
                                    }
                                    case 1001:
                                    case 1002:
                                    case 2001:
                                    case 2002: {
                                        pledgeClass = 5;
                                        break;
                                    }
                                    case -1:
                                    default: {
                                        pledgeClass = 4;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (player.getPledgeType()) {
                        case -1: {
                            pledgeClass = 1;
                            break;
                        }
                        case 100:
                        case 200: {
                            pledgeClass = 4;
                            break;
                        }
                        case 1001:
                        case 1002:
                        case 2001:
                        case 2002: {
                            pledgeClass = 3;
                            break;
                        }
                        case 0: {
                            if (player.isClanLeader()) {
                                pledgeClass = 8;
                            } else {
                                switch (clan.getLeaderSubPledge(player.getObjectId())) {
                                    case 100:
                                    case 200: {
                                        pledgeClass = 7;
                                        break;
                                    }
                                    case 1001:
                                    case 1002:
                                    case 2001:
                                    case 2002: {
                                        pledgeClass = 6;
                                        break;
                                    }
                                    case -1:
                                    default: {
                                        pledgeClass = 5;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 9: {
                    switch (player.getPledgeType()) {
                        case -1: {
                            pledgeClass = 1;
                            break;
                        }
                        case 100:
                        case 200: {
                            pledgeClass = 5;
                            break;
                        }
                        case 1001:
                        case 1002:
                        case 2001:
                        case 2002: {
                            pledgeClass = 4;
                            break;
                        }
                        case 0: {
                            if (player.isClanLeader()) {
                                pledgeClass = 9;
                            } else {
                                switch (clan.getLeaderSubPledge(player.getObjectId())) {
                                    case 100:
                                    case 200: {
                                        pledgeClass = 8;
                                        break;
                                    }
                                    case 1001:
                                    case 1002:
                                    case 2001:
                                    case 2002: {
                                        pledgeClass = 7;
                                        break;
                                    }
                                    case -1:
                                    default: {
                                        pledgeClass = 6;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (player.getPledgeType()) {
                        case -1: {
                            pledgeClass = 1;
                            break;
                        }
                        case 100:
                        case 200: {
                            pledgeClass = 6;
                            break;
                        }
                        case 1001:
                        case 1002:
                        case 2001:
                        case 2002: {
                            pledgeClass = 5;
                            break;
                        }
                        case 0: {
                            if (player.isClanLeader()) {
                                pledgeClass = 10;
                            } else {
                                switch (clan.getLeaderSubPledge(player.getObjectId())) {
                                    case 100:
                                    case 200: {
                                        pledgeClass = 9;
                                        break;
                                    }
                                    case 1001:
                                    case 1002:
                                    case 2001:
                                    case 2002: {
                                        pledgeClass = 8;
                                        break;
                                    }
                                    case -1:
                                    default: {
                                        pledgeClass = 7;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 11: {
                    switch (player.getPledgeType()) {
                        case -1: {
                            pledgeClass = 1;
                            break;
                        }
                        case 100:
                        case 200: {
                            pledgeClass = 7;
                            break;
                        }
                        case 1001:
                        case 1002:
                        case 2001:
                        case 2002: {
                            pledgeClass = 6;
                            break;
                        }
                        case 0: {
                            if (player.isClanLeader()) {
                                pledgeClass = 11;
                            } else {
                                switch (clan.getLeaderSubPledge(player.getObjectId())) {
                                    case 100:
                                    case 200: {
                                        pledgeClass = 10;
                                        break;
                                    }
                                    case 1001:
                                    case 1002:
                                    case 2001:
                                    case 2002: {
                                        pledgeClass = 9;
                                        break;
                                    }
                                    case -1:
                                    default: {
                                        pledgeClass = 8;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                default: {
                    pledgeClass = 1;
                    break;
                }
            }
        }

        if (player.isNoble() && (pledgeClass < 5)) {
            pledgeClass = 5;
        }

        if (player.isHero() && (pledgeClass < 8)) {
            pledgeClass = 8;
        }
        return pledgeClass;
    }

    /**
     * Gets the player instance.
     *
     * @return the player instance
     */
    public Player getPlayerInstance() {
        return _player;
    }

    /**
     * Sets the player instance.
     *
     * @param player the new player instance
     */
    public void setPlayerInstance(Player player) {
        if ((player == null) && (_player != null)) {
            // this is here to keep the data when the player logs off
            _name = _player.getName();
            _level = _player.getLevel();
            _classId = _player.getClassId().getId();
            _objectId = _player.getObjectId();
            _powerGrade = _player.getPowerGrade();
            _pledgeType = _player.getPledgeType();
            _title = _player.getTitle();
            _apprentice = _player.getApprentice();
            _sponsor = _player.getSponsor();
            _sex = _player.getAppearance().isFemale();
            _raceOrdinal = _player.getRace().ordinal();
        }

        if (player != null) {
            clan.addSkillEffects(player);
            if ((clan.getLevel() > 3) && player.isClanLeader()) {
                SiegeManager.getInstance().addSiegeSkills(player);
            }
            if (player.isClanLeader()) {
                clan.setLeader(this);
            }
        }
        _player = player;
    }

    /**
     * Verifies if the clan member is online.
     *
     * @return {@code true} if is online
     */
    public boolean isOnline() {
        if ((_player == null) || !_player.isOnline()) {
            return false;
        }
        return (_player.getClient() != null);
    }

    /**
     * Gets the class id.
     *
     * @return the classId
     */
    public int getClassId() {
        return _player != null ? _player.getClassId().getId() : _classId;
    }

    /**
     * Gets the level.
     *
     * @return the level
     */
    public int getLevel() {
        return _player != null ? _player.getLevel() : _level;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return _player != null ? _player.getName() : _name;
    }

    /**
     * Gets the object id.
     *
     * @return Returns the objectId.
     */
    public int getObjectId() {
        return _player != null ? _player.getObjectId() : _objectId;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return _player != null ? _player.getTitle() : _title;
    }

    /**
     * Gets the pledge type.
     *
     * @return the pledge type
     */
    public int getPledgeType() {
        return _player != null ? _player.getPledgeType() : _pledgeType;
    }

    /**
     * Sets the pledge type.
     *
     * @param pledgeType the new pledge type
     */
    public void setPledgeType(int pledgeType) {
        _pledgeType = pledgeType;
        if (_player != null) {
            _player.setPledgeType(pledgeType);
        } else {
            // db save if char not logged in
            updatePledgeType();
        }
    }

    public void updatePledgeType() {
        getDAO(PlayerDAO.class).updateSubpledge(_objectId, _pledgeType);
    }

    /**
     * Gets the power grade.
     *
     * @return the power grade
     */
    public int getPowerGrade() {
        return _player != null ? _player.getPowerGrade() : _powerGrade;
    }

    /**
     * Sets the power grade.
     *
     * @param powerGrade the new power grade
     */
    public void setPowerGrade(int powerGrade) {
        _powerGrade = powerGrade;
        if (_player != null) {
            _player.setPowerGrade(powerGrade);
        } else {
            // db save if char not logged in
            getDAO(PlayerDAO.class).updatePowerGrade(_objectId, _powerGrade);
        }
    }

    /**
     * Sets the apprentice and sponsor.
     *
     * @param apprenticeID the apprentice id
     * @param sponsorID    the sponsor id
     */
    public void setApprenticeAndSponsor(int apprenticeID, int sponsorID) {
        _apprentice = apprenticeID;
        _sponsor = sponsorID;
    }

    /**
     * Gets the player's race ordinal.
     *
     * @return the race ordinal
     */
    public int getRaceOrdinal() {
        return _player != null ? _player.getRace().ordinal() : _raceOrdinal;
    }

    /**
     * Gets the player's sex.
     *
     * @return the sex
     */
    public boolean getSex() {
        return _player != null ? _player.getAppearance().isFemale() : _sex;
    }

    /**
     * Gets the sponsor.
     *
     * @return the sponsor
     */
    public int getSponsor() {
        return _player != null ? _player.getSponsor() : _sponsor;
    }

    /**
     * Gets the apprentice.
     *
     * @return the apprentice
     */
    public int getApprentice() {
        return _player != null ? _player.getApprentice() : _apprentice;
    }

    /**
     * Gets the apprentice or sponsor name.
     *
     * @return the apprentice or sponsor name
     */
    public String getApprenticeOrSponsorName() {
        if (_player != null) {
            _apprentice = _player.getApprentice();
            _sponsor = _player.getSponsor();
        }

        if (_apprentice != 0) {
            final ClanMember apprentice = clan.getClanMember(_apprentice);
            if (apprentice != null) {
                return apprentice.getName();
            }
            return "Error";
        }
        if (_sponsor != 0) {
            final ClanMember sponsor = clan.getClanMember(_sponsor);
            if (sponsor != null) {
                return sponsor.getName();
            }
            return "Error";
        }
        return "";
    }

    /**
     * Gets the clan.
     *
     * @return the clan
     */
    public Clan getClan() {
        return clan;
    }

    /**
     * Save apprentice and sponsor.
     *
     * @param apprentice the apprentice
     * @param sponsor    the sponsor
     */
    public void saveApprenticeAndSponsor(int apprentice, int sponsor) {
        getDAO(PlayerDAO.class).updateApprenticeAndSponsor(_objectId, apprentice, sponsor);
    }

    public long getOnlineTime() {
        return _onlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        _onlineTime = onlineTime;
    }

    public void resetBonus() {
        _onlineTime = 0;
        getDAO(PlayerVariablesDAO.class).resetClaimedClanReward();
    }

    public int getOnlineStatus() {
        return !isOnline() ? 0 : _onlineTime >= (Config.ALT_CLAN_MEMBERS_TIME_FOR_BONUS) ? 2 : 1;
    }

    public boolean isRewardClaimed(ClanRewardType type) {
        final int claimedRewards = _player.getClaimedClanRewards(ClanRewardType.getDefaultMask());
        return (claimedRewards & type.getMask()) == type.getMask();
    }

    public void setRewardClaimed(ClanRewardType type) {
        int claimedRewards = _player.getClaimedClanRewards(ClanRewardType.getDefaultMask());
        claimedRewards |= type.getMask();
        _player.setClaimedClanRewards(claimedRewards);
        _player.storeVariables();
    }
}
