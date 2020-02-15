package org.l2j.gameserver.model;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.communitybbs.BB.Forum;
import org.l2j.gameserver.communitybbs.Manager.ForumsBBSManager;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanJoin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanLeaderChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanLeft;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanLvlUp;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.interfaces.INamable;
import org.l2j.gameserver.model.itemcontainer.ClanWarehouse;
import org.l2j.gameserver.model.itemcontainer.ItemContainer;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.variables.ClanVariables;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.PledgeSkillList.SubPledgeSkill;
import org.l2j.gameserver.network.serverpackets.pledgebonus.ExPledgeBonusMarkReset;
import org.l2j.gameserver.util.EnumIntBitmask;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.l2j.commons.util.Util.isAlphaNumeric;

public class Clan implements IIdentifiable, INamable {
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
    /**
     * Clan subunit type of Academy
     */
    public static final int SUBUNIT_ACADEMY = -1;
    /**
     * Clan subunit type of Royal Guard A
     */
    public static final int SUBUNIT_ROYAL1 = 100;
    /**
     * Clan subunit type of Royal Guard B
     */
    public static final int SUBUNIT_ROYAL2 = 200;
    // Sub-unit types
    /**
     * Clan subunit type of Order of Knights A-1
     */
    public static final int SUBUNIT_KNIGHT1 = 1001;
    /**
     * Clan subunit type of Order of Knights A-2
     */
    public static final int SUBUNIT_KNIGHT2 = 1002;
    /**
     * Clan subunit type of Order of Knights B-1
     */
    public static final int SUBUNIT_KNIGHT3 = 2001;
    /**
     * Clan subunit type of Order of Knights B-2
     */
    public static final int SUBUNIT_KNIGHT4 = 2002;
    private static final Logger LOGGER = LoggerFactory.getLogger(Clan.class);
    // SQL queries
    private static final String INSERT_CLAN_DATA = "INSERT INTO clan_data (clan_id,clan_name,clan_level,hasCastle,blood_alliance_count,blood_oath_count,ally_id,ally_name,leader_id,crest_id,crest_large_id,ally_crest_id,new_leader_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SELECT_CLAN_DATA = "SELECT * FROM clan_data where clan_id=?";
    private static final int MAX_NOTICE_LENGTH = 8192;
    private final Map<Integer, ClanMember> members = new ConcurrentHashMap<>();
    private final ItemContainer _warehouse = new ClanWarehouse(this);
    private final ConcurrentHashMap<Integer, ClanWar> _atWarWith = new ConcurrentHashMap<>();
    private final Map<Integer, Skill> _skills = new ConcurrentSkipListMap<>();
    private final Map<Integer, RankPrivs> _privs = new ConcurrentSkipListMap<>();
    private final Map<Integer, SubPledge> _subPledges = new ConcurrentSkipListMap<>();
    private final Map<Integer, Skill> _subPledgeSkills = new ConcurrentSkipListMap<>();
    private final AtomicInteger _siegeKills = new AtomicInteger();
    private final AtomicInteger _siegeDeaths = new AtomicInteger();
    private String _name;
    private int _clanId;
    private ClanMember _leader;
    private String _allyName;
    private int _allyId;
    private int _level;
    private int _castleId;
    private int _fortId;
    private int _hideoutId;
    private int _hiredGuards;
    private int _crestId;
    private int _crestLargeId;
    private int _allyCrestId;
    private int _auctionBiddedAt = 0;
    private long _allyPenaltyExpiryTime;
    private int _allyPenaltyType;
    private long _charPenaltyExpiryTime;
    private long _dissolvingExpiryTime;
    private int _bloodAllianceCount;
    private int _bloodOathCount;
    private Forum _forum;
    private int _reputationScore = 0;
    private int _rank = 0;
    private String _notice;
    private boolean _noticeEnabled = false;
    private int _newLeaderId;
    private ClanRewardBonus _lastMembersOnlineBonus = null;
    private ClanRewardBonus _lastHuntingBonus = null;

    private volatile ClanVariables _vars;

    /**
     * Called if a clan is referenced only by id. In this case all other data needs to be fetched from db
     *
     * @param clanId A valid clan Id to create and restore
     */
    public Clan(int clanId) {
        _clanId = clanId;
        initializePrivs();
        restore();
        _warehouse.restore();

        final ClanRewardBonus availableOnlineBonus = ClanRewardType.MEMBERS_ONLINE.getAvailableBonus(this);
        if ((_lastMembersOnlineBonus == null) && (availableOnlineBonus != null)) {
            _lastMembersOnlineBonus = availableOnlineBonus;
        }

        final ClanRewardBonus availableHuntingBonus = ClanRewardType.HUNTING_MONSTERS.getAvailableBonus(this);
        if ((_lastHuntingBonus == null) && (availableHuntingBonus != null)) {
            _lastHuntingBonus = availableHuntingBonus;
        }
    }

    /**
     * Called only if a new clan is created
     *
     * @param clanId   A valid clan Id to create
     * @param clanName A valid clan name
     */
    public Clan(int clanId, String clanName) {
        _clanId = clanId;
        _name = clanName;
        initializePrivs();
    }

    /**
     * @return Returns the clanId.
     */
    @Override
    public int getId() {
        return _clanId;
    }

    /**
     * @param clanId The clanId to set.
     */
    public void setClanId(int clanId) {
        _clanId = clanId;
    }

    /**
     * @return Returns the leaderId.
     */
    public int getLeaderId() {
        return _leader != null ? _leader.getObjectId() : 0;
    }

    /**
     * @return ClanMember of clan leader.
     */
    public ClanMember getLeader() {
        return _leader;
    }

    /**
     * @param leader the leader to set.
     */
    public void setLeader(ClanMember leader) {
        _leader = leader;
        members.put(leader.getObjectId(), leader);
    }

    /**
     * @return the clan leader's name.
     */
    public String getLeaderName() {
        if (_leader == null) {
            LOGGER.warn("Clan " + _name + " without clan leader!");
            return "";
        }
        return _leader.getName();
    }

