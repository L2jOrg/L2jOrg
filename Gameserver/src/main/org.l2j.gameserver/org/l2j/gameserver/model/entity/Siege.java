package org.l2j.gameserver.model.entity;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.CastleDAO;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.SiegeScheduleData;
import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.gameserver.enums.SiegeTeleportWhoType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.SiegeGuardManager;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.ControlTower;
import org.l2j.gameserver.model.actor.instance.FlameTower;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeFinish;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeOwnerChange;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeStart;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public class Siege implements Siegable {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Siege.class);

    private static final byte OWNER = -1;
    private static final byte DEFENDER = 0;
    private static final byte ATTACKER = 1;
    private static final byte DEFENDER_NOT_APPROVED = 2;

    private final IntMap<SiegeClanData> attackers = new CHashIntMap<>();
    private final IntMap<SiegeClanData> defenders = new CHashIntMap<>();
    private final IntMap<SiegeClanData> defendersWaiting = new CHashIntMap<>();
    private final List<ControlTower> controlTowers = new ArrayList<>();
    private final List<FlameTower> flameTowers = new ArrayList<>();

    final Castle castle;
    protected boolean isRegistrationOver = false;
    protected Calendar endDate;
    protected ScheduledFuture<?> scheduledStartSiegeTask = null;
    protected int firstOwnerClanId = -1;
    boolean isInProgress = false;
    private int controlTowerCount;
    private boolean isNormalSide = true; // true = Atk is Atk, false = Atk is Def

    public Siege(Castle castle) {
        this.castle = castle;
        startAutoTask();
    }

    private void startAutoTask() {
        correctSiegeDateTime();
        LOGGER.info("Siege of {} : {}", castle, castle.getSiegeDate());

        loadSiegeClan();

        if (nonNull(scheduledStartSiegeTask)) {
            scheduledStartSiegeTask.cancel(false);
        }
        scheduledStartSiegeTask = ThreadPool.schedule(new ScheduleStartSiegeTask(castle), 1000);
    }

    private void correctSiegeDateTime() {
        if (isNull(getCastle().getSiegeDate()) || LocalDateTime.now().isAfter(getCastle().getSiegeDate())) {
            setNextSiegeDate();
            saveSiegeDate();
        }
    }

    private void loadSiegeClan() {
        attackers.clear();
        defenders.clear();
        defendersWaiting.clear();

        if (castle.getOwnerId() > 0) {
            addDefender(castle.getOwnerId(), SiegeClanType.OWNER);
        }


        getDAO(CastleDAO.class).findSiegeClansByCastle(castle.getId()).forEach(siegeClan -> {
            switch (siegeClan.getType()) {
                case DEFENDER ->  addDefender(siegeClan);
                case ATTACKER ->  addAttacker(siegeClan);
                case DEFENDER_PENDING -> addDefenderWaiting(siegeClan);
            }

        });
    }

    @Override
    public void endSiege() {
        if (isInProgress) {
            Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.THE_S1_SIEGE_HAS_FINISHED).addCastleId(castle.getId()));
            Broadcast.toAllOnlinePlayers(new PlaySound("systemmsg_eu.18"));

            if (castle.getOwnerId() > 0) {
                final Clan clan = ClanTable.getInstance().getClan(getCastle().getOwnerId());

                Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.CLAN_S1_IS_VICTORIOUS_OVER_S2_S_CASTLE_SIEGE).addString(clan.getName()).addCastleId(castle.getId()));

                if (clan.getId() == firstOwnerClanId) {
                    // Owner is unchanged
                    clan.increaseBloodAllianceCount();
                } else {
                    castle.setTicketBuyCount(0);
                    for (ClanMember member : clan.getMembers()) {
                        if (member != null) {
                            final Player player = member.getPlayerInstance();
                            if ((player != null) && player.isNoble()) {
                                Hero.getInstance().setCastleTaken(player.getObjectId(), getCastle().getId());
                            }
                        }
                    }
                }
            } else {
                Broadcast.toAllOnlinePlayers( getSystemMessage(SystemMessageId.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addCastleId(castle.getId()));
            }

            Stream.concat(getAttackerClans().values().stream(), getDefenderClans().values().stream()).forEach(siegeClan ->

                    doIfNonNull(ClanTable.getInstance().getClan(siegeClan.getClanId()), clan -> {
                    clan.forEachOnlineMember(Player::checkItemRestriction);
                    clan.clearSiegeKills();
                    clan.clearSiegeDeaths();
            }));

            castle.updateClansReputation();
            removeFlags(); // Removes all flags. Note: Remove flag before teleporting players
            teleportPlayer(SiegeTeleportWhoType.NotOwner, TeleportWhereType.TOWN); // Teleport to the second closest town
            isInProgress = false; // Flag so that siege instance can be started
            updatePlayerSiegeStateFlags(true);
            saveCastleSiege(); // Save castle specific data
            clearSiegeClan(); // Clear siege clan from db
            removeTowers(); // Remove all towers from this castle
            SiegeGuardManager.getInstance().unspawnSiegeGuard(getCastle()); // Remove all spawned siege guard from this castle
            if (castle.getOwnerId() > 0) {
                SiegeGuardManager.getInstance().removeSiegeGuards(getCastle());
            }
            castle.spawnDoor(); // Respawn door to castle
            castle.getZone().setIsActive(false);
            castle.getZone().updateZoneStatusForCharactersInside();
            castle.getZone().setSiegeInstance(null);

            // Notify to scripts.
            EventDispatcher.getInstance().notifyEventAsync(new OnCastleSiegeFinish(this), getCastle());
        }
    }

    private void removeDefender(SiegeClanData sc) {
        if (nonNull(sc)) {
            getDefenderClans().remove(sc.getClanId());
        }
    }

    private void removeAttacker(SiegeClanData sc) {
        if (nonNull(sc)) {
            getAttackerClans().remove(sc.getClanId());
        }
    }

    private void addDefender(SiegeClanData sc, SiegeClanType type) {
        if (isNull(sc)) {
            return;
        }
        sc.setType(type);
        getDefenderClans().put(sc.getClanId(), sc);
    }

    /**
     * When control of castle changed during siege<BR>
     * <BR>
     */
    public void midVictory() {
        if (isInProgress) {

            if (castle.getOwnerId() > 0) {
                SiegeGuardManager.getInstance().removeSiegeGuards(getCastle());
            }

            if (getDefenderClans().isEmpty() && getAttackerClans().size() == 1) {

                var newOwner = getAttackerClan(castle.getOwnerId());
                removeAttacker(newOwner);
                addDefender(newOwner, SiegeClanType.OWNER);
                endSiege();
                return;
            }

            if (castle.getOwnerId() > 0) {

                final int allyId = ClanTable.getInstance().getClan(castle.getOwnerId()).getAllyId();

                if (getDefenderClans().isEmpty()) {
                    if (allyId != 0) {
                        boolean allInSameAlliance = true;
                        for (var sc : getAttackerClans().values()) {
                            if (ClanTable.getInstance().getClan(sc.getClanId()).getAllyId() != allyId) {
                                allInSameAlliance = false;
                                break;
                            }
                        }

                        if (allInSameAlliance) {
                            var newOwner = getAttackerClan(castle.getOwnerId());
                            removeAttacker(newOwner);
                            addDefender(newOwner, SiegeClanType.OWNER);
                            endSiege();
                            return;
                        }
                    }
                }

                for (var sc : getDefenderClans().values()) {
                    removeDefender(sc);
                    addAttacker(sc);
                }

                var newOwner = getAttackerClan(castle.getOwnerId());
                removeAttacker(newOwner);
                addDefender(newOwner, SiegeClanType.OWNER);

                for (Clan clan : ClanTable.getInstance().getClanAllies(allyId)) {

                    doIfNonNull(getAttackerClan(clan.getId()), siegeClan -> {
                        removeAttacker(siegeClan);
                        addDefender(siegeClan, SiegeClanType.DEFENDER);

                    });
                }

                teleportPlayer(SiegeTeleportWhoType.Attacker, TeleportWhereType.SIEGEFLAG);
                teleportPlayer(SiegeTeleportWhoType.Spectator, TeleportWhereType.TOWN);

                removeDefenderFlags();
                castle.removeUpgrade();
                castle.spawnDoor(true);
                removeTowers();
                controlTowerCount = 0;
                spawnControlTower();
                spawnFlameTower();
                updatePlayerSiegeStateFlags(false);

                EventDispatcher.getInstance().notifyEventAsync(new OnCastleSiegeOwnerChange(this), getCastle());
            }
        }
    }

    /**
     * When siege starts<BR>
     * <BR>
     */
    @Override
    public void startSiege() {
        if (!isInProgress) {
            firstOwnerClanId = castle.getOwnerId();

            if (getAttackerClans().isEmpty()) {
                SystemMessage sm;
                if (firstOwnerClanId <= 0) {
                    sm = getSystemMessage(SystemMessageId.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST);
                } else {
                    sm = getSystemMessage(SystemMessageId.S1_S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED);
                    ClanTable.getInstance().getClan(firstOwnerClanId).increaseBloodAllianceCount();
                }
                sm.addCastleId(castle.getId());
                Broadcast.toAllOnlinePlayers(sm);
                saveCastleSiege();
                return;
            }

            isNormalSide = true; // Atk is now atk
            isInProgress = true; // Flag so that same siege instance cannot be started again

            loadSiegeClan(); // Load siege clan from db
            updatePlayerSiegeStateFlags(false);
            teleportPlayer(SiegeTeleportWhoType.NotOwner, TeleportWhereType.TOWN); // Teleport to the closest town
            controlTowerCount = 0;
            spawnControlTower(); // Spawn control tower
            spawnFlameTower(); // Spawn control tower
            castle.spawnDoor(); // Spawn door
            spawnSiegeGuard(); // Spawn siege guard
            SiegeGuardManager.getInstance().deleteTickets(getCastle().getId()); // remove the tickets from the ground
            castle.getZone().setSiegeInstance(this);
            castle.getZone().setIsActive(true);
            castle.getZone().updateZoneStatusForCharactersInside();

            // Schedule a task to prepare auto siege end
            endDate = Calendar.getInstance();
            endDate.add(Calendar.MINUTE, SiegeManager.getInstance().getSiegeLength());
            ThreadPool.schedule(new ScheduleEndSiegeTask(castle), 1000); // Prepare auto end task

            Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.THE_S1_SIEGE_HAS_STARTED).addCastleId(castle.getId()));
            Broadcast.toAllOnlinePlayers(new PlaySound("systemmsg_eu.17"));

            // Notify to scripts.
            EventDispatcher.getInstance().notifyEventAsync(new OnCastleSiegeStart(this), getCastle());
        }
    }

    /**
     * Announce to player.<BR>
     * <BR>
     *
     * @param message   The SystemMessage to send to player
     * @param bothSides True - broadcast to both attackers and defenders. False - only to defenders.
     */
    public void announceToPlayer(SystemMessage message, boolean bothSides) {
        var stream = bothSides ? Stream.concat(getDefenderClans().values().stream(), getAttackerClans().values().stream()) : getDefenderClans().values().stream();

        stream.map(siegeClan -> ClanTable.getInstance().getClan(siegeClan.getClanId())).filter(Objects::nonNull).forEach(clan -> clan.forEachOnlineMember(message::sendTo));
    }

    public void updatePlayerSiegeStateFlags(boolean clear) {
        getAttackerClans().values().stream().map(siegeClan -> ClanTable.getInstance().getClan(siegeClan.getClanId())).forEach(clan -> {
            clan.forEachOnlineMember(member -> {
                if (clear) {
                    member.setSiegeState((byte) 0);
                    member.setSiegeSide(0);
                    member.setIsInSiege(false);
                    member.stopFameTask();
                } else {
                    member.setSiegeState((byte) 1);
                    member.setSiegeSide(castle.getId());
                    if (checkIfInZone(member)) {
                        member.setIsInSiege(true);
                        member.startFameTask(Config.CASTLE_ZONE_FAME_TASK_FREQUENCY * 1000, Config.CASTLE_ZONE_FAME_AQUIRE_POINTS);
                    }
                }
                broadcastMemberInfo(member);
            });
        });

        getDefenderClans().values().stream().map(siegeClan -> ClanTable.getInstance().getClan(siegeClan.getClanId())).forEach(clan -> {
            clan.forEachOnlineMember(member -> {
                if (clear) {
                    member.setSiegeState((byte) 0);
                    member.setSiegeSide(0);
                    member.setIsInSiege(false);
                    member.stopFameTask();
                } else {
                    member.setSiegeState((byte) 2);
                    member.setSiegeSide(castle.getId());
                    if (checkIfInZone(member)) {
                        member.setIsInSiege(true);
                        member.startFameTask(Config.CASTLE_ZONE_FAME_TASK_FREQUENCY * 1000, Config.CASTLE_ZONE_FAME_AQUIRE_POINTS);
                    }
                }
                broadcastMemberInfo(member);
            });
        });
    }

    private void broadcastMemberInfo(Player member) {
        member.sendPacket(new UserInfo(member));

        World.getInstance().forEachVisibleObject(member, Player.class, player -> {
            if (!member.isVisibleFor(player)) {
                return;
            }

            final int relation = member.getRelation(player);
            final Integer oldrelation = member.getKnownRelations().get(player.getObjectId());
            if ((oldrelation == null) || (oldrelation != relation)) {
                final RelationChanged rc = new RelationChanged();
                rc.addRelation(member, relation, member.isAutoAttackable(player));
                if (member.hasSummon()) {
                    final Summon pet = member.getPet();
                    if (pet != null) {
                        rc.addRelation(pet, relation, member.isAutoAttackable(player));
                    }
                    if (member.hasServitors()) {
                        member.getServitors().values().forEach(s -> rc.addRelation(s, relation, member.isAutoAttackable(player)));
                    }
                }
                player.sendPacket(rc);
                member.getKnownRelations().put(player.getObjectId(), relation);
            }
        });
    }

    /**
     * Approve clan as defender for siege<BR>
     * <BR>
     *
     * @param clanId The int of player's clan id
     */
    public void approveSiegeDefenderClan(int clanId) {
        if (clanId <= 0) {
            return;
        }
        saveSiegeClan(ClanTable.getInstance().getClan(clanId), SiegeClanType.DEFENDER, true);
        loadSiegeClan();
    }

    public boolean checkIfInZone(ILocational loc) {
        return isInProgress && castle.checkIfInZone(loc);
    }

    /**
     * Return true if clan is attacker<BR>
     * <BR>
     *
     * @param clan The Clan of the player
     */
    @Override
    public boolean checkIsAttacker(Clan clan) {
        return (getAttackerClan(clan) != null);
    }

    /**
     * Return true if clan is defender<BR>
     * <BR>
     *
     * @param clan The Clan of the player
     */
    @Override
    public boolean checkIsDefender(Clan clan) {
        return (getDefenderClan(clan) != null);
    }

    /**
     * @param clan The Clan of the player
     * @return true if clan is defender waiting approval
     */
    public boolean checkIsDefenderWaiting(Clan clan) {
        return (getDefenderWaitingClan(clan) != null);
    }

    public void clearSiegeClan() {
        var castleDAO = getDAO(CastleDAO.class);
        castleDAO.deleteSiegeByCastle(castle.getId());
        castleDAO.deleteSiegeByClan(castle.getOwnerId());
        getAttackerClans().clear();
        getDefenderClans().clear();
        defendersWaiting.clear();
    }

    public void clearSiegeWaitingClan() {
        getDAO(CastleDAO.class).deleteWaintingClansByCastle(castle.getId());
        defendersWaiting.clear();
    }

    /**
     * Return list of Player registered as attacker in the zone.
     */
    @Override
    public List<Player> getAttackersInZone() {
        //@formatter:off
        return getAttackerClans().values().stream()
                .map(siegeclan -> ClanTable.getInstance().getClan(siegeclan.getClanId()))
                .filter(Objects::nonNull)
                .flatMap(clan -> clan.getOnlineMembers(0).stream())
                .filter(Player::isInSiege)
                .collect(Collectors.toList());
        //@formatter:on
    }

    public void killedControlTower(Npc ct) {
        controlTowerCount = Math.max(controlTowerCount - 1, 0);
    }

    public void listRegisterClan(Player player) {
        player.sendPacket(new SiegeInfo(castle, player));
    }

    public void registerAttacker(Player player) {
        registerAttacker(player, false);
    }

    public void registerAttacker(Player player, boolean force) {
        if (player.getClan() == null) {
            return;
        }
        int allyId = 0;
        if (castle.getOwnerId() != 0) {
            allyId = ClanTable.getInstance().getClan(getCastle().getOwnerId()).getAllyId();
        }
        if (allyId != 0) {
            if ((player.getClan().getAllyId() == allyId) && !force) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_REGISTER_AS_AN_ATTACKER_BECAUSE_YOU_ARE_IN_AN_ALLIANCE_WITH_THE_CASTLE_OWNING_CLAN);
                return;
            }
        }

        if (force) {
            if (SiegeManager.getInstance().checkIsRegistered(player.getClan(), getCastle().getId())) {
                player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
            } else {
                saveSiegeClan(player.getClan(), SiegeClanType.ATTACKER, false); // Save to database
            }
            return;
        }

        if (checkIfCanRegister(player, ATTACKER)) {
            saveSiegeClan(player.getClan(), SiegeClanType.ATTACKER, false); // Save to database
        }
    }

    /**
     * Register a clan as defender.
     *
     * @param player the player to register
     */
    public void registerDefender(Player player) {
        registerDefender(player, false);
    }

    public void registerDefender(Player player, boolean force) {
        if (castle.getOwnerId() <= 0) {
            player.sendMessage("You cannot register as a defender because " + castle.getName() + " is owned by NPC.");
            return;
        }

        if (force) {
            if (SiegeManager.getInstance().checkIsRegistered(player.getClan(), getCastle().getId())) {
                player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
            } else {
                saveSiegeClan(player.getClan(), SiegeClanType.DEFENDER_PENDING, false); // Save to database
            }
            return;
        }

        if (checkIfCanRegister(player, DEFENDER_NOT_APPROVED)) {
            saveSiegeClan(player.getClan(), SiegeClanType.DEFENDER_PENDING, false); // Save to database
        }
    }

    /**
     * Remove clan from siege<BR>
     * <BR>
     *
     * @param clanId The int of player's clan id
     */
    public void removeSiegeClan(int clanId) {
        if (clanId <= 0) {
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM siege_clans WHERE castle_id=? and clan_id=?")) {
            statement.setInt(1, castle.getId());
            statement.setInt(2, clanId);
            statement.execute();

            loadSiegeClan();
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Exception: removeSiegeClan(): " + e.getMessage(), e);
        }
    }

    /**
     * Remove clan from siege<BR>
     * <BR>
     *
     * @param clan clan being removed
     */
    public void removeSiegeClan(Clan clan) {
        if ((clan == null) || (clan.getCastleId() == getCastle().getId()) || !SiegeManager.getInstance().checkIsRegistered(clan, getCastle().getId())) {
            return;
        }
        removeSiegeClan(clan.getId());
    }

    /**
     * Remove clan from siege<BR>
     * <BR>
     *
     * @param player The Player of player/clan being removed
     */
    public void removeSiegeClan(Player player) {
        removeSiegeClan(player.getClan());
    }

    /**
     * Teleport players
     *
     * @param teleportWho
     * @param teleportWhere
     */
    private void teleportPlayer(SiegeTeleportWhoType teleportWho, TeleportWhereType teleportWhere) {
        switch (teleportWho) {
            case Owner -> teleportOnwersInZone(teleportWhere);
            case NotOwner -> teleportNotOwnerInZone(teleportWhere);
            case Attacker -> teleportAttackersInZone(teleportWhere);
            case Spectator -> teleportSpectatorsInZone(teleportWhere);
        }
    }

    private void teleportSpectatorsInZone(TeleportWhereType teleportWhere) {
        castle.getZone().forEachPlayer(p -> p.teleToLocation(teleportWhere), p -> !p.isInSiege() && !p.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) && !p.isJailed());
    }

    private void teleportAttackersInZone(TeleportWhereType teleportWhere) {
        getAttackerClans().values().stream().map(a -> ClanTable.getInstance().getClan(a.getClanId())).forEach( c -> {
            c.forEachOnlineMember(p -> p.teleToLocation(teleportWhere), p -> p.isInSiege() && !p.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) && !p.isJailed() );
        });
    }

    private void teleportNotOwnerInZone(TeleportWhereType teleportWhere) {
        castle.getZone().forEachPlayer(p -> p.teleToLocation(teleportWhere),
                p -> !p.inObserverMode() && (p.getClanId() <= 0 || p.getClanId() != castle.getOwnerId()) && !p.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) && !p.isJailed());
    }

    private void teleportOnwersInZone(TeleportWhereType teleportWhere) {
        var defenderClan = getDefenderClan(castle.getOwnerId());
        if(nonNull(defenderClan)) {
            var clan = ClanTable.getInstance().getClan(defenderClan.getClanId());
            clan.forEachOnlineMember(p -> p.teleToLocation(teleportWhere), p -> p.isInSiege() && !p.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) && !p.isJailed());
        }
    }

    /**
     * Add clan as attacker<BR>
     * <BR>
     *
     * @param siegeClan The int of clan's id
     */
    private void addAttacker(SiegeClanData siegeClan) {
        siegeClan.setType(SiegeClanType.ATTACKER);
        getAttackerClans().put(siegeClan.getClanId(), siegeClan);
    }

    private void addDefender(SiegeClanData siegeClan) {
        getDefenderClans().put(siegeClan.getClanId(), siegeClan); // Add registered defender to defender list
    }

    /**
     * <p>
     * Add clan as defender with the specified type
     * </p>
     *
     * @param clanId The int of clan's id
     * @param type   the type of the clan
     */
    private void addDefender(int clanId, SiegeClanType type) {
        getDefenderClans().put(clanId, new SiegeClanData(clanId, type));
    }

    private void addDefenderWaiting(SiegeClanData siegeClan) {
        defendersWaiting.put(siegeClan.getClanId(), siegeClan); // Add registered defender to defender list
    }

    /**
     * @param player The Player of the player trying to register
     * @param typeId -1 = owner 0 = defender, 1 = attacker, 2 = defender waiting
     * @return true if the player can register.
     */
    private boolean checkIfCanRegister(Player player, byte typeId) {
        if (isRegistrationOver) {
            final SystemMessage sm = getSystemMessage(SystemMessageId.THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED);
            sm.addCastleId(castle.getId());
            player.sendPacket(sm);
        } else if (isInProgress) {
            player.sendPacket(SystemMessageId.THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_CANNOT_BE_DONE);
        } else if ((player.getClan() == null) || (player.getClan().getLevel() < SiegeManager.getInstance().getSiegeClanMinLevel())) {
            player.sendPacket(SystemMessageId.ONLY_CLANS_OF_LEVEL_3_OR_ABOVE_MAY_REGISTER_FOR_A_CASTLE_SIEGE);
        } else if (player.getClan().getId() == castle.getOwnerId()) {
            player.sendPacket(SystemMessageId.CASTLE_OWNING_CLANS_ARE_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE);
        } else if (player.getClan().getCastleId() > 0) {
            player.sendPacket(SystemMessageId.A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE);
        } else if (SiegeManager.getInstance().checkIsRegistered(player.getClan(), getCastle().getId())) {
            player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
        } else if (checkIfAlreadyRegisteredForSameDay(player.getClan())) {
            player.sendPacket(SystemMessageId.YOUR_APPLICATION_HAS_BEEN_DENIED_BECAUSE_YOU_HAVE_ALREADY_SUBMITTED_A_REQUEST_FOR_ANOTHER_CASTLE_SIEGE);
        } else if ((typeId == ATTACKER) && (getAttackerClans().size() >= SiegeManager.getInstance().getAttackerMaxClans())) {
            player.sendPacket(SystemMessageId.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE);
        } else if (((typeId == DEFENDER) || (typeId == DEFENDER_NOT_APPROVED) || (typeId == OWNER)) && ((getDefenderClans().size() + getDefendersWaiting().size()) >= SiegeManager.getInstance().getDefenderMaxClans())) {
            player.sendPacket(SystemMessageId.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE);
        } else {
            return true;
        }

        return false;
    }

    /**
     * @param clan The Clan of the player trying to register
     * @return true if the clan has already registered to a siege for the same day.
     */
    public boolean checkIfAlreadyRegisteredForSameDay(Clan clan) {
        for (Siege siege : SiegeManager.getInstance().getSieges()) {
            if (siege == this) {
                continue;
            }

            if(ChronoUnit.DAYS.between(siege.getSiegeDate(), getSiegeDate()) == 0 && (siege.checkIsAttacker(clan) || siege.checkIsDefender(clan) || siege.checkIsDefenderWaiting(clan))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove all spawned towers.
     */
    private void removeTowers() {
        for (FlameTower ct : flameTowers) {
            ct.deleteMe();
        }

        for (ControlTower ct : controlTowers) {
            ct.deleteMe();
        }

        flameTowers.clear();
        controlTowers.clear();
    }

    /**
     * Remove all flags.
     */
    private void removeFlags() {
        getAttackerClans().values().forEach(SiegeClanData::removeFlags);
        getDefenderClans().values().forEach(SiegeClanData::removeFlags);
    }

    /**
     * Remove flags from defenders.
     */
    private void removeDefenderFlags() {
        getDefenderClans().values().forEach(SiegeClanData::removeFlags);
    }

    /**
     * Save castle siege related to database.
     */
    private void saveCastleSiege() {
        setNextSiegeDate(); // Set the next set date for 2 weeks from now
        // Schedule Time registration end
        castle.setSiegeTimeRegistrationEnd(LocalDateTime.now().plusDays(1));

        saveSiegeDate(); // Save the new date
        startAutoTask(); // Prepare auto start siege and end registration
    }

    /**
     * Save siege date to database.
     */
    public void saveSiegeDate() {
        if (scheduledStartSiegeTask != null) {
            scheduledStartSiegeTask.cancel(true);
            scheduledStartSiegeTask = ThreadPool.schedule(new ScheduleStartSiegeTask(castle), 1000);
        }

        castle.updateSiegeDate();
    }

    /**
     * Save registration to database.<BR>
     * <BR>
     *
     * @param clan                 The Clan of player
     * @param typeId               -1 = owner 0 = defender, 1 = attacker, 2 = defender waiting
     * @param isUpdateRegistration
     */
    private void saveSiegeClan(Clan clan, SiegeClanType typeId, boolean isUpdateRegistration) {
        if (clan.getCastleId() > 0) {
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            if ((typeId == SiegeClanType.DEFENDER) || (typeId == SiegeClanType.DEFENDER_PENDING) || (typeId == SiegeClanType.OWNER)) {
                if ((getDefenderClans().size() + getDefendersWaiting().size()) >= SiegeManager.getInstance().getDefenderMaxClans()) {
                    return;
                }
            } else if (getAttackerClans().size() >= SiegeManager.getInstance().getAttackerMaxClans()) {
                return;
            }

            if (!isUpdateRegistration) {
                try (PreparedStatement statement = con.prepareStatement("INSERT INTO siege_clans (clan_id,castle_id,type,castle_owner) values (?,?,?,0)")) {
                    statement.setInt(1, clan.getId());
                    statement.setInt(2, castle.getId());
                    statement.setString(3, typeId.name());
                    statement.execute();
                }
            } else {
                try (PreparedStatement statement = con.prepareStatement("UPDATE siege_clans SET type = ? WHERE castle_id = ? AND clan_id = ?")) {
                    statement.setString(1, typeId.name());
                    statement.setInt(2, castle.getId());
                    statement.setInt(3, clan.getId());
                    statement.execute();
                }
            }

            if ((typeId == SiegeClanType.DEFENDER) || (typeId == SiegeClanType.OWNER)) {
                addDefender(clan.getId(), typeId);
            } else if (typeId == SiegeClanType.ATTACKER) {
                addAttacker(new SiegeClanData(clan.getId(), typeId));
            } else if (typeId == SiegeClanType.DEFENDER_PENDING) {
                addDefenderWaiting(new SiegeClanData(clan.getId(), typeId));
            }
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Exception: saveSiegeClan(Clan clan, int typeId, boolean isUpdateRegistration): " + e.getMessage(), e);
        }
    }

    /**
     * Set the date for the next siege.
     */
    private void setNextSiegeDate() {
        var siegeDate = LocalDateTime.now().plusWeeks(2).withMinute(0).withSecond(0);

        for (SiegeScheduleDate holder : SiegeScheduleData.getInstance().getScheduleDates()) {
            siegeDate = siegeDate.with(ChronoField.DAY_OF_WEEK, holder.getDay()).withHour(holder.getHour());

            if (CastleManager.getInstance().getSiegesOnDate(siegeDate) < holder.getMaxConcurrent()) {
                CastleManager.getInstance().registerSiegeDate(castle, siegeDate);
                break;
            }
        }

        Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME).addCastleId(castle.getId()));
        isRegistrationOver = false; // Allow registration for next siege
    }

    /**
     * Spawn control tower.
     */
    private void spawnControlTower() {
        try {
            for (TowerSpawn ts : SiegeManager.getInstance().getControlTowers(getCastle().getId())) {
                final Spawn spawn = new Spawn(ts.getId());
                spawn.setLocation(ts.getLocation());
                controlTowers.add((ControlTower) spawn.doSpawn());
            }
        } catch (Exception e) {
            LOGGER.warn(": Cannot spawn control tower! " + e);
        }
        controlTowerCount = controlTowers.size();
    }

    /**
     * Spawn flame tower.
     */
    private void spawnFlameTower() {
        try {
            for (TowerSpawn ts : SiegeManager.getInstance().getFlameTowers(getCastle().getId())) {
                final Spawn spawn = new Spawn(ts.getId());
                spawn.setLocation(ts.getLocation());
                final FlameTower tower = (FlameTower) spawn.doSpawn();
                tower.setUpgradeLevel(ts.getUpgradeLevel());
                tower.setZoneList(ts.getZoneList());
                flameTowers.add(tower);
            }
        } catch (Exception e) {
            LOGGER.warn(": Cannot spawn flame tower! " + e);
        }
    }

    /**
     * Spawn siege guard.
     */
    private void spawnSiegeGuard() {
        SiegeGuardManager.getInstance().spawnSiegeGuard(getCastle());

        // Register guard to the closest Control Tower
        // When CT dies, so do all the guards that it controls
        final Set<Spawn> spawned = SiegeGuardManager.getInstance().getSpawnedGuards(getCastle().getId());
        if (!spawned.isEmpty()) {
            ControlTower closestCt;
            double distance;
            double distanceClosest = 0;
            for (Spawn spawn : spawned) {
                if (spawn == null) {
                    continue;
                }

                closestCt = null;
                distanceClosest = Integer.MAX_VALUE;

                for (ControlTower ct : controlTowers) {
                    if (ct == null) {
                        continue;
                    }

                    distance = MathUtil.calculateDistance3D(ct, spawn);

                    if (distance < distanceClosest) {
                        closestCt = ct;
                        distanceClosest = distance;
                    }
                }
                if (closestCt != null) {
                    closestCt.registerGuard(spawn);
                }
            }
        }
    }

    @Override
    public final SiegeClanData getAttackerClan(Clan clan) {
        if (clan == null) {
            return null;
        }
        return getAttackerClan(clan.getId());
    }

    @Override
    public final SiegeClanData getAttackerClan(int clanId) {
        for (var sc : getAttackerClans().values()) {
            if ((sc != null) && (sc.getClanId() == clanId)) {
                return sc;
            }
        }
        return null;
    }

    @Override
    public final IntMap<SiegeClanData> getAttackerClans() {
        if (isNormalSide) {
            return attackers;
        }
        return defenders;
    }

    public final int getAttackerRespawnDelay() {
        return (SiegeManager.getInstance().getAttackerRespawnDelay());
    }

    public final Castle getCastle() {
        return castle;
    }

    @Override
    public final SiegeClanData getDefenderClan(Clan clan) {
        if (clan == null) {
            return null;
        }
        return getDefenderClan(clan.getId());
    }

    @Override
    public final SiegeClanData getDefenderClan(int clanId) {
        return getDefenderClans().get(clanId);
    }

    @Override
    public final IntMap<SiegeClanData> getDefenderClans() {
        if (isNormalSide) {
            return defenders;
        }
        return attackers;
    }

    public final SiegeClanData getDefenderWaitingClan(Clan clan) {
        if (clan == null) {
            return null;
        }
        return getDefenderWaitingClan(clan.getId());
    }

    public final SiegeClanData getDefenderWaitingClan(int clanId) {
        return defendersWaiting.get(clanId);
    }

    public final IntMap<SiegeClanData> getDefendersWaiting() {
        return defendersWaiting;
    }

    public final boolean isInProgress() {
        return isInProgress;
    }

    public final boolean getIsRegistrationOver() {
        return isRegistrationOver;
    }

    @Override
    public final LocalDateTime getSiegeDate() {
        return castle.getSiegeDate();
    }


    public void endTimeRegistration(boolean automatic) {
        castle.setSiegeTimeRegistrationEnd(LocalDateTime.now());
        if (!automatic) {
            saveSiegeDate();
        }
    }

    @Override
    public Set<Npc> getFlag(Clan clan) {
        if (clan != null) {
            var sc = getAttackerClan(clan);
            if (sc != null) {
                return sc.getFlags();
            }
        }
        return null;
    }

    public int getControlTowerCount() {
        return controlTowerCount;
    }

    @Override
    public boolean giveFame() {
        return true;
    }

    @Override
    public int getFameFrequency() {
        return Config.CASTLE_ZONE_FAME_TASK_FREQUENCY;
    }

    @Override
    public int getFameAmount() {
        return Config.CASTLE_ZONE_FAME_AQUIRE_POINTS;
    }

    @Override
    public void updateSiege() {
    }

    public class ScheduleEndSiegeTask implements Runnable {
        private final Castle _castleInst;

        public ScheduleEndSiegeTask(Castle pCastle) {
            _castleInst = pCastle;
        }

        @Override
        public void run() {
            if (!isInProgress) {
                return;
            }

            final long timeRemaining = endDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
            if (timeRemaining > 3600000) {
                announceToPlayer(getSystemMessage(SystemMessageId.S1_HOUR_S_UNTIL_CASTLE_SIEGE_CONCLUSION).addInt(2), true);
                ThreadPool.schedule(new ScheduleEndSiegeTask(_castleInst), timeRemaining - 3600000); // Prepare task for 1 hr left.

            } else if (timeRemaining > 600000) {
                announceToPlayer(getSystemMessage(SystemMessageId.S1_MINUTE_S_UNTIL_CASTLE_SIEGE_CONCLUSION).addInt((int) timeRemaining / 60000), true);
                ThreadPool.schedule(new ScheduleEndSiegeTask(_castleInst), timeRemaining - 600000); // Prepare task for 10 minute left.

            } else if (timeRemaining > 300000) {
                announceToPlayer(getSystemMessage(SystemMessageId.S1_MINUTE_S_UNTIL_CASTLE_SIEGE_CONCLUSION).addInt((int) timeRemaining / 60000), true);
                ThreadPool.schedule(new ScheduleEndSiegeTask(_castleInst), timeRemaining - 300000); // Prepare task for 5 minute left.

            } else if (timeRemaining > 10000) {
                announceToPlayer(getSystemMessage(SystemMessageId.S1_MINUTE_S_UNTIL_CASTLE_SIEGE_CONCLUSION).addInt((int) timeRemaining / 60000), true);
                ThreadPool.schedule(new ScheduleEndSiegeTask(_castleInst), timeRemaining - 10000); // Prepare task for 10 seconds count down

            } else if (timeRemaining > 0) {
                announceToPlayer(getSystemMessage(SystemMessageId.THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECOND_S).addInt((int) timeRemaining / 1000), true);
                ThreadPool.schedule(new ScheduleEndSiegeTask(_castleInst), timeRemaining); // Prepare task for second count down

            } else {
                _castleInst.getSiege().endSiege();
            }
        }
    }

    public class ScheduleStartSiegeTask implements Runnable {
        private final Castle _castleInst;

        public ScheduleStartSiegeTask(Castle pCastle) {
            _castleInst = pCastle;
        }

        @Override
        public void run() {
            scheduledStartSiegeTask.cancel(false);

            if (isInProgress) {
                return;
            }

            if (castle.isSiegeTimeRegistrationSeason()) {
                var regTimeRemaining = Duration.between(Instant.now(), castle.getSiegeTimeRegistrationEnd());
                if (regTimeRemaining.compareTo(Duration.ZERO) > 0) {
                    scheduledStartSiegeTask = ThreadPool.schedule(new ScheduleStartSiegeTask(_castleInst), regTimeRemaining);
                    return;
                }
                endTimeRegistration(true);
            }

            var duration = Duration.between(Instant.now(), getSiegeDate());

            if(duration.compareTo(ChronoUnit.DAYS.getDuration()) > 0) {
                scheduledStartSiegeTask = ThreadPool.schedule(new ScheduleStartSiegeTask(_castleInst), duration.minusDays(1)); // Prepare task for 24 before siege start to end registration
            } else if(duration.compareTo(ChronoUnit.HOURS.getDuration()) > 0) {
                Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.THE_REGISTRATION_TERM_FOR_S1_HAS_ENDED).addCastleId(castle.getId()));
                isRegistrationOver = true;
                clearSiegeWaitingClan();
                scheduledStartSiegeTask = ThreadPool.schedule(new ScheduleStartSiegeTask(_castleInst), duration.minusHours(1)); // Prepare task for 1 hr left before siege start.
            } else if(duration.compareTo(Duration.ZERO) > 0) {
                scheduledStartSiegeTask = ThreadPool.schedule(new ScheduleStartSiegeTask(_castleInst), duration);
            } else {
                _castleInst.getSiege().startSiege();
            }
        }
    }
}
