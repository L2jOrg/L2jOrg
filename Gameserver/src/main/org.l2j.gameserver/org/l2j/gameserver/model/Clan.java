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
package org.l2j.gameserver.model;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.ClanDAO;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.database.data.ClanData;
import org.l2j.gameserver.data.database.data.ClanMember;
import org.l2j.gameserver.data.database.data.ClanSkillData;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.SocialStatus;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanJoin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanLeaderChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanLeft;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanLvlUp;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.interfaces.INamable;
import org.l2j.gameserver.model.item.container.ClanWarehouse;
import org.l2j.gameserver.model.item.container.Warehouse;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.pledge.*;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.ClanSettings;
import org.l2j.gameserver.util.EnumIntBitmask;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.isAlphaNumeric;
import static org.l2j.gameserver.network.SystemMessageId.CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public class Clan implements IIdentifiable, INamable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Clan.class);

    /**
     * Clan leaved ally
     */
    public static final int PENALTY_TYPE_CLAN_LEAVED = 1;
    /**
     * Clan was dismissed from ally
     */
    public static final int PENALTY_TYPE_CLAN_DISMISSED = 2;
    /**
     * Leader clan dismiss clan from ally
     */
    public static final int PENALTY_TYPE_DISMISS_CLAN = 3;

    // Ally Penalty Types
    /**
     * Leader clan dissolve ally
     */
    public static final int PENALTY_TYPE_DISSOLVE_ALLY = 4;

    private static final int MAX_NOTICE_LENGTH = 8192;
    private final IntMap<ClanMember> members = new CHashIntMap<>();
    private final Warehouse warehouse = new ClanWarehouse(this);
    private final IntMap<ClanWar> _atWarWith = new CHashIntMap<>();
    private final IntMap<Skill> _skills = new CHashIntMap<>();
    private final IntMap<RankPrivs> privs = new CHashIntMap<>();
    private final AtomicInteger _siegeKills = new AtomicInteger();
    private final AtomicInteger _siegeDeaths = new AtomicInteger();

    private ClanMember leader;
    private int _hideoutId;
    private String notice;
    private boolean noticeEnabled = false;
    private ClanRewardBonus _lastMembersOnlineBonus = null;
    private ClanRewardBonus _lastHuntingBonus = null;

    private final ClanData data;

    public Clan(ClanData data) {
        this.data = data;
        initializePrivs();
        restore();

        warehouse.restore();

        final ClanRewardBonus availableOnlineBonus = ClanRewardType.MEMBERS_ONLINE.getAvailableBonus(this);
        if ((_lastMembersOnlineBonus == null) && (availableOnlineBonus != null)) {
            _lastMembersOnlineBonus = availableOnlineBonus;
        }

        final ClanRewardBonus availableHuntingBonus = ClanRewardType.HUNTING_MONSTERS.getAvailableBonus(this);
        if ((_lastHuntingBonus == null) && (availableHuntingBonus != null)) {
            _lastHuntingBonus = availableHuntingBonus;
        }
    }

    private void restore() {
        if (data.getAllyPenaltyExpiryTime() < System.currentTimeMillis()) {
            setAllyPenaltyExpiryTime(0, 0);
        }

        if ((data.getCharPenaltyExpiryTime() + ClanSettings.daysToJoinClan() * 86400000L) < System.currentTimeMillis()) // 24*60*60*1000 = 86400000
        {
            setCharPenaltyExpiryTime(0);
        }

        getDAO(PlayerDAO.class).findClanMembers(data.getId()).forEach(member -> {
            member.setClan(this);
            if(member.getObjectId() == data.getLeaderId()) {
                setLeader(member);
            } else {
                addClanMember(member);
            }
        });

        restoreRankPrivs();
        restoreSkills();
        restoreNotice();
        ClanRewardManager.getInstance().checkArenaProgress(this);
    }


    /**
     * Called only if a new clan is created
     *
     * @param clanId   A valid clan Id to create
     * @param clanName A valid clan name
     */
    public Clan(int clanId, String clanName) {
        data = new ClanData();
        data.setId(clanId);
        data.setName(clanName);
        initializePrivs();
    }

    @Override
    public int getId() {
        return data.getId();
    }

    /**
     * @return Returns the leaderId.
     */
    public int getLeaderId() {
        return leader != null ? leader.getObjectId() : 0;
    }

    /**
     * @return ClanMember of clan leader.
     */
    public ClanMember getLeader() {
        return leader;
    }

    /**
     * @param leader the leader to set.
     */
    public void setLeader(ClanMember leader) {
        this.leader = leader;
        members.put(leader.getObjectId(), leader);
        data.setLeader(leader.getObjectId());
    }

    public String getLeaderName() {
        if (isNull(leader)) {
            LOGGER.atWarn().addArgument(data::getName).log("Clan {} without clan leader!");
            return "";
        }
        return leader.getName();
    }

    @Override
    public String getName() {
        return data.getName();
    }

    /**
     * Adds a clan member to the clan.
     *
     * @param member the clan member.
     */
    private void addClanMember(ClanMember member) {
        members.put(member.getObjectId(), member);
    }

    /**
     * Adds a clan member to the clan.<br>
     * Using a different constructor, to make it easier to read.
     *
     * @param player the clan member
     */
    public void addClanMember(Player player) {
        final ClanMember member = ClanMember.of(this, player);
        // store in memory
        addClanMember(member);
        player.setClan(this);
        updateSocialStatus(player);
        player.sendPacket(new PledgeShowMemberListUpdate(player));
        player.sendPacket(new PledgeSkillList(this));

        addSkillEffects(player);

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanJoin(member, this));
    }

    /**
     * @param name the name of the required clan member.
     * @return the clan member for a given name.
     */
    public ClanMember getClanMember(String name) {
        for (ClanMember temp : members.values()) {
            if (temp.getName().equals(name)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * @param objectId the required clan member object Id.
     * @return the clan member for a given {@code objectId}.
     */
    public ClanMember getClanMember(int objectId) {
        return members.get(objectId);
    }

    /**
     * @param objectId           the object Id of the member that will be removed.
     * @param clanJoinExpiryTime time penalty to join a clan.
     */
    public void removeClanMember(int objectId, long clanJoinExpiryTime) {
        final ClanMember exMember = members.remove(objectId);
        if (exMember == null) {
            LOGGER.warn("Member Object ID: {} not found in clan while trying to remove",  objectId);
            return;
        }

        if (exMember.getApprentice() != 0) {
            final ClanMember apprentice = getClanMember(exMember.getApprentice());
            if (apprentice != null) {
                if (apprentice.getPlayerInstance() != null) {
                    apprentice.getPlayerInstance().setSponsor(0);
                } else {
                    apprentice.setApprenticeAndSponsor(0, 0);
                }

                apprentice.saveApprenticeAndSponsor(0, 0);
            }
        }
        if (exMember.getSponsor() != 0) {
            final ClanMember sponsor = getClanMember(exMember.getSponsor());
            if (sponsor != null) {
                if (sponsor.getPlayerInstance() != null) {
                    sponsor.getPlayerInstance().setApprentice(0);
                } else {
                    sponsor.setApprenticeAndSponsor(0, 0);
                }

                sponsor.saveApprenticeAndSponsor(0, 0);
            }
        }
        exMember.saveApprenticeAndSponsor(0, 0);
        if (CharacterSettings.removeCastleCirclets()) {
            CastleManager.getInstance().removeCirclet(exMember, getCastleId());
        }
        if (exMember.isOnline()) {
            final Player player = exMember.getPlayerInstance();
            if (!player.isNoble()) {
                player.setTitle("");
            }
            player.setApprentice(0);
            player.setSponsor(0);

            if (player.isClanLeader()) {
                SiegeManager.getInstance().removeSiegeSkills(player);
                player.setClanCreateExpiryTime(System.currentTimeMillis() + ClanSettings.daysToCreateClan() * 86400000L); // 24*60*60*1000 = 86400000
            }

            // remove Clan skills from Player
            removeSkillEffects(player);
            player.getEffectList().stopSkillEffects(true, CommonSkill.CLAN_ADVENT.getId());

            // remove Residential skills
            if (player.getClan().getCastleId() > 0) {
                CastleManager.getInstance().getCastleByOwner(player.getClan()).removeResidentialSkills(player);
            }

            player.sendSkillList();
            player.setClan(null);

            player.setClanJoinExpiryTime(clanJoinExpiryTime);

            updateSocialStatus(player);
            player.broadcastUserInfo();
            player.sendPacket(PledgeShowMemberListDeleteAll.STATIC_PACKET);
        } else {
            removeMemberInDatabase(exMember, clanJoinExpiryTime, getLeaderId() == objectId ? System.currentTimeMillis() + ClanSettings.daysToCreateClan() * 86400000L : 0);
        }

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanLeft(exMember, this));
    }

    public Collection<ClanMember> getMembers() {
        return members.values();
    }

    public int getMembersCount() {
        return members.size();
    }

    /**
     * @return the maximum number of members allowed.
     */
    public int getMaxNrOfMembers() {
        return switch (data.getLevel()) {
            case 3 -> 30;
            case 2 -> 20;
            case 1 -> 15;
            case 0 -> 10;
            default -> 40;
        };
    }

    /**
     * @param exclude the object Id to exclude from list.
     * @return all online members excluding the one with object id {code exclude}.
     */
    public List<Player> getOnlineMembers(int exclude) {
        //@formatter:off
        return members.values().stream()
                .filter(member -> member.getObjectId() != exclude)
                .filter(ClanMember::isOnline)
                .map(ClanMember::getPlayerInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        //@formatter:on
    }

    /**
     * @return the online clan member count.
     */
    public int getOnlineMembersCount() {
        //@formatter:off
        return (int) members.values().stream()
                .filter(ClanMember::isOnline)
                .count();
        //@formatter:on
    }

    public void forEachMember(Consumer<ClanMember> action) {
        members.values().forEach(action);
    }

    public void forEachOnlineMember(Consumer<Player> action) {
        onlineMembersStream().forEach(action);
    }

    public void forEachOnlineMember(Consumer<Player> action, Predicate<Player> filter) {
        onlineMembersStream().filter(filter).forEach(action);
    }

    private Stream<Player> onlineMembersStream() {
        return members.values().stream().filter(ClanMember::isOnline).map(ClanMember::getPlayerInstance);
    }

    public int getAllyId() {
        return data.getAllyId();
    }

    public void setAllyId(int allyId) {
        data.setAllyId(allyId);
    }

    public String getAllyName() {
        return data.getAllyName();
    }

    public void setAllyName(String allyName) {
        data.setAllyName(allyName);
    }

    public int getAllyCrestId() {
        return data.getAllyCrest();
    }

    public void setAllyCrestId(int allyCrestId) {
        data.setAllyCrest(allyCrestId);
    }

    public int getLevel() {
        return data.getLevel();
    }

    /**
     * Sets the clan level and updates the clan forum if it's needed.
     *
     * @param level the clan level to be set.
     */
    public void setLevel(int level) {
        data.setLevel(level);
    }

    public int getCastleId() {
        return data.getCastle();
    }

    public void setCastleId(int castleId) {
        data.setCastle(castleId);
    }

    /**
     * @return the hideout Id for this clan if owns a hideout, zero otherwise.
     */
    public int getHideoutId() {
        return _hideoutId;
    }

    /**
     * @param hideoutId the hideout Id to set.
     */
    public void setHideoutId(int hideoutId) {
        _hideoutId = hideoutId;
    }

    public int getCrestId() {
        return data.getCrest();
    }

    /**
     * @param crestId the Id of the clan crest to be set.
     */
    public void setCrestId(int crestId) {
        data.setCrest(crestId);
    }

    public int getCrestLargeId() {
        return data.getCrestLarge();
    }

    public void setCrestLargeId(int crestLargeId) {
        data.setCrestLarge(crestLargeId);
    }

    /**
     * @param id the Id of the player to be verified.
     * @return {code true} if the player belongs to the clan.
     */
    public boolean isMember(int id) {
        return ((id != 0) && members.containsKey(id));
    }

    public int getBloodAllianceCount() {
        return data.getBloodAllianceCount();
    }

    /**
     * Increase Blood Alliance count by config predefined count and updates the database.
     */
    public void increaseBloodAllianceCount() {
        data.setBloodAllianceCount(data.getBloodAllianceCount() + SiegeManager.getInstance().getBloodAllianceReward());
    }

    /**
     * Reset the Blood Alliance count to zero and updates the database.
     */
    public void resetBloodAllianceCount() {
        data.setBloodAllianceCount(0);
    }

    public void updateInDB() {
        getDAO(ClanDAO.class).save(data);
    }

    public void updateClanInDB() {
        getDAO(ClanDAO.class).save(data);
    }

    public void store() {
        getDAO(ClanDAO.class).save(data);
    }

    private void removeMemberInDatabase(ClanMember member, long clanJoinExpiryTime, long clanCreateExpiryTime) {
        var characterDAO = getDAO(PlayerDAO.class);
        characterDAO.deleteClanInfoOfMember(member.getObjectId(), clanJoinExpiryTime, clanCreateExpiryTime);
        characterDAO.deleteApprentice(member.getObjectId());
        characterDAO.deleteSponsor(member.getObjectId());
    }

    private void restoreNotice() {
        getDAO(ClanDAO.class).withNoticesDo(data.getId(), resutSet -> {
            try {
                if(resutSet.next()) {
                    noticeEnabled = resutSet.getBoolean("enabled");
                    notice = resutSet.getString("notice");
                }
            } catch (Exception e) {
                LOGGER.error("Error restoring clan notice", e);
            }
        });
    }

    private void storeNotice(String notice, boolean enabled) {
        if (isNull(notice)) {
            notice = "";
        }

        if (notice.length() > MAX_NOTICE_LENGTH) {
            notice = notice.substring(0, MAX_NOTICE_LENGTH - 1);
        }

        this.notice = notice;
        noticeEnabled = enabled;
        getDAO(ClanDAO.class).saveNotice(data.getId(), notice, enabled);
    }

    public boolean isNoticeEnabled() {
        return noticeEnabled;
    }

    public void setNoticeEnabled(boolean enabled) {
        storeNotice(notice, enabled);
    }

    public String getNotice() {
        if (notice == null) {
            return "";
        }
        return notice;
    }

    public void setNotice(String notice) {
        storeNotice(notice, noticeEnabled);
    }

    private void restoreSkills() {
        for (ClanSkillData skillData : getDAO(ClanDAO.class).findSkillsByClan(data.getId())) {
            var skill = SkillEngine.getInstance().getSkill(skillData.getId(), skillData.getLevel());
            _skills.put(skill.getId(), skill);
        }
    }

    public final Skill[] getAllSkills() {
        return _skills.values().toArray(Skill[]::new);
    }

    public IntMap<Skill> getSkills() {
        return _skills;
    }

    /**
     * Used to add a new skill to the list, send a packet to all online clan members, update their stats and store it in db
     */
    public void addNewSkill(Skill newSkill) {
        Skill oldSkill;
        if (newSkill != null) {
            oldSkill = _skills.put(newSkill.getId(), newSkill);

            if(nonNull(oldSkill)) {
                getDAO(ClanDAO.class).updateClanSkill(data.getId(), oldSkill.getId(), newSkill.getLevel());
            } else {
                getDAO(ClanDAO.class).saveClanSkill(data.getId(), newSkill.getId(), newSkill.getLevel());
            }

            final SystemMessage sm = getSystemMessage(SystemMessageId.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED);
            sm.addSkillName(newSkill.getId());

            for (ClanMember temp : members.values()) {
                if ((temp != null) && (temp.getPlayerInstance() != null) && temp.isOnline()) {
                    temp.getPlayerInstance().addSkill(newSkill, false); // Skill is not saved to player DB
                    temp.getPlayerInstance().sendPacket(new PledgeSkillAdd(newSkill.getId(), newSkill.getLevel()));
                    temp.getPlayerInstance().sendPacket(sm);
                    temp.getPlayerInstance().sendSkillList();
                }
            }
        }
    }

    public void removeSkill(int skillId) {
        if(_skills.containsKey(skillId)) {
            final var oldSkill = _skills.remove(skillId);
            getDAO(ClanDAO.class).removeSkill(getId(), skillId);
            final var skillDelete = new PledgeSkillDelete(oldSkill);
            forEachOnlineMember(player -> {
                player.removeSkill(oldSkill, false);
                player.sendPacket(skillDelete);
            });
        }

    }

    public void addSkillEffects(Player player) {
        if (player == null) {
            return;
        }

        var socialStatus = player.getSocialStatus();

        for (Skill skill : _skills.values()) {
            var skillLearn = SkillTreesData.getInstance().getPledgeSkill(skill.getId(), skill.getLevel());
            if (hasSocialStatusRequirements(socialStatus, skillLearn)) {
                player.addSkill(skill, false); // Skill is not saved to player DB
            }
        }

        if (data.getReputation() < 0) {
            skillsStatus(player, true);
        }
    }

    private boolean hasSocialStatusRequirements(SocialStatus socialStatus, SkillLearn skillLearn) {
        return skillLearn == null || skillLearn.getSocialClass() == null || socialStatus.compareTo(skillLearn.getSocialClass()) >= 0;
    }

    public void removeSkillEffects(Player player) {
        if (player == null) {
            return;
        }

        for (Skill skill : _skills.values()) {
            player.removeSkill(skill, false); // Skill is not saved to player DB
        }
    }

    public void skillsStatus(Player player, boolean disable) {
        if (player == null) {
            return;
        }

        for (Skill skill : _skills.values()) {
            if (disable) {
                player.disableSkill(skill, -1);
            } else {
                player.enableSkill(skill);
            }
        }
    }

    public void broadcastToOnlineAllyMembers(ServerPacket packet) {
        ClanEngine.getInstance().getClanAllies(getAllyId()).forEach(c -> c.broadcastToOnlineMembers(packet));
    }

    public void broadcastToOnlineMembers(ServerPacket packet) {
        onlineMembersStream().forEach(packet::sendTo);
    }

    public void broadcastCSToOnlineMembers(CreatureSay packet, Player broadcaster) {
        for (ClanMember member : members.values()) {
            if ((member != null) && member.isOnline() && !BlockList.isBlocked(member.getPlayerInstance(), broadcaster)) {
                member.getPlayerInstance().sendPacket(packet);
            }
        }
    }

    public void broadcastToOtherOnlineMembers(ServerPacket packet, Player player) {
        for (ClanMember member : members.values()) {
            if ((member != null) && member.isOnline() && (member.getPlayerInstance() != player)) {
                member.getPlayerInstance().sendPacket(packet);
            }
        }
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public boolean isAtWarWith(int clanId) {
        return _atWarWith.containsKey(clanId);
    }

    public boolean isAtWarWith(Clan clan) {
        if (clan == null) {
            return false;
        }
        return _atWarWith.containsKey(clan.getId());
    }

    public boolean isAtWar() {
        return !_atWarWith.isEmpty();
    }

    public IntMap<ClanWar> getWarList() {
        return _atWarWith;
    }

    public void broadcastClanStatus() {
        for (Player member : getOnlineMembers(0)) {
            member.sendPacket(PledgeShowMemberListDeleteAll.STATIC_PACKET);
            PledgeShowMemberListAll.sendAllTo(member);
        }
    }

    private void restoreRankPrivs() {
        getDAO(ClanDAO.class).withClanPrivs(data.getId(), resultSet -> {
            try{
                while (resultSet.next()) {
                    var rank = resultSet.getInt("rank");
                    var privileges = resultSet.getInt("privs");
                    if (rank == -1) {
                        continue;
                    }
                    privs.get(rank).setPrivs(privileges);
                }
            }catch (Exception e) {
                LOGGER.error("Error restoring clan privs by rank", e);
            }
        });
    }

    public void initializePrivs() {
        for (int i = 1; i < 10; i++) {
            privs.put(i, new RankPrivs(i, 0, new EnumIntBitmask<>(ClanPrivilege.class, false)));
        }
    }

    public EnumIntBitmask<ClanPrivilege> getRankPrivs(int rank) {
        return privs.get(rank) != null ? privs.get(rank).getPrivs() : new EnumIntBitmask<>(ClanPrivilege.class, false);
    }

    public void setRankPrivs(int rank, int privs) {
        if (this.privs.get(rank) != null) {
            this.privs.get(rank).setPrivs(privs);

            getDAO(ClanDAO.class).saveClanPrivs(data.getId(), rank, privs);

            for (ClanMember cm : members.values()) {
                if (cm.isOnline()) {
                    if (cm.getPowerGrade() == rank) {
                        if (cm.getPlayerInstance() != null) {
                            cm.getPlayerInstance().getClanPrivileges().setBitmask(privs);
                            cm.getPlayerInstance().sendPacket(new UserInfo(cm.getPlayerInstance()));
                        }
                    }
                }
            }
            broadcastClanStatus();
        } else {
            this.privs.put(rank, new RankPrivs(rank, 0, privs));
            getDAO(ClanDAO.class).saveClanPrivs(data.getId(), rank, privs);
        }
    }

    /**
     * @return all RankPrivs.
     */
    public final RankPrivs[] getAllRankPrivs() {
        return privs.values().toArray(RankPrivs[]::new);
    }

    public synchronized void addReputationScore(int value, boolean save) {
        setReputationScore(data.getReputation() + value, save);
    }

    public synchronized void takeReputationScore(int value, boolean save) {
        setReputationScore(data.getReputation() - value, save);
    }

    private void setReputationScore(int value, boolean save) {
        if ((data.getReputation() >= 0) && (value < 0)) {
            broadcastToOnlineMembers(getSystemMessage(SystemMessageId.SINCE_THE_CLAN_REPUTATION_HAS_DROPPED_BELOW_0_YOUR_CLAN_SKILL_S_WILL_BE_DE_ACTIVATED));
            for (ClanMember member : members.values()) {
                if (member.isOnline() && (member.getPlayerInstance() != null)) {
                    skillsStatus(member.getPlayerInstance(), true);
                }
            }
        } else if ((data.getReputation() < 0) && (value >= 0)) {
            broadcastToOnlineMembers(getSystemMessage(SystemMessageId.CLAN_SKILLS_WILL_NOW_BE_ACTIVATED_SINCE_THE_CLAN_REPUTATION_IS_1_OR_HIGHER));
            for (ClanMember member : members.values()) {
                if (member.isOnline() && (member.getPlayerInstance() != null)) {
                    skillsStatus(member.getPlayerInstance(), false);
                }
            }
        }

        if (value > 100000000) {
            value = 100000000;
        } else if (value < -100000000) {
            value = -100000000;
        }
        data.setReputation(value);
        broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
        if (save) {
            updateInDB();
        }
    }

    public int getReputationScore() {
        return data.getReputation();
    }

    /**
     * @param player the clan inviting player.
     * @param target     the invited player.
     * @return {core true} if activeChar and target meet various conditions to join a clan.
     */
    public boolean checkClanJoinCondition(Player player, Player target) {
        if (player == null) {
            return false;
        }
        if (!player.hasClanPrivilege(ClanPrivilege.CL_JOIN_CLAN)) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return false;
        }
        if (target == null) {
            player.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return false;
        }
        if (player.getObjectId() == target.getObjectId()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN);
            return false;
        }
        if (data.getCharPenaltyExpiryTime() > System.currentTimeMillis()) {
            player.sendPacket(SystemMessageId.AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER);
            return false;
        }
        if (target.getClanId() != 0) {
            final SystemMessage sm = getSystemMessage(SystemMessageId.S1_IS_ALREADY_A_MEMBER_OF_ANOTHER_CLAN);
            sm.addString(target.getName());
            player.sendPacket(sm);
            return false;
        }
        if (target.getClanJoinExpiryTime() > System.currentTimeMillis()) {
            final SystemMessage sm = getSystemMessage(SystemMessageId.C1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_CLAN);
            sm.addString(target.getName());
            player.sendPacket(sm);
            return false;
        }

        if (getMembersCount() >= getMaxNrOfMembers()) {
            player.sendPacket(getSystemMessage(SystemMessageId.S1_IS_FULL_AND_CANNOT_ACCEPT_ADDITIONAL_CLAN_MEMBERS_AT_THIS_TIME).addString(data.getName()));
            return false;
        }
        return true;
    }

    /**
     * @param activeChar the clan inviting player.
     * @param target     the invited player.
     * @return {core true} if activeChar and target meet various conditions to join a clan.
     */
    public boolean checkAllyJoinCondition(Player activeChar, Player target) {
        if (activeChar == null) {
            return false;
        }
        if ((activeChar.getAllyId() == 0) || !activeChar.isClanLeader() || (activeChar.getClanId() != activeChar.getAllyId())) {
            activeChar.sendPacket(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
            return false;
        }
        final Clan leaderClan = activeChar.getClan();
        if (leaderClan.getAllyPenaltyExpiryTime() > System.currentTimeMillis()) {
            if (leaderClan.getAllyPenaltyType() == PENALTY_TYPE_DISMISS_CLAN) {
                activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_ACCEPT_ANY_CLAN_WITHIN_A_DAY_AFTER_EXPELLING_ANOTHER_CLAN);
                return false;
            }
        }
        if (target == null) {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return false;
        }
        if (activeChar.getObjectId() == target.getObjectId()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN);
            return false;
        }
        if (target.getClan() == null) {
            activeChar.sendPacket(SystemMessageId.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
            return false;
        }
        if (!target.isClanLeader()) {
            final SystemMessage sm = getSystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER);
            sm.addString(target.getName());
            activeChar.sendPacket(sm);
            return false;
        }
        final Clan targetClan = target.getClan();
        if (target.getAllyId() != 0) {
            final SystemMessage sm = getSystemMessage(SystemMessageId.S1_CLAN_IS_ALREADY_A_MEMBER_OF_S2_ALLIANCE);
            sm.addString(targetClan.getName());
            sm.addString(targetClan.getAllyName());
            activeChar.sendPacket(sm);
            return false;
        }
        if (targetClan.getAllyPenaltyExpiryTime() > System.currentTimeMillis()) {
            if (targetClan.getAllyPenaltyType() == PENALTY_TYPE_CLAN_LEAVED) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.S1_CLAN_CANNOT_JOIN_THE_ALLIANCE_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_ALLIANCE);
                sm.addString(target.getClan().getName());
                sm.addString(target.getClan().getAllyName());
                activeChar.sendPacket(sm);
                return false;
            }
            if (targetClan.getAllyPenaltyType() == PENALTY_TYPE_CLAN_DISMISSED) {
                activeChar.sendPacket(SystemMessageId.A_CLAN_THAT_HAS_WITHDRAWN_OR_BEEN_EXPELLED_CANNOT_ENTER_INTO_AN_ALLIANCE_WITHIN_ONE_DAY_OF_WITHDRAWAL_OR_EXPULSION);
                return false;
            }
        }
        if (activeChar.isInsideZone(ZoneType.SIEGE) && target.isInsideZone(ZoneType.SIEGE)) {
            activeChar.sendPacket(SystemMessageId.THE_OPPOSING_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE);
            return false;
        }
        if (leaderClan.isAtWarWith(targetClan.getId())) {
            activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_ALLY_WITH_A_CLAN_YOU_ARE_CURRENTLY_AT_WAR_WITH_THAT_WOULD_BE_DIABOLICAL_AND_TREACHEROUS);
            return false;
        }

        if (ClanEngine.getInstance().getClanAllies(activeChar.getAllyId()).size() >= ClanSettings.maxClansInAlly()) {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_LIMIT);
            return false;
        }

        return true;
    }

    public long getAllyPenaltyExpiryTime() {
        return data.getAllyPenaltyExpiryTime();
    }

    public int getAllyPenaltyType() {
        return data.getAllyPenaltyType();
    }

    public void setAllyPenaltyExpiryTime(long expiryTime, int penaltyType) {
        data.setAllyPenaltyExpiryTime(expiryTime);
        data.setAllyPenaltyType(penaltyType);
    }

    public long getCharPenaltyExpiryTime() {
        return data.getCharPenaltyExpiryTime();
    }

    public void setCharPenaltyExpiryTime(long time) {
        data.setCharPenaltyExpiryTime(time);
    }

    public long getDissolvingExpiryTime() {
        return data.getDissolvingExpiryTime();
    }

    public void setDissolvingExpiryTime(long time) {
        data.setDissolvingExpiryTime(time);
    }

    public void createAlly(Player player, String allyName) {
        if (null == player) {
            return;
        }

        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES);
            return;
        }
        if (data.getAllyId() != 0) {
            player.sendPacket(SystemMessageId.YOU_ALREADY_BELONG_TO_ANOTHER_ALLIANCE);
            return;
        }
        if (getLevel() < 5) {
            player.sendPacket(SystemMessageId.TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER);
            return;
        }
        if ((data.getAllyPenaltyExpiryTime() > System.currentTimeMillis()) && (getAllyPenaltyType() == PENALTY_TYPE_DISSOLVE_ALLY)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CREATE_A_NEW_ALLIANCE_WITHIN_1_DAY_OF_DISSOLUTION);
            return;
        }
        if (data.getDissolvingExpiryTime() > System.currentTimeMillis()) {
            player.sendPacket(SystemMessageId.AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_NO_ALLIANCE_CAN_BE_CREATED);
            return;
        }
        if (!isAlphaNumeric(allyName)) {
            player.sendPacket(SystemMessageId.INCORRECT_ALLIANCE_NAME_PLEASE_TRY_AGAIN);
            return;
        }
        if ((allyName.length() > 16) || (allyName.length() < 2)) {
            player.sendPacket(SystemMessageId.INCORRECT_LENGTH_FOR_AN_ALLIANCE_NAME);
            return;
        }
        if (ClanEngine.getInstance().isAllyExists(allyName)) {
            player.sendPacket(SystemMessageId.THAT_ALLIANCE_NAME_ALREADY_EXISTS);
            return;
        }

        setAllyId(data.getId());
        setAllyName(allyName.trim());
        setAllyPenaltyExpiryTime(0, 0);
        updateClanInDB();

        player.sendPacket(new UserInfo(player));

        // TODO: Need correct message id
        player.sendMessage("Alliance " + allyName + " has been created.");
    }

    public void dissolveAlly(Player player) {
        if (data.getAllyId() == 0) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
            return;
        }
        if (!player.isClanLeader() || (data.getId() != data.getAllyId())) {
            player.sendPacket(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
            return;
        }
        if (player.isInsideZone(ZoneType.SIEGE)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISSOLVE_AN_ALLIANCE_WHILE_AN_AFFILIATED_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE);
            return;
        }

        broadcastToOnlineAllyMembers(getSystemMessage(SystemMessageId.THE_ALLIANCE_HAS_BEEN_DISSOLVED));

        final long currentTime = System.currentTimeMillis();
        for (Clan clan : ClanEngine.getInstance().getClanAllies(getAllyId())) {
            if (clan.getId() != getId()) {
                clan.setAllyId(0);
                clan.setAllyName(null);
                clan.setAllyPenaltyExpiryTime(0, 0);
                clan.updateClanInDB();
            }
        }

        setAllyId(0);
        setAllyName(null);
        changeAllyCrest(0, false);
        setAllyPenaltyExpiryTime(currentTime + ClanSettings.daysToCreateAllyAfterDissolved() * 86400000L, PENALTY_TYPE_DISSOLVE_ALLY); // 24*60*60*1000 = 86400000
        updateClanInDB();
    }

    public boolean levelUpClan(Player player) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return false;
        }
        if (System.currentTimeMillis() < data.getDissolvingExpiryTime()) {
            player.sendPacket(SystemMessageId.AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOUR_CLAN_LEVEL_CANNOT_BE_INCREASED);
            return false;
        }

        boolean increaseClanLevel = false;

        // Such as https://l2wiki.com/classic/Clans_%E2%80%93_Clan_Level
        switch (getLevel()) {
            case 0 -> {
                // Upgrade to 1
                if ((player.getSp() >= 1000) && (player.getAdena() >= 150000) && (members.size() >= 1)) {
                    if (player.reduceAdena("ClanLvl", 150000, player.getTarget(), true)) {
                        player.setSp(player.getSp() - 1000);
                        final SystemMessage sp = getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                        sp.addInt(1000);
                        player.sendPacket(sp);
                        increaseClanLevel = true;
                    }
                }
            }
            case 1 -> {
                // Upgrade to 2
                if ((player.getSp() >= 15000) && (player.getAdena() >= 300000) && (members.size() >= 1)) {
                    if (player.reduceAdena("ClanLvl", 300000, player.getTarget(), true)) {
                        player.setSp(player.getSp() - 15000);
                        final SystemMessage sp = getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                        sp.addInt(15000);
                        player.sendPacket(sp);
                        increaseClanLevel = true;
                    }
                }
            }
            case 2 -> {
                // Upgrade to 3
                if ((player.getSp() >= 100000) && (player.getInventory().getItemByItemId(1419) != null) && (members.size() >= 1)) {
                    // itemId 1419 == Blood Mark
                    if (player.destroyItemByItemId("ClanLvl", 1419, 100, player.getTarget(), true)) {
                        player.setSp(player.getSp() - 100000);
                        final SystemMessage sp = getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                        sp.addInt(100000);
                        player.sendPacket(sp);
                        final SystemMessage sm = getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                        sm.addItemName(1419);
                        player.sendPacket(sm);
                        increaseClanLevel = true;
                    }
                }
            }
            case 3 -> {
                // Upgrade to 4
                if ((player.getSp() >= 1000000) && (player.getInventory().getItemByItemId(1419) != null) && (members.size() >= 1)) {
                    // itemId 1419 == Blood Mark
                    if (player.destroyItemByItemId("ClanLvl", 1419, 5000, player.getTarget(), true)) {
                        player.setSp(player.getSp() - 1000000);
                        final SystemMessage sp = getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                        sp.addInt(1000000);
                        player.sendPacket(sp);
                        final SystemMessage sm = getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                        sm.addItemName(1419);
                        player.sendPacket(sm);
                        increaseClanLevel = true;
                    }
                }
            }
            case 4 -> {
                // Upgrade to 5
                if ((player.getSp() >= 5000000) && (player.getInventory().getItemByItemId(1419) != null) && (members.size() >= 1)) {
                    // itemId 1419 == Blood Mark
                    if (player.destroyItemByItemId("ClanLvl", 1419, 10000, player.getTarget(), true)) {
                        player.setSp(player.getSp() - 5000000);
                        final SystemMessage sp = getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                        sp.addInt(5000000);
                        player.sendPacket(sp);
                        final SystemMessage sm = getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                        sm.addItemName(1419);
                        player.sendPacket(sm);
                        increaseClanLevel = true;
                    }
                }
            }
            default -> {
                return false;
            }
        }

        if (!increaseClanLevel) {
            player.sendPacket(SystemMessageId.THE_CONDITIONS_NECESSARY_TO_INCREASE_THE_CLAN_S_LEVEL_HAVE_NOT_BEEN_MET);
            return false;
        }

        // the player should know that he has less sp now :p
        final UserInfo ui = new UserInfo(player, false);
        ui.addComponentType(UserInfoType.CURRENT_HPMPCP_EXP_SP);
        player.sendPacket(ui);

        player.sendItemList();

        changeLevel(getLevel() + 1);

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanLvlUp(player, this));
        return true;
    }

    public void changeLevel(int level) {
        getDAO(ClanDAO.class).updateClanLevel(data.getId(), level);
        setLevel(level);

        if (leader.isOnline()) {
            final Player leader = this.leader.getPlayerInstance();
            if (level > 4) {
                SiegeManager.getInstance().addSiegeSkills(leader);
                leader.sendPacket(SystemMessageId.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION);
            } else {
                SiegeManager.getInstance().removeSiegeSkills(leader);
            }
        }

        // notify all the members about it
        broadcastToOnlineMembers(getSystemMessage(SystemMessageId.YOUR_CLAN_S_LEVEL_HAS_INCREASED));
        broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
    }

    /**
     * Change the clan crest. If crest id is 0, crest is removed. New crest id is saved to database.
     *
     * @param crestId if 0, crest is removed, else new crest id is set and saved to database
     */
    public void changeClanCrest(int crestId) {
        if (data.getCrest() != 0) {
            CrestTable.getInstance().removeCrest(getCrestId());
        }

        setCrestId(crestId);
        getDAO(ClanDAO.class).updateClanCrest(data.getId(), crestId);
        forEachOnlineMember(Player::broadcastUserInfo);
    }

    /**
     * Change the ally crest. If crest id is 0, crest is removed. New crest id is saved to database.
     *
     * @param crestId      if 0, crest is removed, else new crest id is set and saved to database
     * @param onlyThisClan
     */
    public void changeAllyCrest(int crestId, boolean onlyThisClan) {

        if (!onlyThisClan) {
            if (data.getAllyCrest() != 0) {
                CrestTable.getInstance().removeCrest(getAllyCrestId());
            }
            getDAO(ClanDAO.class).updateAllyCrestByAlly(data.getAllyId(), crestId);
        } else {
            getDAO(ClanDAO.class).updateAllyCrest(data.getId(), crestId);
        }


        if (onlyThisClan) {
            setAllyCrestId(crestId);
            for (Player member : getOnlineMembers(0)) {
                member.broadcastUserInfo();
            }
        } else {
            for (Clan clan : ClanEngine.getInstance().getClanAllies(getAllyId())) {
                clan.setAllyCrestId(crestId);
                for (Player member : clan.getOnlineMembers(0)) {
                    member.broadcastUserInfo();
                }
            }
        }
    }

    /**
     * Change the large crest. If crest id is 0, crest is removed. New crest id is saved to database.
     *
     * @param crestId if 0, crest is removed, else new crest id is set and saved to database
     */
    public void changeLargeCrest(int crestId) {
        if (data.getCrestLarge() != 0) {
            CrestTable.getInstance().removeCrest(getCrestLargeId());
        }

        setCrestLargeId(crestId);

        getDAO(ClanDAO.class).updateClanCrestLarge(data.getId(), crestId);
        forEachOnlineMember(Player::broadcastUserInfo);
    }

    public void setNewLeaderId(int objectId, boolean storeInDb) {
        data.setNewLeader(objectId);
        if (storeInDb) {
            updateClanInDB();
        }
    }

    public int getNewLeaderId() {
        return data.getNewLeaderId();
    }

    public void setNewLeader(ClanMember member) {
        final Player newLeader = member.getPlayerInstance();
        final ClanMember exMember = leader;
        final Player exLeader = exMember.getPlayerInstance();

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanLeaderChange(exMember, member, this));

        if (exLeader != null) {
            if (exLeader.isFlying()) {
                exLeader.dismount();
            }

            if (getLevel() >= SiegeManager.getInstance().getSiegeClanMinLevel()) {
                SiegeManager.getInstance().removeSiegeSkills(exLeader);
            }
            exLeader.getClanPrivileges().clear();
            exLeader.broadcastUserInfo();
        } else {
            getDAO(PlayerDAO.class).updateClanPrivs(getLeaderId(), 0);
        }

        setLeader(member);
        if (data.getNewLeaderId() != 0) {
            setNewLeaderId(0, true);
        }
        updateClanInDB();

        if (exLeader != null) {
            updateSocialStatus(exLeader);
            exLeader.broadcastUserInfo();
            exLeader.checkItemRestriction();
        }

        if (newLeader != null) {
            updateSocialStatus(newLeader);
            newLeader.getClanPrivileges().setAll();

            if (getLevel() >= SiegeManager.getInstance().getSiegeClanMinLevel()) {
                SiegeManager.getInstance().addSiegeSkills(newLeader);
            }
            newLeader.broadcastUserInfo();
        } else {
            getDAO(PlayerDAO.class).updateClanPrivs(getLeaderId(), EnumIntBitmask.getAllBitmask(ClanPrivilege.class));
        }

        broadcastClanStatus();
        broadcastToOnlineMembers(getSystemMessage(SystemMessageId.CLAN_LEADER_PRIVILEGES_HAVE_BEEN_TRANSFERRED_TO_C1).addString(member.getName()));

        LOGGER.info("Leader of Clan: " + getName() + " changed to: " + member.getName() + " ex leader: " + exMember.getName());
    }

    public String getNewLeaderName() {
        return PlayerNameTable.getInstance().getNameById(data.getNewLeaderId());
    }

    public int getSiegeKills() {
        return _siegeKills.get();
    }

    public int getSiegeDeaths() {
        return _siegeDeaths.get();
    }

    public int addSiegeKill() {
        return _siegeKills.incrementAndGet();
    }

    public int addSiegeDeath() {
        return _siegeDeaths.incrementAndGet();
    }

    public void clearSiegeKills() {
        _siegeKills.set(0);
    }

    public void clearSiegeDeaths() {
        _siegeDeaths.set(0);
    }

    public int getWarCount() {
        return _atWarWith.size();
    }

    public void addWar(int clanId, ClanWar war) {
        _atWarWith.put(clanId, war);
    }

    public void deleteWar(int clanId) {
        _atWarWith.remove(clanId);
    }

    public ClanWar getWarWith(int clanId) {
        return _atWarWith.get(clanId);
    }

    public synchronized void addMemberOnlineTime(Player player) {
        final ClanMember clanMember = getClanMember(player.getObjectId());
        if (clanMember != null) {
            clanMember.setOnlineTime(clanMember.getOnlineTime() + (60 * 1000));
            if (clanMember.getOnlineTime() == (30 * 60 * 1000)) {
                broadcastToOnlineMembers(new PledgeShowMemberListUpdate(clanMember));
            }
        }

        final ClanRewardBonus availableBonus = ClanRewardType.MEMBERS_ONLINE.getAvailableBonus(this);
        if (availableBonus != null) {
            if (_lastMembersOnlineBonus == null) {
                _lastMembersOnlineBonus = availableBonus;
                broadcastToOnlineMembers(getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ACHIEVED_LOGIN_BONUS_LV_S1).addByte(availableBonus.getLevel()));
            } else if (_lastMembersOnlineBonus.getLevel() < availableBonus.getLevel()) {
                _lastMembersOnlineBonus = availableBonus;
                broadcastToOnlineMembers(getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ACHIEVED_LOGIN_BONUS_LV_S1).addByte(availableBonus.getLevel()));
            }
        }

        var onlineTimeForBonus = ClanSettings.onlineTimeForBonus();
        var currentMaxOnline = 0;
        for (var member : members.values()) {
            if(member.getOnlineTime() > onlineTimeForBonus) {
                currentMaxOnline++;
            }
        }

        if (getMaxOnlineMembers() < currentMaxOnline) {
            data.setMaxOnlineMembers(currentMaxOnline);
        }
    }

    public synchronized void addHuntingPoints(Player activeChar, Npc target, double value) {
        // TODO: Figure out the retail formula
        final int points = (int) value / 2960; // Reduced / 10 for Classic.
        if (points > 0) {
            data.addHuntingPoints(points);
            final ClanRewardBonus availableBonus = ClanRewardType.HUNTING_MONSTERS.getAvailableBonus(this);
            if (availableBonus != null) {
                if (_lastHuntingBonus == null) {
                    _lastHuntingBonus = availableBonus;
                    broadcastToOnlineMembers(getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ACHIEVED_HUNTING_BONUS_LV_S1).addByte(availableBonus.getLevel()));
                } else if (_lastHuntingBonus.getLevel() < availableBonus.getLevel()) {
                    _lastHuntingBonus = availableBonus;
                    broadcastToOnlineMembers(getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ACHIEVED_HUNTING_BONUS_LV_S1).addByte(availableBonus.getLevel()));
                }
            }
        }
    }

    public int getMaxOnlineMembers() {
        return data.getMaxOnlineMember();
    }

    public int getHuntingPoints() {
        return data.getHuntingPoints();
    }

    public int getPreviousMaxOnlinePlayers() {
        return data.getPrevMaxOnlineMember();
    }

    public int getPreviousHuntingPoints() {
        return data.getPrevHuntingPoints();
    }

    public boolean canClaimBonusReward(Player player, ClanRewardType type) {
        if(!isMember(player.getObjectId()) || type.getAvailableBonus(this) == null) {
            return false;
        }
        int claimedRewards = player.getClaimedClanRewards(ClanRewardType.getDefaultMask());
        return (claimedRewards & type.getMask()) != type.getMask();
    }

    public void resetClanBonus() {
        data.setPrevMaxOnlineMember(data.getMaxOnlineMember());
        data.setPrevHuntingPoints(data.getHuntingPoints());
        data.setHuntingPoints(0);
        data.setMaxOnlineMembers(0);

        members.values().forEach(ClanMember::resetBonus);
        broadcastToOnlineMembers(ExPledgeBonusMarkReset.STATIC_PACKET);
    }

    public int getArenaProgress() {
        return data.getArenaProgress();
    }

    public void setArenaProgress(int progress) {
        data.setArenaProgress((short) progress);
    }

    public void onMemberLogout(Player player) {
        var member = members.get(player.getObjectId());
        if(member != null) {
            member.logout();
        }
    }

    public void onMemberLogin(Player player) {
        var member = members.get(player.getObjectId());
        if(member != null) {
            member.login(player);

            addSkillEffects(player);
            if (player.isClanLeader()) {
                setLeader(member);

                if(data.getLevel() > 3) {
                    SiegeManager.getInstance().addSiegeSkills(player);
                }
            }

            var msg = getSystemMessage(CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME).addString(player.getName());
            broadcastToOtherOnlineMembers(msg, player);
            broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(player), player);
        }
    }

    public void onMemberLevelChanged(Player player, byte oldLevel, byte level) {
        var member = members.get(player.getObjectId());
        if(member != null) {
            broadcastToOnlineMembers(new PledgeShowMemberListUpdate(member));
            if(data.getLevel() >= 3 && oldLevel < level && member.getLastReputationLevel() < level) {
                member.setLastReputationLevel(level);
                getDAO(PlayerDAO.class).saveLastReputationLevel(data.getId(), player.getObjectId(), level);
                addLevelUpReputation(oldLevel, level);
            }
        }
    }

    private void addLevelUpReputation(byte oldLevel, byte level) {
        int reputation = 0;
        while (oldLevel < level) {
            reputation += ClanRewardManager.getInstance().getReputationBonus(++oldLevel);
        }
        if(reputation > 0) {
            addReputationScore(reputation, true);
        }
    }

    public static void updateSocialStatus(Player player) {
        SocialStatus status = SocialStatus.VAGABOND;
        var clan = player.getClan();

        if(nonNull(clan)) {
            int clanLevel = clan.getLevel();

            if(clanLevel == 4) {
                status = player.isClanLeader() ? SocialStatus.ELITE_KNIGHT : SocialStatus.APPRENTICE;
            } else if(clanLevel == 5) {
                status = player.isClanLeader() ? SocialStatus.BARON : SocialStatus.KNIGHT;
            }
        }
        player.setSocialStatus(status);
    }

    public static class RankPrivs {
        private final int _rankId;
        private final int _party; // TODO find out what this stuff means and implement it
        private final EnumIntBitmask<ClanPrivilege> _rankPrivs;

        public RankPrivs(int rank, int party, int privs) {
            _rankId = rank;
            _party = party;
            _rankPrivs = new EnumIntBitmask<>(ClanPrivilege.class, privs);
        }

        public RankPrivs(int rank, int party, EnumIntBitmask<ClanPrivilege> rankPrivs) {
            _rankId = rank;
            _party = party;
            _rankPrivs = rankPrivs;
        }

        public int getRank() {
            return _rankId;
        }

        public int getParty() {
            return _party;
        }

        public EnumIntBitmask<ClanPrivilege> getPrivs() {
            return _rankPrivs;
        }

        public void setPrivs(int privs) {
            _rankPrivs.setBitmask(privs);
        }
    }
}