    /**
     * @return the clan name.
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        _name = name;
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
        final ClanMember member = new ClanMember(this, player);
        // store in memory
        addClanMember(member);
        member.setPlayerInstance(player);
        player.setClan(this);
        player.setPledgeClass(ClanMember.calculatePledgeClass(player));
        player.sendPacket(new PledgeShowMemberListUpdate(player));
        player.sendPacket(new PledgeSkillList(this));

        addSkillEffects(player);

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanJoin(member, this));
    }

    /**
     * Updates player status in clan.
     *
     * @param player the player to be updated.
     */
    public void updateClanMember(Player player) {
        final ClanMember member = new ClanMember(player.getClan(), player);
        if (player.isClanLeader()) {
            setLeader(member);
        }

        addClanMember(member);
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
            LOGGER.warn("Member Object ID: " + objectId + " not found in clan while trying to remove");
            return;
        }
        final int leadssubpledge = getLeaderSubPledge(objectId);
        if (leadssubpledge != 0) {
            // Sub-unit leader withdraws, position becomes vacant and leader
            // should appoint new via NPC
            getSubPledge(leadssubpledge).setLeaderId(0);
            updateSubPledgeInDB(leadssubpledge);
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
        if (Config.REMOVE_CASTLE_CIRCLETS) {
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
                player.setClanCreateExpiryTime(System.currentTimeMillis() + (Config.ALT_CLAN_CREATE_DAYS * 86400000)); // 24*60*60*1000 = 86400000
            }

            // remove Clan skills from Player
            removeSkillEffects(player);
            player.getEffectList().stopSkillEffects(true, CommonSkill.CLAN_ADVENT.getId());

            // remove Residential skills
            if (player.getClan().getCastleId() > 0) {
                CastleManager.getInstance().getCastleByOwner(player.getClan()).removeResidentialSkills(player);
            }
            if (player.getClan().getFortId() > 0) {
                FortDataManager.getInstance().getFortByOwner(player.getClan()).removeResidentialSkills(player);
            }
            player.sendSkillList();

            player.setClan(null);

            // players leaving from clan academy have no penalty
            if (exMember.getPledgeType() != -1) {
                player.setClanJoinExpiryTime(clanJoinExpiryTime);
            }

            player.setPledgeClass(ClanMember.calculatePledgeClass(player));
            player.broadcastUserInfo();
            // disable clan tab
            player.sendPacket(PledgeShowMemberListDeleteAll.STATIC_PACKET);
        } else {
            removeMemberInDatabase(exMember, clanJoinExpiryTime, getLeaderId() == objectId ? System.currentTimeMillis() + (Config.ALT_CLAN_CREATE_DAYS * 86400000) : 0);
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

    public int getSubPledgeMembersCount(int subpl) {
        int result = 0;
        for (ClanMember temp : members.values()) {
            if (temp.getPledgeType() == subpl) {
                result++;
            }
        }
        return result;
    }

    /**
     * @param pledgeType the Id of the pledge type.
     * @return the maximum number of members allowed for a given {@code pledgeType}.
     */
    public int getMaxNrOfMembers(int pledgeType) {
        int limit = 0;

        switch (pledgeType) {
            case 0: {
                switch (_level) {
                    case 3: {
                        limit = 30;
                        break;
                    }
                    case 2: {
                        limit = 20;
                        break;
                    }
                    case 1: {
                        limit = 15;
                        break;
                    }
                    case 0: {
                        limit = 10;
                        break;
                    }
                    default: {
                        limit = 40;
                        break;
                    }
                }
                break;
            }
            case -1: {
                limit = 20;
                break;
            }
            case 100:
            case 200: {
                switch (_level) {
                    case 11: {
                        limit = 30;
                        break;
                    }
                    default: {
                        limit = 20;
                        break;
                    }
                }
                break;
            }
            case 1001:
            case 1002:
            case 2001:
            case 2002: {
                switch (_level) {
                    case 9:
                    case 10:
                    case 11: {
                        limit = 25;
                        break;
                    }
                    default: {
                        limit = 10;
                        break;
                    }
                }
                break;
            }
            default: {
                break;
            }
        }

        return limit;
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

    public void forEachOnlineMember(Consumer<Player> action, Predicate<Player> filter) {
        members.values().stream().filter(ClanMember::isOnline).map(ClanMember::getPlayerInstance).filter(filter).forEach(action);
    }

    /**
     * @return the alliance Id.
     */
    public int getAllyId() {
        return _allyId;
    }

    /**
     * @param allyId The allyId to set.
     */
    public void setAllyId(int allyId) {
        _allyId = allyId;
    }

    /**
     * @return the alliance name.
     */
    public String getAllyName() {
        return _allyName;
    }

    /**
     * @param allyName The allyName to set.
     */
    public void setAllyName(String allyName) {
        _allyName = allyName;
    }

    /**
     * @return the alliance crest Id.
     */
    public int getAllyCrestId() {
        return _allyCrestId;
    }

    /**
     * @param allyCrestId the alliance crest Id to be set.
     */
    public void setAllyCrestId(int allyCrestId) {
        _allyCrestId = allyCrestId;
    }

    /**
     * @return the clan level.
     */
    public int getLevel() {
        return _level;
    }

    /**
     * Sets the clan level and updates the clan forum if it's needed.
     *
     * @param level the clan level to be set.
     */
    public void setLevel(int level) {
        _level = level;
        if ((_level >= 2) && (_forum == null) && Config.ENABLE_COMMUNITY_BOARD) {
            final Forum forum = ForumsBBSManager.getInstance().getForumByName("ClanRoot");
            if (forum != null) {
                _forum = forum.getChildByName(_name);
                if (_forum == null) {
                    _forum = ForumsBBSManager.getInstance().createNewForum(_name, ForumsBBSManager.getInstance().getForumByName("ClanRoot"), Forum.CLAN, Forum.CLANMEMBERONLY, getId());
                }
            }
        }
    }

    /**
     * @return the castle Id for this clan if owns a castle, zero otherwise.
     */
    public int getCastleId() {
        return _castleId;
    }

    /**
     * @param castleId the castle Id to set.
     */
    public void setCastleId(int castleId) {
        _castleId = castleId;
    }

    /**
     * @return the fort Id for this clan if owns a fort, zero otherwise.
     */
    public int getFortId() {
        return _fortId;
    }

    /**
     * @param fortId the fort Id to set.
     */
    public void setFortId(int fortId) {
        _fortId = fortId;
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

    /**
     * @return Returns the clanCrestId.
     */
    public int getCrestId() {
        return _crestId;
    }

    /**
     * @param crestId the Id of the clan crest to be set.
     */
    public void setCrestId(int crestId) {
        _crestId = crestId;
    }

    /**
     * @return Returns the clan CrestLargeId
     */
    public int getCrestLargeId() {
        return _crestLargeId;
    }

    /**
     * @param crestLargeId The id of pledge LargeCrest.
     */
    public void setCrestLargeId(int crestLargeId) {
        _crestLargeId = crestLargeId;
    }

    /**
     * @param id the Id of the player to be verified.
     * @return {code true} if the player belongs to the clan.
     */
    public boolean isMember(int id) {
        return ((id != 0) && members.containsKey(id));
    }

    /**
     * @return the Blood Alliance count for this clan
     */
    public int getBloodAllianceCount() {
        return _bloodAllianceCount;
    }

    /**
     * Increase Blood Alliance count by config predefined count and updates the database.
     */
    public void increaseBloodAllianceCount() {
        _bloodAllianceCount += SiegeManager.getInstance().getBloodAllianceReward();
        updateBloodAllianceCountInDB();
    }

    /**
     * Reset the Blood Alliance count to zero and updates the database.
     */
    public void resetBloodAllianceCount() {
        _bloodAllianceCount = 0;
        updateBloodAllianceCountInDB();
    }

    /**
     * Store current Bloood Alliances count in database.
     */
    public void updateBloodAllianceCountInDB() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET blood_alliance_count=? WHERE clan_id=?")) {
            ps.setInt(1, _bloodAllianceCount);
            ps.setInt(2, _clanId);
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Exception on updateBloodAllianceCountInDB(): " + e.getMessage(), e);
        }
    }

    /**
     * @return the Blood Oath count for this clan
     */
    public int getBloodOathCount() {
        return _bloodOathCount;
    }

    /**
     * Increase Blood Oath count by config predefined count and updates the database.
     */
    public void increaseBloodOathCount() {
        _bloodOathCount += Config.FS_BLOOD_OATH_COUNT;
        updateBloodOathCountInDB();
    }

    /**
     * Reset the Blood Oath count to zero and updates the database.
     */
    public void resetBloodOathCount() {
        _bloodOathCount = 0;
        updateBloodOathCountInDB();
    }

    /**
     * Store current Bloood Alliances count in database.
     */
    public void updateBloodOathCountInDB() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET blood_oath_count=? WHERE clan_id=?")) {
            ps.setInt(1, _bloodOathCount);
            ps.setInt(2, _clanId);
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Exception on updateBloodAllianceCountInDB(): " + e.getMessage(), e);
        }
    }

    public void updateInDB() {
        // Update reputation
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET reputation_score=? WHERE clan_id=?")) {
            ps.setInt(1, _reputationScore);
            ps.setInt(2, _clanId);
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Exception on updateClanScoreInDb(): " + e.getMessage(), e);
        }

        // Update variables at database
        if (_vars != null) {
            _vars.storeMe();
        }
    }

    /**
     * Updates in database clan information:
     * <ul>
     * <li>Clan leader Id</li>
     * <li>Alliance Id</li>
     * <li>Alliance name</li>
     * <li>Clan's reputation</li>
     * <li>Alliance's penalty expiration time</li>
     * <li>Alliance's penalty type</li>
     * <li>Character's penalty expiration time</li>
     * <li>Dissolving expiration time</li>
     * <li>Clan's id</li>
     * </ul>
     */
    public void updateClanInDB() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET leader_id=?,ally_id=?,ally_name=?,reputation_score=?,ally_penalty_expiry_time=?,ally_penalty_type=?,char_penalty_expiry_time=?,dissolving_expiry_time=?,new_leader_id=? WHERE clan_id=?")) {
            ps.setInt(1, getLeaderId());
            ps.setInt(2, _allyId);
            ps.setString(3, _allyName);
            ps.setInt(4, _reputationScore);
            ps.setLong(5, _allyPenaltyExpiryTime);
            ps.setInt(6, _allyPenaltyType);
            ps.setLong(7, _charPenaltyExpiryTime);
            ps.setLong(8, _dissolvingExpiryTime);
            ps.setInt(9, _newLeaderId);
            ps.setInt(10, _clanId);
            ps.execute();
        } catch (Exception e) {
            LOGGER.error("Error saving clan: " + e.getMessage(), e);
        }
    }

    /**
     * Stores in database clan information:
     * <ul>
     * <li>Clan Id</li>
     * <li>Clan name</li>
     * <li>Clan level</li>
     * <li>Has castle</li>
     * <li>Alliance Id</li>
     * <li>Alliance name</li>
     * <li>Clan leader Id</li>
     * <li>Clan crest Id</li>
     * <li>Clan large crest Id</li>
     * <li>Alliance crest Id</li>
     * </ul>
     */
    public void store() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_CLAN_DATA)) {
            ps.setInt(1, _clanId);
            ps.setString(2, _name);
            ps.setInt(3, _level);
            ps.setInt(4, _castleId);
            ps.setInt(5, _bloodAllianceCount);
            ps.setInt(6, _bloodOathCount);
            ps.setInt(7, _allyId);
            ps.setString(8, _allyName);
            ps.setInt(9, getLeaderId());
            ps.setInt(10, _crestId);
            ps.setInt(11, _crestLargeId);
            ps.setInt(12, _allyCrestId);
            ps.setInt(13, _newLeaderId);
            ps.execute();
        } catch (Exception e) {
            LOGGER.error("Error saving new clan: " + e.getMessage(), e);
        }
    }

    /**
     * @param member               the clan member to be removed.
     * @param clanJoinExpiryTime
     * @param clanCreateExpiryTime
     */
    private void removeMemberInDatabase(ClanMember member, long clanJoinExpiryTime, long clanCreateExpiryTime) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps1 = con.prepareStatement("UPDATE characters SET clanid=0, title=?, clan_join_expiry_time=?, clan_create_expiry_time=?, clan_privs=0, wantspeace=0, subpledge=0, lvl_joined_academy=0, apprentice=0, sponsor=0 WHERE charId=?");
             PreparedStatement ps2 = con.prepareStatement("UPDATE characters SET apprentice=0 WHERE apprentice=?");
             PreparedStatement ps3 = con.prepareStatement("UPDATE characters SET sponsor=0 WHERE sponsor=?")) {
            ps1.setString(1, "");
            ps1.setLong(2, clanJoinExpiryTime);
            ps1.setLong(3, clanCreateExpiryTime);
            ps1.setInt(4, member.getObjectId());
            ps1.execute();
            // Remove apprentice.
            ps2.setInt(1, member.getObjectId());
            ps2.execute();
            // Remove sponsor.
            ps3.setInt(1, member.getObjectId());
            ps3.execute();
        } catch (Exception e) {
            LOGGER.error("Error removing clan member: " + e.getMessage(), e);
        }
    }

    private void restore() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_CLAN_DATA)) {
            ps.setInt(1, _clanId);
            try (ResultSet clanData = ps.executeQuery()) {
                if (clanData.next()) {
                    setName(clanData.getString("clan_name"));
                    setLevel(clanData.getInt("clan_level"));
                    setCastleId(clanData.getInt("hasCastle"));
                    _bloodAllianceCount = clanData.getInt("blood_alliance_count");
                    _bloodOathCount = clanData.getInt("blood_oath_count");
                    setAllyId(clanData.getInt("ally_id"));
                    setAllyName(clanData.getString("ally_name"));
                    setAllyPenaltyExpiryTime(clanData.getLong("ally_penalty_expiry_time"), clanData.getInt("ally_penalty_type"));
                    if (_allyPenaltyExpiryTime < System.currentTimeMillis()) {
                        setAllyPenaltyExpiryTime(0, 0);
                    }
                    setCharPenaltyExpiryTime(clanData.getLong("char_penalty_expiry_time"));
                    if ((_charPenaltyExpiryTime + (Config.ALT_CLAN_JOIN_DAYS * 86400000)) < System.currentTimeMillis()) // 24*60*60*1000 = 86400000
                    {
                        setCharPenaltyExpiryTime(0);
                    }
                    setDissolvingExpiryTime(clanData.getLong("dissolving_expiry_time"));

                    setCrestId(clanData.getInt("crest_id"));
                    setCrestLargeId(clanData.getInt("crest_large_id"));
                    setAllyCrestId(clanData.getInt("ally_crest_id"));

                    setReputationScore(clanData.getInt("reputation_score"), false);
                    setAuctionBiddedAt(clanData.getInt("auction_bid_at"), false);
                    setNewLeaderId(clanData.getInt("new_leader_id"), false);

                    final int leaderId = (clanData.getInt("leader_id"));

                    ps.clearParameters();

                    try (PreparedStatement select = con.prepareStatement("SELECT char_name,level,classid,charId,title,power_grade,subpledge,apprentice,sponsor,sex,race FROM characters WHERE clanid=?")) {
                        select.setInt(1, _clanId);
                        try (ResultSet clanMember = select.executeQuery()) {
                            ClanMember member = null;
                            while (clanMember.next()) {
                                member = new ClanMember(this, clanMember);
                                if (member.getObjectId() == leaderId) {
                                    setLeader(member);
                                } else {
                                    addClanMember(member);
                                }
                            }
                        }
                    }
                }
            }

            restoreSubPledges();
            restoreRankPrivs();
            restoreSkills();
            restoreNotice();
        } catch (Exception e) {
            LOGGER.error("Error restoring clan data: " + e.getMessage(), e);
        }
    }

    private void restoreNotice() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT enabled,notice FROM clan_notices WHERE clan_id=?")) {
            ps.setInt(1, _clanId);
            try (ResultSet noticeData = ps.executeQuery()) {
                while (noticeData.next()) {
                    _noticeEnabled = noticeData.getBoolean("enabled");
                    _notice = noticeData.getString("notice");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error restoring clan notice: " + e.getMessage(), e);
        }
    }

    private void storeNotice(String notice, boolean enabled) {
        if (notice == null) {
            notice = "";
        }

        if (notice.length() > MAX_NOTICE_LENGTH) {
            notice = notice.substring(0, MAX_NOTICE_LENGTH - 1);
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO clan_notices (clan_id,notice,enabled) values (?,?,?) ON DUPLICATE KEY UPDATE notice=?,enabled=?")) {
            ps.setInt(1, _clanId);
            ps.setString(2, notice);
            if (enabled) {
                ps.setString(3, "true");
            } else {
                ps.setString(3, "false");
            }
            ps.setString(4, notice);
            if (enabled) {
                ps.setString(5, "true");
            } else {
                ps.setString(5, "false");
            }
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Error could not store clan notice: " + e.getMessage(), e);
        }

        _notice = notice;
        _noticeEnabled = enabled;
    }

    public boolean isNoticeEnabled() {
        return _noticeEnabled;
    }

    public void setNoticeEnabled(boolean enabled) {
        storeNotice(_notice, enabled);
    }

    public String getNotice() {
        if (_notice == null) {
            return "";
        }
        return _notice;
    }

    public void setNotice(String notice) {
        storeNotice(notice, _noticeEnabled);
    }

    private void restoreSkills() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT skill_id,skill_level,sub_pledge_id FROM clan_skills WHERE clan_id=?")) {
            // Retrieve all skills of this Player from the database
            ps.setInt(1, _clanId);
            try (ResultSet rset = ps.executeQuery()) {
                // Go though the recordset of this SQL query
                while (rset.next()) {
                    final int id = rset.getInt("skill_id");
                    final int level = rset.getInt("skill_level");
                    // Create a L2Skill object for each record
                    final Skill skill = SkillEngine.getInstance().getSkill(id, level);
                    // Add the L2Skill object to the Clan _skills
                    final int subType = rset.getInt("sub_pledge_id");

                    if (subType == -2) {
                        _skills.put(skill.getId(), skill);
                    } else if (subType == 0) {
                        _subPledgeSkills.put(skill.getId(), skill);
                    } else {
                        final SubPledge subunit = _subPledges.get(subType);
                        if (subunit != null) {
                            subunit.addNewSkill(skill);
                        } else {
                            LOGGER.info("Missing subpledge " + subType + " for clan " + this + ", skill skipped.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error restoring clan skills: " + e.getMessage(), e);
        }
    }

    /**
     * @return all the clan skills.
     */
    public final Skill[] getAllSkills() {
        if (_skills == null) {
            return new Skill[0];
        }

        return _skills.values().toArray(new Skill[_skills.values().size()]);
    }

    /**
     * @return the map containing this clan skills.
     */
    public Map<Integer, Skill> getSkills() {
        return _skills;
    }

    /**
     * Used to add a skill to skill list of this Clan
     *
     * @param newSkill
     * @return
     */
    public Skill addSkill(Skill newSkill) {
        Skill oldSkill = null;

        if (newSkill != null) {
            // Replace oldSkill by newSkill or Add the newSkill
            oldSkill = _skills.put(newSkill.getId(), newSkill);
        }

        return oldSkill;
    }

    public Skill addNewSkill(Skill newSkill) {
        return addNewSkill(newSkill, -2);
    }

    /**
     * Used to add a new skill to the list, send a packet to all online clan members, update their stats and store it in db
     *
     * @param newSkill
     * @param subType
     * @return
     */
    public Skill addNewSkill(Skill newSkill, int subType) {
        Skill oldSkill = null;
        if (newSkill != null) {
            if (subType == -2) {
                oldSkill = _skills.put(newSkill.getId(), newSkill);
            } else if (subType == 0) {
                oldSkill = _subPledgeSkills.put(newSkill.getId(), newSkill);
            } else {
                final SubPledge subunit = getSubPledge(subType);
                if (subunit != null) {
                    oldSkill = subunit.addNewSkill(newSkill);
                } else {
                    LOGGER.warn("Subpledge " + subType + " does not exist for clan " + this);
                    return oldSkill;
                }
            }

            try (Connection con = DatabaseFactory.getInstance().getConnection()) {
                if (oldSkill != null) {
                    try (PreparedStatement ps = con.prepareStatement("UPDATE clan_skills SET skill_level=? WHERE skill_id=? AND clan_id=?")) {
                        ps.setInt(1, newSkill.getLevel());
                        ps.setInt(2, oldSkill.getId());
                        ps.setInt(3, _clanId);
                        ps.execute();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO clan_skills (clan_id,skill_id,skill_level,skill_name,sub_pledge_id) VALUES (?,?,?,?,?)")) {
                        ps.setInt(1, _clanId);
                        ps.setInt(2, newSkill.getId());
                        ps.setInt(3, newSkill.getLevel());
                        ps.setString(4, newSkill.getName());
                        ps.setInt(5, subType);
                        ps.execute();
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Error could not store clan skills: " + e.getMessage(), e);
            }

            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED);
            sm.addSkillName(newSkill.getId());

            for (ClanMember temp : members.values()) {
                if ((temp != null) && (temp.getPlayerInstance() != null) && temp.isOnline()) {
                    if (subType == -2) {
                        if (newSkill.getMinPledgeClass() <= temp.getPlayerInstance().getPledgeClass()) {
                            temp.getPlayerInstance().addSkill(newSkill, false); // Skill is not saved to player DB
                            temp.getPlayerInstance().sendPacket(new PledgeSkillListAdd(newSkill.getId(), newSkill.getLevel()));
                            temp.getPlayerInstance().sendPacket(sm);
                            temp.getPlayerInstance().sendSkillList();
                        }
                    } else if (temp.getPledgeType() == subType) {
                        temp.getPlayerInstance().addSkill(newSkill, false); // Skill is not saved to player DB
                        temp.getPlayerInstance().sendPacket(new ExSubPledgeSkillAdd(subType, newSkill.getId(), newSkill.getLevel()));
                        temp.getPlayerInstance().sendPacket(sm);
                        temp.getPlayerInstance().sendSkillList();
                    }
                }
            }
        }

        return oldSkill;
    }

    public void addSkillEffects() {
        for (Skill skill : _skills.values()) {
            for (ClanMember temp : members.values()) {
                try {
                    if ((temp != null) && temp.isOnline()) {
                        if (skill.getMinPledgeClass() <= temp.getPlayerInstance().getPledgeClass()) {
                            temp.getPlayerInstance().addSkill(skill, false); // Skill is not saved to player DB
                        }
                    }
                } catch (NullPointerException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }
    }

    public void addSkillEffects(Player player) {
        if (player == null) {
            return;
        }

        final int playerSocialClass = player.getPledgeClass() + 1;
        for (Skill skill : _skills.values()) {
            final SkillLearn skillLearn = SkillTreesData.getInstance().getPledgeSkill(skill.getId(), skill.getLevel());
            if ((skillLearn == null) || (skillLearn.getSocialClass() == null) || (playerSocialClass >= skillLearn.getSocialClass().ordinal())) {
                player.addSkill(skill, false); // Skill is not saved to player DB
            }
        }
        if (player.getPledgeType() == 0) {
            for (Skill skill : _subPledgeSkills.values()) {
                final SkillLearn skillLearn = SkillTreesData.getInstance().getSubPledgeSkill(skill.getId(), skill.getLevel());
                if ((skillLearn == null) || (skillLearn.getSocialClass() == null) || (playerSocialClass >= skillLearn.getSocialClass().ordinal())) {
                    player.addSkill(skill, false); // Skill is not saved to player DB
                }
            }
        } else {
            final SubPledge subunit = getSubPledge(player.getPledgeType());
            if (subunit == null) {
                return;
            }
            for (Skill skill : subunit.getSkills()) {
                player.addSkill(skill, false); // Skill is not saved to player DB
            }
        }

        if (_reputationScore < 0) {
            skillsStatus(player, true);
        }
    }

    public void removeSkillEffects(Player player) {
        if (player == null) {
            return;
        }

        for (Skill skill : _skills.values()) {
            player.removeSkill(skill, false); // Skill is not saved to player DB
        }

        if (player.getPledgeType() == 0) {
            for (Skill skill : _subPledgeSkills.values()) {
                player.removeSkill(skill, false); // Skill is not saved to player DB
            }
        } else {
            final SubPledge subunit = getSubPledge(player.getPledgeType());
            if (subunit == null) {
                return;
            }
            for (Skill skill : subunit.getSkills()) {
                player.removeSkill(skill, false); // Skill is not saved to player DB
            }
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

        if (player.getPledgeType() == 0) {
            for (Skill skill : _subPledgeSkills.values()) {
                if (disable) {
                    player.disableSkill(skill, -1);
                } else {
                    player.enableSkill(skill);
                }
            }
        } else {
            final SubPledge subunit = getSubPledge(player.getPledgeType());
            if (subunit != null) {
                for (Skill skill : subunit.getSkills()) {
                    if (disable) {
                        player.disableSkill(skill, -1);
                    } else {
                        player.enableSkill(skill);
                    }
                }
            }
        }
    }

    public void broadcastToOnlineAllyMembers(ServerPacket packet) {
        ClanTable.getInstance().getClanAllies(getAllyId()).forEach(c -> c.broadcastToOnlineMembers(packet));
    }

    public void broadcastToOnlineMembers(ServerPacket packet) {
        members.values().stream().filter(ClanMember::isOnline).map(ClanMember::getPlayerInstance).forEach(packet::sendTo);
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
        return _name + "[" + _clanId + "]";
    }

    public ItemContainer getWarehouse() {
        return _warehouse;
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

    public int getHiredGuards() {
        return _hiredGuards;
    }

    public void incrementHiredGuards() {
        _hiredGuards++;
    }

    public boolean isAtWar() {
        return !_atWarWith.isEmpty();
    }

    public Map<Integer, ClanWar> getWarList() {
        return _atWarWith;
    }

    public void broadcastClanStatus() {
        for (Player member : getOnlineMembers(0)) {
            member.sendPacket(PledgeShowMemberListDeleteAll.STATIC_PACKET);
            PledgeShowMemberListAll.sendAllTo(member);
        }
    }

    private void restoreSubPledges() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT sub_pledge_id,name,leader_id FROM clan_subpledges WHERE clan_id=?")) {
            // Retrieve all subpledges of this clan from the database
            ps.setInt(1, _clanId);
            try (ResultSet rset = ps.executeQuery()) {
                while (rset.next()) {
                    final int id = rset.getInt("sub_pledge_id");
                    final String name = rset.getString("name");
                    final int leaderId = rset.getInt("leader_id");
                    // Create a SubPledge object for each record
                    final SubPledge pledge = new SubPledge(id, name, leaderId);
                    _subPledges.put(id, pledge);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not restore clan sub-units: " + e.getMessage(), e);
        }
    }

    /**
     * used to retrieve subPledge by type
     *
     * @param pledgeType
     * @return
     */
    public final SubPledge getSubPledge(int pledgeType) {
        return _subPledges == null ? null : _subPledges.get(pledgeType);
    }

    /**
     * Used to retrieve subPledge by type
     *
     * @param pledgeName
     * @return
     */
    public final SubPledge getSubPledge(String pledgeName) {
        if (_subPledges == null) {
            return null;
        }

        for (SubPledge sp : _subPledges.values()) {
            if (sp.getName().equalsIgnoreCase(pledgeName)) {
                return sp;
            }
        }
        return null;
    }

    /**
     * Used to retrieve all subPledges
     *
     * @return
     */
    public final SubPledge[] getAllSubPledges() {
        if (_subPledges == null) {
            return new SubPledge[0];
        }

        return _subPledges.values().toArray(new SubPledge[_subPledges.values().size()]);
    }

    public SubPledge createSubPledge(Player player, int pledgeType, int leaderId, String subPledgeName) {
        SubPledge subPledge = null;
        pledgeType = getAvailablePledgeTypes(pledgeType);
        if (pledgeType == 0) {
            if (pledgeType == SUBUNIT_ACADEMY) {
                player.sendPacket(SystemMessageId.YOUR_CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY);
            } else {
                player.sendMessage("You can't create any more sub-units of this type");
            }
            return null;
        }
        if (_leader.getObjectId() == leaderId) {
            player.sendMessage("Leader is not correct");
            return null;
        }

        // Royal Guard 5000 points per each
        // Order of Knights 10000 points per each
        if ((pledgeType != -1) && (((_reputationScore < Config.ROYAL_GUARD_COST) && (pledgeType < SUBUNIT_KNIGHT1)) || ((_reputationScore < Config.KNIGHT_UNIT_COST) && (pledgeType > SUBUNIT_ROYAL2)))) {
            player.sendPacket(SystemMessageId.THE_CLAN_REPUTATION_IS_TOO_LOW);
            return null;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO clan_subpledges (clan_id,sub_pledge_id,name,leader_id) values (?,?,?,?)")) {
            ps.setInt(1, _clanId);
            ps.setInt(2, pledgeType);
            ps.setString(3, subPledgeName);
            ps.setInt(4, pledgeType != -1 ? leaderId : 0);
            ps.execute();

            subPledge = new SubPledge(pledgeType, subPledgeName, leaderId);
            _subPledges.put(pledgeType, subPledge);

            if (pledgeType != -1) {
                // Royal Guard 5000 points per each
                // Order of Knights 10000 points per each
                if (pledgeType < SUBUNIT_KNIGHT1) {
                    setReputationScore(_reputationScore - Config.ROYAL_GUARD_COST, true);
                } else {
                    setReputationScore(_reputationScore - Config.KNIGHT_UNIT_COST, true);
                    // TODO: clan lvl9 or more can reinforce knights cheaper if first knight unit already created, use Config.KNIGHT_REINFORCE_COST
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error saving sub clan data: " + e.getMessage(), e);
        }

        broadcastToOnlineMembers(new PledgeShowInfoUpdate(_leader.getClan()));
        broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(subPledge, _leader.getClan()));
        return subPledge;
    }

    public int getAvailablePledgeTypes(int pledgeType) {
        if (_subPledges.get(pledgeType) != null) {
            // LOGGER.warn("found sub-unit with id: "+pledgeType);
            switch (pledgeType) {
                case SUBUNIT_ACADEMY: {
                    return 0;
                }
                case SUBUNIT_ROYAL1: {
                    pledgeType = getAvailablePledgeTypes(SUBUNIT_ROYAL2);
                    break;
                }
                case SUBUNIT_ROYAL2: {
                    return 0;
                }
                case SUBUNIT_KNIGHT1: {
                    pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT2);
                    break;
                }
                case SUBUNIT_KNIGHT2: {
                    pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT3);
                    break;
                }
                case SUBUNIT_KNIGHT3: {
                    pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT4);
                    break;
                }
                case SUBUNIT_KNIGHT4: {
                    return 0;
                }
            }
        }
        return pledgeType;
    }

    public void updateSubPledgeInDB(int pledgeType) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE clan_subpledges SET leader_id=?, name=? WHERE clan_id=? AND sub_pledge_id=?")) {
            ps.setInt(1, getSubPledge(pledgeType).getLeaderId());
            ps.setString(2, getSubPledge(pledgeType).getName());
            ps.setInt(3, _clanId);
            ps.setInt(4, pledgeType);
            ps.execute();
        } catch (Exception e) {
            LOGGER.error("Error updating subpledge: " + e.getMessage(), e);
        }
    }

    private void restoreRankPrivs() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT privs,`rank`,party FROM clan_privs WHERE clan_id=?")) {
            // Retrieve all skills of this Player from the database
            ps.setInt(1, _clanId);
            // LOGGER.warn("clanPrivs restore for ClanId : "+getClanId());
            try (ResultSet rset = ps.executeQuery()) {
                // Go though the recordset of this SQL query
                while (rset.next()) {
                    final int rank = rset.getInt("rank");
                    // int party = rset.getInt("party");
                    final int privileges = rset.getInt("privs");
                    // Create a SubPledge object for each record
                    if (rank == -1) {
                        continue;
                    }

                    _privs.get(rank).setPrivs(privileges);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error restoring clan privs by rank: " + e.getMessage(), e);
        }
    }

    public void initializePrivs() {
        for (int i = 1; i < 10; i++) {
            _privs.put(i, new RankPrivs(i, 0, new EnumIntBitmask<>(ClanPrivilege.class, false)));
        }
    }

    public EnumIntBitmask<ClanPrivilege> getRankPrivs(int rank) {
        return _privs.get(rank) != null ? _privs.get(rank).getPrivs() : new EnumIntBitmask<>(ClanPrivilege.class, false);
    }

    public void setRankPrivs(int rank, int privs) {
        if (_privs.get(rank) != null) {
            _privs.get(rank).setPrivs(privs);

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("REPLACE INTO clan_privs (clan_id,`rank`,party,privs) VALUES (?,?,?,?)")) {
                // Retrieve all skills of this Player from the database
                ps.setInt(1, _clanId);
                ps.setInt(2, rank);
                ps.setInt(3, 0);
                ps.setInt(4, privs);
                ps.execute();
            } catch (Exception e) {
                LOGGER.warn("Could not store clan privs for rank: " + e.getMessage(), e);
            }

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
            _privs.put(rank, new RankPrivs(rank, 0, privs));

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("REPLACE INTO clan_privs (clan_id,`rank`,party,privs) VALUES (?,?,?,?)")) {
                // Retrieve all skills of this Player from the database
                ps.setInt(1, _clanId);
                ps.setInt(2, rank);
                ps.setInt(3, 0);
                ps.setInt(4, privs);
                ps.execute();
            } catch (Exception e) {
                LOGGER.warn("Could not create new rank and store clan privs for rank: " + e.getMessage(), e);
            }
        }
    }

    /**
     * @return all RankPrivs.
     */
    public final RankPrivs[] getAllRankPrivs() {
        return _privs == null ? new RankPrivs[0] : _privs.values().toArray(new RankPrivs[_privs.values().size()]);
    }

    public int getLeaderSubPledge(int leaderId) {
        int id = 0;
        for (SubPledge sp : _subPledges.values()) {
            if (sp.getLeaderId() == 0) {
                continue;
            }
            if (sp.getLeaderId() == leaderId) {
                id = sp.getId();
            }
        }
        return id;
    }

    public synchronized void addReputationScore(int value, boolean save) {
        setReputationScore(_reputationScore + value, save);
    }

    public synchronized void takeReputationScore(int value, boolean save) {
        setReputationScore(_reputationScore - value, save);
    }

    private void setReputationScore(int value, boolean save) {
        if ((_reputationScore >= 0) && (value < 0)) {
            broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.SINCE_THE_CLAN_REPUTATION_HAS_DROPPED_BELOW_0_YOUR_CLAN_SKILL_S_WILL_BE_DE_ACTIVATED));
            for (ClanMember member : members.values()) {
                if (member.isOnline() && (member.getPlayerInstance() != null)) {
                    skillsStatus(member.getPlayerInstance(), true);
                }
            }
        } else if ((_reputationScore < 0) && (value >= 0)) {
            broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_SKILLS_WILL_NOW_BE_ACTIVATED_SINCE_THE_CLAN_REPUTATION_IS_1_OR_HIGHER));
            for (ClanMember member : members.values()) {
                if (member.isOnline() && (member.getPlayerInstance() != null)) {
                    skillsStatus(member.getPlayerInstance(), false);
                }
            }
        }

        _reputationScore = value;
        if (_reputationScore > 100000000) {
            _reputationScore = 100000000;
        }
        if (_reputationScore < -100000000) {
            _reputationScore = -100000000;
        }
        broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
        if (save) {
            updateInDB();
        }
    }

    public int getReputationScore() {
        return _reputationScore;
    }

    public int getRank() {
        return _rank;
    }

    public void setRank(int rank) {
        _rank = rank;
    }

    public int getAuctionBiddedAt() {
        return _auctionBiddedAt;
    }

    public void setAuctionBiddedAt(int id, boolean storeInDb) {
        _auctionBiddedAt = id;

        if (storeInDb) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET auction_bid_at=? WHERE clan_id=?")) {
                ps.setInt(1, id);
                ps.setInt(2, _clanId);
                ps.execute();
            } catch (Exception e) {
                LOGGER.warn("Could not store auction for clan: " + e.getMessage(), e);
            }
        }
    }

    /**
     * @param activeChar the clan inviting player.
     * @param target     the invited player.
     * @param pledgeType the pledge type to join.
     * @return {core true} if activeChar and target meet various conditions to join a clan.
     */
    public boolean checkClanJoinCondition(Player activeChar, Player target, int pledgeType) {
        if (activeChar == null) {
            return false;
        }
        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_JOIN_CLAN)) {
            activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return false;
        }
        if (target == null) {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return false;
        }
        if (activeChar.getObjectId() == target.getObjectId()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN);
            return false;
        }
        if (_charPenaltyExpiryTime > System.currentTimeMillis()) {
            activeChar.sendPacket(SystemMessageId.AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER);
            return false;
        }
        if (target.getClanId() != 0) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_ALREADY_A_MEMBER_OF_ANOTHER_CLAN);
            sm.addString(target.getName());
            activeChar.sendPacket(sm);
            return false;
        }
        if (target.getClanJoinExpiryTime() > System.currentTimeMillis()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_CLAN);
            sm.addString(target.getName());
            activeChar.sendPacket(sm);
            return false;
        }
        if (((target.getLevel() > 40) || (target.getClassId().level() >= 2)) && (pledgeType == -1)) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DOES_NOT_MEET_THE_REQUIREMENTS_TO_JOIN_A_CLAN_ACADEMY);
            sm.addString(target.getName());
            activeChar.sendPacket(sm);
            activeChar.sendPacket(SystemMessageId.IN_ORDER_TO_JOIN_THE_CLAN_ACADEMY_YOU_MUST_BE_UNAFFILIATED_WITH_A_CLAN_AND_BE_AN_UNAWAKENED_CHARACTER_LV_84_OR_BELOW_FPR_BOTH_MAIN_AND_SUBCLASS);
            return false;
        }
        if (getSubPledgeMembersCount(pledgeType) >= getMaxNrOfMembers(pledgeType)) {
            if (pledgeType == 0) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_FULL_AND_CANNOT_ACCEPT_ADDITIONAL_CLAN_MEMBERS_AT_THIS_TIME);
                sm.addString(_name);
                activeChar.sendPacket(sm);
            } else {
                activeChar.sendPacket(SystemMessageId.THE_CLAN_IS_FULL);
            }
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
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER);
            sm.addString(target.getName());
            activeChar.sendPacket(sm);
            return false;
        }
        final Clan targetClan = target.getClan();
        if (target.getAllyId() != 0) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CLAN_IS_ALREADY_A_MEMBER_OF_S2_ALLIANCE);
            sm.addString(targetClan.getName());
            sm.addString(targetClan.getAllyName());
            activeChar.sendPacket(sm);
            return false;
        }
        if (targetClan.getAllyPenaltyExpiryTime() > System.currentTimeMillis()) {
            if (targetClan.getAllyPenaltyType() == PENALTY_TYPE_CLAN_LEAVED) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CLAN_CANNOT_JOIN_THE_ALLIANCE_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_ALLIANCE);
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

        if (ClanTable.getInstance().getClanAllies(activeChar.getAllyId()).size() >= Config.ALT_MAX_NUM_OF_CLANS_IN_ALLY) {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_LIMIT);
            return false;
        }

        return true;
    }

    public long getAllyPenaltyExpiryTime() {
        return _allyPenaltyExpiryTime;
    }

    public int getAllyPenaltyType() {
        return _allyPenaltyType;
    }

    public void setAllyPenaltyExpiryTime(long expiryTime, int penaltyType) {
        _allyPenaltyExpiryTime = expiryTime;
        _allyPenaltyType = penaltyType;
    }

    public long getCharPenaltyExpiryTime() {
        return _charPenaltyExpiryTime;
    }

    public void setCharPenaltyExpiryTime(long time) {
        _charPenaltyExpiryTime = time;
    }

    public long getDissolvingExpiryTime() {
        return _dissolvingExpiryTime;
    }

    public void setDissolvingExpiryTime(long time) {
        _dissolvingExpiryTime = time;
    }

    public void createAlly(Player player, String allyName) {
        if (null == player) {
            return;
        }

        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES);
            return;
        }
        if (_allyId != 0) {
            player.sendPacket(SystemMessageId.YOU_ALREADY_BELONG_TO_ANOTHER_ALLIANCE);
            return;
        }
        if (_level < 5) {
            player.sendPacket(SystemMessageId.TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER);
            return;
        }
        if ((_allyPenaltyExpiryTime > System.currentTimeMillis()) && (_allyPenaltyType == PENALTY_TYPE_DISSOLVE_ALLY)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CREATE_A_NEW_ALLIANCE_WITHIN_1_DAY_OF_DISSOLUTION);
            return;
        }
        if (_dissolvingExpiryTime > System.currentTimeMillis()) {
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
        if (ClanTable.getInstance().isAllyExists(allyName)) {
            player.sendPacket(SystemMessageId.THAT_ALLIANCE_NAME_ALREADY_EXISTS);
            return;
        }

        setAllyId(_clanId);
        setAllyName(allyName.trim());
        setAllyPenaltyExpiryTime(0, 0);
        updateClanInDB();

        player.sendPacket(new UserInfo(player));

        // TODO: Need correct message id
        player.sendMessage("Alliance " + allyName + " has been created.");
    }

    public void dissolveAlly(Player player) {
        if (_allyId == 0) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
            return;
        }
        if (!player.isClanLeader() || (_clanId != _allyId)) {
            player.sendPacket(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
            return;
        }
        if (player.isInsideZone(ZoneType.SIEGE)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISSOLVE_AN_ALLIANCE_WHILE_AN_AFFILIATED_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE);
            return;
        }

        broadcastToOnlineAllyMembers(SystemMessage.getSystemMessage(SystemMessageId.THE_ALLIANCE_HAS_BEEN_DISSOLVED));

        final long currentTime = System.currentTimeMillis();
        for (Clan clan : ClanTable.getInstance().getClanAllies(getAllyId())) {
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
        setAllyPenaltyExpiryTime(currentTime + (Config.ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED * 86400000), PENALTY_TYPE_DISSOLVE_ALLY); // 24*60*60*1000 = 86400000
        updateClanInDB();
    }

    public boolean levelUpClan(Player player) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return false;
        }
        if (System.currentTimeMillis() < _dissolvingExpiryTime) {
            player.sendPacket(SystemMessageId.AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOUR_CLAN_LEVEL_CANNOT_BE_INCREASED);
            return false;
        }

        boolean increaseClanLevel = false;

        // Such as https://l2wiki.com/classic/Clans_%E2%80%93_Clan_Level
        switch (_level) {
            case 0: {
                // Upgrade to 1
                if ((player.getSp() >= 1000) && (player.getAdena() >= 150000) && (members.size() >= 1)) {
                    if (player.reduceAdena("ClanLvl", 150000, player.getTarget(), true)) {
                        player.setSp(player.getSp() - 1000);
                        final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                        sp.addInt(1000);
                        player.sendPacket(sp);
                        increaseClanLevel = true;
                    }
                }
                break;
            }
            case 1: {
                // Upgrade to 2
                if ((player.getSp() >= 15000) && (player.getAdena() >= 300000) && (members.size() >= 1)) {
                    if (player.reduceAdena("ClanLvl", 300000, player.getTarget(), true)) {
                        player.setSp(player.getSp() - 15000);
                        final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                        sp.addInt(15000);
                        player.sendPacket(sp);
                        increaseClanLevel = true;
                    }
                }
                break;
            }
            case 2: {
                // Upgrade to 3
                if ((player.getSp() >= 100000) && (player.getInventory().getItemByItemId(1419) != null) && (members.size() >= 1)) {
                    // itemId 1419 == Blood Mark
                    if (player.destroyItemByItemId("ClanLvl", 1419, 100, player.getTarget(), true)) {
                        player.setSp(player.getSp() - 100000);
                        final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                        sp.addInt(100000);
                        player.sendPacket(sp);
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                        sm.addItemName(1419);
                        player.sendPacket(sm);
                        increaseClanLevel = true;
                    }
                }
                break;
            }
            case 3: {
                // Upgrade to 4
                if ((player.getSp() >= 1000000) && (player.getInventory().getItemByItemId(1419) != null) && (members.size() >= 1)) {
                    // itemId 1419 == Blood Mark
                    if (player.destroyItemByItemId("ClanLvl", 1419, 5000, player.getTarget(), true)) {
                        player.setSp(player.getSp() - 1000000);
                        final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                        sp.addInt(1000000);
                        player.sendPacket(sp);
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                        sm.addItemName(1419);
                        player.sendPacket(sm);
                        increaseClanLevel = true;
                    }
                }
                break;
            }
            case 4: {
                // Upgrade to 5
                if ((player.getSp() >= 5000000) && (player.getInventory().getItemByItemId(1419) != null) && (members.size() >= 1)) {
                    // itemId 1419 == Blood Mark
                    if (player.destroyItemByItemId("ClanLvl", 1419, 10000, player.getTarget(), true)) {
                        player.setSp(player.getSp() - 5000000);
                        final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                        sp.addInt(5000000);
                        player.sendPacket(sp);
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                        sm.addItemName(1419);
                        player.sendPacket(sm);
                        increaseClanLevel = true;
                    }
                }
                break;
            }
            default: {
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

        changeLevel(_level + 1);

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanLvlUp(player, this));
        return true;
    }

    public void changeLevel(int level) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET clan_level = ? WHERE clan_id = ?")) {
            ps.setInt(1, level);
            ps.setInt(2, _clanId);
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("could not increase clan level:" + e.getMessage(), e);
        }

        setLevel(level);

        if (_leader.isOnline()) {
            final Player leader = _leader.getPlayerInstance();
            if (level > 4) {
                SiegeManager.getInstance().addSiegeSkills(leader);
                leader.sendPacket(SystemMessageId.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION);
            } else if (level < 5) {
                SiegeManager.getInstance().removeSiegeSkills(leader);
            }
        }

        // notify all the members about it
        broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_S_LEVEL_HAS_INCREASED));
        broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
    }

    /**
     * Change the clan crest. If crest id is 0, crest is removed. New crest id is saved to database.
     *
     * @param crestId if 0, crest is removed, else new crest id is set and saved to database
     */
    public void changeClanCrest(int crestId) {
        if (_crestId != 0) {
            CrestTable.getInstance().removeCrest(getCrestId());
        }

        setCrestId(crestId);

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET crest_id = ? WHERE clan_id = ?")) {
            ps.setInt(1, crestId);
            ps.setInt(2, _clanId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.warn("Could not update crest for clan " + _name + " [" + _clanId + "] : " + e.getMessage(), e);
        }

        for (Player member : getOnlineMembers(0)) {
            member.broadcastUserInfo();
        }
    }

    /**
     * Change the ally crest. If crest id is 0, crest is removed. New crest id is saved to database.
     *
     * @param crestId      if 0, crest is removed, else new crest id is set and saved to database
     * @param onlyThisClan
     */
    public void changeAllyCrest(int crestId, boolean onlyThisClan) {
        String sqlStatement = "UPDATE clan_data SET ally_crest_id = ? WHERE clan_id = ?";
        int allyId = _clanId;
        if (!onlyThisClan) {
            if (_allyCrestId != 0) {
                CrestTable.getInstance().removeCrest(getAllyCrestId());
            }
            sqlStatement = "UPDATE clan_data SET ally_crest_id = ? WHERE ally_id = ?";
            allyId = _allyId;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sqlStatement)) {
            ps.setInt(1, crestId);
            ps.setInt(2, allyId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.warn("Could not update ally crest for ally/clan id " + allyId + " : " + e.getMessage(), e);
        }

        if (onlyThisClan) {
            setAllyCrestId(crestId);
            for (Player member : getOnlineMembers(0)) {
                member.broadcastUserInfo();
            }
        } else {
            for (Clan clan : ClanTable.getInstance().getClanAllies(getAllyId())) {
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
        if (_crestLargeId != 0) {
            CrestTable.getInstance().removeCrest(getCrestLargeId());
        }

        setCrestLargeId(crestId);

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET crest_large_id = ? WHERE clan_id = ?")) {
            ps.setInt(1, crestId);
            ps.setInt(2, _clanId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.warn("Could not update large crest for clan " + _name + " [" + _clanId + "] : " + e.getMessage(), e);
        }

        for (Player member : getOnlineMembers(0)) {
            member.broadcastUserInfo();
        }
    }

    /**
     * Check if this clan can learn the skill for the given skill ID, level.
     *
     * @param skillId
     * @param skillLevel
     * @return {@code true} if skill can be learned.
     */
    public boolean isLearnableSubSkill(int skillId, int skillLevel) {
        Skill current = _subPledgeSkills.get(skillId);
        // is next level?
        if ((current != null) && ((current.getLevel() + 1) == skillLevel)) {
            return true;
        }
        // is first level?
        if ((current == null) && (skillLevel == 1)) {
            return true;
        }
        // other sub-pledges
        for (SubPledge subunit : _subPledges.values()) {
            // disable academy
            if (subunit.getId() == -1) {
                continue;
            }
            current = subunit.getSkill(skillId);
            // is next level?
            if ((current != null) && ((current.getLevel() + 1) == skillLevel)) {
                return true;
            }
            // is first level?
            if ((current == null) && (skillLevel == 1)) {
                return true;
            }
        }
        return false;
    }

    public boolean isLearnableSubPledgeSkill(Skill skill, int subType) {
        // academy
        if (subType == -1) {
            return false;
        }

        final int id = skill.getId();
        Skill current;
        if (subType == 0) {
            current = _subPledgeSkills.get(id);
        } else {
            current = _subPledges.get(subType).getSkill(id);
        }
        // is next level?
        if ((current != null) && ((current.getLevel() + 1) == skill.getLevel())) {
            return true;
        }
        // is first level?
        return (current == null) && (skill.getLevel() == 1);

    }

    public SubPledgeSkill[] getAllSubSkills() {
        final List<SubPledgeSkill> list = new LinkedList<>();
        for (Skill skill : _subPledgeSkills.values()) {
            list.add(new SubPledgeSkill(0, skill.getId(), skill.getLevel()));
        }
        for (SubPledge subunit : _subPledges.values()) {
            for (Skill skill : subunit.getSkills()) {
                list.add(new SubPledgeSkill(subunit.getId(), skill.getId(), skill.getLevel()));
            }
        }
        return list.toArray(SubPledgeSkill[]::new);
    }

    public void setNewLeaderId(int objectId, boolean storeInDb) {
        _newLeaderId = objectId;
        if (storeInDb) {
            updateClanInDB();
        }
    }

    public int getNewLeaderId() {
        return _newLeaderId;
    }

    public Player getNewLeader() {
        return World.getInstance().findPlayer(_newLeaderId);
    }

    public void setNewLeader(ClanMember member) {
        final Player newLeader = member.getPlayerInstance();
        final ClanMember exMember = _leader;
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
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE characters SET clan_privs = ? WHERE charId = ?")) {
                ps.setInt(1, 0);
                ps.setInt(2, getLeaderId());
                ps.execute();
            } catch (Exception e) {
                LOGGER.warn("Couldn't update clan privs for old clan leader", e);
            }
        }

        setLeader(member);
        if (_newLeaderId != 0) {
            setNewLeaderId(0, true);
        }
        updateClanInDB();

        if (exLeader != null) {
            exLeader.setPledgeClass(ClanMember.calculatePledgeClass(exLeader));
            exLeader.broadcastUserInfo();
            exLeader.checkItemRestriction();
        }

        if (newLeader != null) {
            newLeader.setPledgeClass(ClanMember.calculatePledgeClass(newLeader));
            newLeader.getClanPrivileges().setAll();

            if (getLevel() >= SiegeManager.getInstance().getSiegeClanMinLevel()) {
                SiegeManager.getInstance().addSiegeSkills(newLeader);
            }
            newLeader.broadcastUserInfo();
        } else {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE characters SET clan_privs = ? WHERE charId = ?")) {
                ps.setInt(1, EnumIntBitmask.getAllBitmask(ClanPrivilege.class));
                ps.setInt(2, getLeaderId());
                ps.execute();
            } catch (Exception e) {
                LOGGER.warn("Couldn't update clan privs for new clan leader", e);
            }
        }

        broadcastClanStatus();
        broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_LEADER_PRIVILEGES_HAVE_BEEN_TRANSFERRED_TO_C1).addString(member.getName()));

        LOGGER.info("Leader of Clan: " + getName() + " changed to: " + member.getName() + " ex leader: " + exMember.getName());
    }

    public String getNewLeaderName() {
        return PlayerNameTable.getInstance().getNameById(_newLeaderId);
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
                broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ACHIEVED_LOGIN_BONUS_LV_S1).addByte(availableBonus.getLevel()));
            } else if (_lastMembersOnlineBonus.getLevel() < availableBonus.getLevel()) {
                _lastMembersOnlineBonus = availableBonus;
                broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ACHIEVED_LOGIN_BONUS_LV_S1).addByte(availableBonus.getLevel()));
            }
        }

        final int currentMaxOnline = (int) members.values().stream().filter(member -> member.getOnlineTime() > Config.ALT_CLAN_MEMBERS_TIME_FOR_BONUS).count();
        if (getMaxOnlineMembers() < currentMaxOnline) {
            getVariables().set("MAX_ONLINE_MEMBERS", currentMaxOnline);
        }
    }

    /**
     * @param activeChar
     * @param target
     * @param value
     */
    public synchronized void addHuntingPoints(Player activeChar, Npc target, double value) {
        // TODO: Figure out the retail formula
        final int points = (int) value / 2960; // Reduced / 10 for Classic.
        if (points > 0) {
            getVariables().set("HUNTING_POINTS", getHuntingPoints() + points);
            final ClanRewardBonus availableBonus = ClanRewardType.HUNTING_MONSTERS.getAvailableBonus(this);
            if (availableBonus != null) {
                if (_lastHuntingBonus == null) {
                    _lastHuntingBonus = availableBonus;
                    broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ACHIEVED_HUNTING_BONUS_LV_S1).addByte(availableBonus.getLevel()));
                } else if (_lastHuntingBonus.getLevel() < availableBonus.getLevel()) {
                    _lastHuntingBonus = availableBonus;
                    broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ACHIEVED_HUNTING_BONUS_LV_S1).addByte(availableBonus.getLevel()));
                }
            }
        }
    }

    public int getMaxOnlineMembers() {
        return getVariables().getInt("MAX_ONLINE_MEMBERS", 0);
    }

    public int getHuntingPoints() {
        return getVariables().getInt("HUNTING_POINTS", 0);
    }

    public int getPreviousMaxOnlinePlayers() {
        return getVariables().getInt("PREVIOUS_MAX_ONLINE_PLAYERS", 0);
    }

    public int getPreviousHuntingPoints() {
        return getVariables().getInt("PREVIOUS_HUNTING_POINTS", 0);
    }

    public boolean canClaimBonusReward(Player player, ClanRewardType type) {
        final ClanMember clanMember = getClanMember(player.getObjectId());
        return (clanMember != null) && (type.getAvailableBonus(this) != null) && !clanMember.isRewardClaimed(type);
    }

    public void resetClanBonus() {
        // Save current state
        getVariables().set("PREVIOUS_MAX_ONLINE_PLAYERS", getMaxOnlineMembers());
        getVariables().set("PREVIOUS_HUNTING_POINTS", getHuntingPoints());

        // Reset
        members.values().forEach(ClanMember::resetBonus);
        getVariables().remove("HUNTING_POINTS");

        // force store
        getVariables().storeMe();

        // Send Packet
        broadcastToOnlineMembers(ExPledgeBonusMarkReset.STATIC_PACKET);
    }

    public ClanVariables getVariables() {
        if (_vars == null) {
            synchronized (this) {
                if (_vars == null) {
                    _vars = new ClanVariables(_clanId);
                    if (Config.CLAN_VARIABLES_STORE_INTERVAL > 0) {
                        ThreadPool.scheduleAtFixedRate(this::storeVariables, Config.CLAN_VARIABLES_STORE_INTERVAL, Config.CLAN_VARIABLES_STORE_INTERVAL);
                    }
                }
            }
        }
        return _vars;
    }

    public boolean hasVariables() {
        return _vars != null;
    }

    private void storeVariables() {
        final ClanVariables vars = _vars;
        if (vars != null) {
            vars.storeMe();
        }
    }

    public static class SubPledge {
        private final int _id;
        private final Map<Integer, Skill> _subPledgeSkills = new ConcurrentSkipListMap<>();
        private String _subPledgeName;
        private int _leaderId;

        public SubPledge(int id, String name, int leaderId) {
            _id = id;
            _subPledgeName = name;
            _leaderId = leaderId;
        }

        public int getId() {
            return _id;
        }

        public String getName() {
            return _subPledgeName;
        }

        public void setName(String name) {
            _subPledgeName = name;
        }

        public int getLeaderId() {
            return _leaderId;
        }

        public void setLeaderId(int leaderId) {
            _leaderId = leaderId;
        }

        public Skill addNewSkill(Skill skill) {
            return _subPledgeSkills.put(skill.getId(), skill);
        }

        public Collection<Skill> getSkills() {
            return _subPledgeSkills.values();
        }

        public Skill getSkill(int id) {
            return _subPledgeSkills.get(id);
        }
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
