package org.l2j.gameserver.model.entity;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
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
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.RelationChanged;
import org.l2j.gameserver.network.serverpackets.SiegeInfo;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public class Siege implements Siegable {
    // typeId's
    public static final byte OWNER = -1;
    public static final byte DEFENDER = 0;
    public static final byte ATTACKER = 1;
    public static final byte DEFENDER_NOT_APPROVED = 2;
    protected static final Logger LOGGER = LoggerFactory.getLogger(Siege.class);
    final Castle _castle;
    // must support Concurrent Modifications
    private final List<SiegeClan> _attackerClans = new CopyOnWriteArrayList<>();
    private final List<SiegeClan> _defenderClans = new CopyOnWriteArrayList<>();
    private final List<SiegeClan> _defenderWaitingClans = new CopyOnWriteArrayList<>();
    // Castle setting
    private final List<ControlTower> _controlTowers = new ArrayList<>();
    private final List<FlameTower> _flameTowers = new ArrayList<>();
    protected boolean _isRegistrationOver = false;
    protected Calendar _siegeEndDate;
    protected ScheduledFuture<?> _scheduledStartSiegeTask = null;
    protected int _firstOwnerClanId = -1;
    boolean _isInProgress = false;
    private int _controlTowerCount;
    private boolean _isNormalSide = true; // true = Atk is Atk, false = Atk is Def
    public Siege(Castle castle) {
        _castle = castle;

        startAutoTask();
    }

    @Override
    public void endSiege() {
        if (_isInProgress) {
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_S1_SIEGE_HAS_FINISHED);
            sm.addCastleId(_castle.getResidenceId());
            Broadcast.toAllOnlinePlayers(sm);

            if (_castle.getOwnerId() > 0) {
                final Clan clan = ClanTable.getInstance().getClan(getCastle().getOwnerId());
                sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_S1_IS_VICTORIOUS_OVER_S2_S_CASTLE_SIEGE);
                sm.addString(clan.getName());
                sm.addCastleId(_castle.getResidenceId());
                Broadcast.toAllOnlinePlayers(sm);

                if (clan.getId() == _firstOwnerClanId) {
                    // Owner is unchanged
                    clan.increaseBloodAllianceCount();
                } else {
                    _castle.setTicketBuyCount(0);
                    for (ClanMember member : clan.getMembers()) {
                        if (member != null) {
                            final Player player = member.getPlayerInstance();
                            if ((player != null) && player.isNoble()) {
                                Hero.getInstance().setCastleTaken(player.getObjectId(), getCastle().getResidenceId());
                            }
                        }
                    }
                }
            } else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW);
                sm.addCastleId(_castle.getResidenceId());
                Broadcast.toAllOnlinePlayers(sm);
            }

            for (SiegeClan attackerClan : getAttackerClans()) {
                final Clan clan = ClanTable.getInstance().getClan(attackerClan.getClanId());
                if (clan == null) {
                    continue;
                }

                for (Player member : clan.getOnlineMembers(0)) {
                    member.checkItemRestriction();
                }

                clan.clearSiegeKills();
                clan.clearSiegeDeaths();
            }

            for (SiegeClan defenderClan : getDefenderClans()) {
                final Clan clan = ClanTable.getInstance().getClan(defenderClan.getClanId());
                if (clan == null) {
                    continue;
                }

                for (Player member : clan.getOnlineMembers(0)) {
                    member.checkItemRestriction();
                }

                clan.clearSiegeKills();
                clan.clearSiegeDeaths();
            }

            _castle.updateClansReputation();
            removeFlags(); // Removes all flags. Note: Remove flag before teleporting players
            teleportPlayer(SiegeTeleportWhoType.NotOwner, TeleportWhereType.TOWN); // Teleport to the second closest town
            _isInProgress = false; // Flag so that siege instance can be started
            updatePlayerSiegeStateFlags(true);
            saveCastleSiege(); // Save castle specific data
            clearSiegeClan(); // Clear siege clan from db
            removeTowers(); // Remove all towers from this castle
            SiegeGuardManager.getInstance().unspawnSiegeGuard(getCastle()); // Remove all spawned siege guard from this castle
            if (_castle.getOwnerId() > 0) {
                SiegeGuardManager.getInstance().removeSiegeGuards(getCastle());
            }
            _castle.spawnDoor(); // Respawn door to castle
            _castle.getZone().setIsActive(false);
            _castle.getZone().updateZoneStatusForCharactersInside();
            _castle.getZone().setSiegeInstance(null);

            // Notify to scripts.
            EventDispatcher.getInstance().notifyEventAsync(new OnCastleSiegeFinish(this), getCastle());
        }
    }

    private void removeDefender(SiegeClan sc) {
        if (sc != null) {
            getDefenderClans().remove(sc);
        }
    }

    private void removeAttacker(SiegeClan sc) {
        if (sc != null) {
            getAttackerClans().remove(sc);
        }
    }

    private void addDefender(SiegeClan sc, SiegeClanType type) {
        if (sc == null) {
            return;
        }
        sc.setType(type);
        getDefenderClans().add(sc);
    }

    private void addAttacker(SiegeClan sc) {
        if (sc == null) {
            return;
        }
        sc.setType(SiegeClanType.ATTACKER);
        getAttackerClans().add(sc);
    }

    /**
     * When control of castle changed during siege<BR>
     * <BR>
     */
    public void midVictory() {
        if (_isInProgress) // Siege still in progress
        {
            if (_castle.getOwnerId() > 0) {
                SiegeGuardManager.getInstance().removeSiegeGuards(getCastle()); // Remove all merc entry from db
            }

            if (getDefenderClans().isEmpty() && // If defender doesn't exist (Pc vs Npc)
                    (getAttackerClans().size() == 1 // Only 1 attacker
                    )) {
                final SiegeClan sc_newowner = getAttackerClan(_castle.getOwnerId());
                removeAttacker(sc_newowner);
                addDefender(sc_newowner, SiegeClanType.OWNER);
                endSiege();
                return;
            }
            if (_castle.getOwnerId() > 0) {
                final int allyId = ClanTable.getInstance().getClan(getCastle().getOwnerId()).getAllyId();
                if (getDefenderClans().isEmpty()) // If defender doesn't exist (Pc vs Npc)
                // and only an alliance attacks
                {
                    // The player's clan is in an alliance
                    if (allyId != 0) {
                        boolean allinsamealliance = true;
                        for (SiegeClan sc : getAttackerClans()) {
                            if (sc != null) {
                                if (ClanTable.getInstance().getClan(sc.getClanId()).getAllyId() != allyId) {
                                    allinsamealliance = false;
                                }
                            }
                        }
                        if (allinsamealliance) {
                            final SiegeClan sc_newowner = getAttackerClan(_castle.getOwnerId());
                            removeAttacker(sc_newowner);
                            addDefender(sc_newowner, SiegeClanType.OWNER);
                            endSiege();
                            return;
                        }
                    }
                }

                for (SiegeClan sc : getDefenderClans()) {
                    if (sc != null) {
                        removeDefender(sc);
                        addAttacker(sc);
                    }
                }

                final SiegeClan sc_newowner = getAttackerClan(_castle.getOwnerId());
                removeAttacker(sc_newowner);
                addDefender(sc_newowner, SiegeClanType.OWNER);

                // The player's clan is in an alliance
                for (Clan clan : ClanTable.getInstance().getClanAllies(allyId)) {
                    final SiegeClan sc = getAttackerClan(clan.getId());
                    if (sc != null) {
                        removeAttacker(sc);
                        addDefender(sc, SiegeClanType.DEFENDER);
                    }
                }
                teleportPlayer(SiegeTeleportWhoType.Attacker, TeleportWhereType.SIEGEFLAG); // Teleport to the second closest town
                teleportPlayer(SiegeTeleportWhoType.Spectator, TeleportWhereType.TOWN); // Teleport to the second closest town

                removeDefenderFlags(); // Removes defenders' flags
                _castle.removeUpgrade(); // Remove all castle upgrade
                _castle.spawnDoor(true); // Respawn door to castle but make them weaker (50% hp)
                removeTowers(); // Remove all towers from this castle
                _controlTowerCount = 0; // Each new siege midvictory CT are completely respawned.
                spawnControlTower();
                spawnFlameTower();
                updatePlayerSiegeStateFlags(false);

                // Notify to scripts.
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
        if (!_isInProgress) {
            _firstOwnerClanId = _castle.getOwnerId();

            if (getAttackerClans().isEmpty()) {
                SystemMessage sm;
                if (_firstOwnerClanId <= 0) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST);
                } else {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED);
                    final Clan ownerClan = ClanTable.getInstance().getClan(_firstOwnerClanId);
                    ownerClan.increaseBloodAllianceCount();
                }
                sm.addCastleId(_castle.getResidenceId());
                Broadcast.toAllOnlinePlayers(sm);
                saveCastleSiege();
                return;
            }

            _isNormalSide = true; // Atk is now atk
            _isInProgress = true; // Flag so that same siege instance cannot be started again

            loadSiegeClan(); // Load siege clan from db
            updatePlayerSiegeStateFlags(false);
            teleportPlayer(SiegeTeleportWhoType.NotOwner, TeleportWhereType.TOWN); // Teleport to the closest town
            _controlTowerCount = 0;
            spawnControlTower(); // Spawn control tower
            spawnFlameTower(); // Spawn control tower
            _castle.spawnDoor(); // Spawn door
            spawnSiegeGuard(); // Spawn siege guard
            SiegeGuardManager.getInstance().deleteTickets(getCastle().getResidenceId()); // remove the tickets from the ground
            _castle.getZone().setSiegeInstance(this);
            _castle.getZone().setIsActive(true);
            _castle.getZone().updateZoneStatusForCharactersInside();

            // Schedule a task to prepare auto siege end
            _siegeEndDate = Calendar.getInstance();
            _siegeEndDate.add(Calendar.MINUTE, SiegeManager.getInstance().getSiegeLength());
            ThreadPoolManager.getInstance().schedule(new ScheduleEndSiegeTask(_castle), 1000); // Prepare auto end task

            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_S1_SIEGE_HAS_STARTED);
            sm.addCastleId(_castle.getResidenceId());
            Broadcast.toAllOnlinePlayers(sm);

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
        for (SiegeClan siegeClans : getDefenderClans()) {
            final Clan clan = ClanTable.getInstance().getClan(siegeClans.getClanId());
            if (clan != null) {
                clan.getOnlineMembers(0).forEach(message::sendTo);
            }
        }

        if (bothSides) {
            for (SiegeClan siegeClans : getAttackerClans()) {
                final Clan clan = ClanTable.getInstance().getClan(siegeClans.getClanId());
                if (clan != null) {
                    clan.getOnlineMembers(0).forEach(message::sendTo);
                }
            }
        }
    }

    public void updatePlayerSiegeStateFlags(boolean clear) {
        Clan clan;
        for (SiegeClan siegeclan : getAttackerClans()) {
            if (siegeclan == null) {
                continue;
            }

            clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
            for (Player member : clan.getOnlineMembers(0)) {
                if (clear) {
                    member.setSiegeState((byte) 0);
                    member.setSiegeSide(0);
                    member.setIsInSiege(false);
                    member.stopFameTask();
                } else {
                    member.setSiegeState((byte) 1);
                    member.setSiegeSide(_castle.getResidenceId());
                    if (checkIfInZone(member)) {
                        member.setIsInSiege(true);
                        member.startFameTask(Config.CASTLE_ZONE_FAME_TASK_FREQUENCY * 1000, Config.CASTLE_ZONE_FAME_AQUIRE_POINTS);
                    }
                }
                member.sendPacket(new UserInfo(member));

                World.getInstance().forEachVisibleObject(member, Player.class, player ->
                {
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
        }
        for (SiegeClan siegeclan : getDefenderClans()) {
            if (siegeclan == null) {
                continue;
            }

            clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
            for (Player member : clan.getOnlineMembers(0)) {
                if (clear) {
                    member.setSiegeState((byte) 0);
                    member.setSiegeSide(0);
                    member.setIsInSiege(false);
                    member.stopFameTask();
                } else {
                    member.setSiegeState((byte) 2);
                    member.setSiegeSide(_castle.getResidenceId());
                    if (checkIfInZone(member)) {
                        member.setIsInSiege(true);
                        member.startFameTask(Config.CASTLE_ZONE_FAME_TASK_FREQUENCY * 1000, Config.CASTLE_ZONE_FAME_AQUIRE_POINTS);
                    }
                }
                member.sendPacket(new UserInfo(member));

                World.getInstance().forEachVisibleObject(member, Player.class, player ->
                {
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
        }
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
        saveSiegeClan(ClanTable.getInstance().getClan(clanId), DEFENDER, true);
        loadSiegeClan();
    }

    /**
     * @param object
     * @return true if object is inside the zone
     */
    public boolean checkIfInZone(WorldObject object) {
        return checkIfInZone(object.getX(), object.getY(), object.getZ());
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return true if object is inside the zone
     */
    public boolean checkIfInZone(int x, int y, int z) {
        return (_isInProgress && (_castle.checkIfInZone(x, y, z))); // Castle zone during siege
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

    /**
     * Clear all registered siege clans from database for castle
     */
    public void clearSiegeClan() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM siege_clans WHERE castle_id=?")) {
            statement.setInt(1, _castle.getResidenceId());
            statement.execute();

            if (_castle.getOwnerId() > 0) {
                try (PreparedStatement delete = con.prepareStatement("DELETE FROM siege_clans WHERE clan_id=?")) {
                    delete.setInt(1, _castle.getOwnerId());
                    delete.execute();
                }
            }

            getAttackerClans().clear();
            getDefenderClans().clear();
            _defenderWaitingClans.clear();
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Exception: clearSiegeClan(): " + e.getMessage(), e);
        }
    }

    /**
     * Clear all siege clans waiting for approval from database for castle
     */
    public void clearSiegeWaitingClan() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM siege_clans WHERE castle_id=? and type = 2")) {
            statement.setInt(1, _castle.getResidenceId());
            statement.execute();

            _defenderWaitingClans.clear();
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Exception: clearSiegeWaitingClan(): " + e.getMessage(), e);
        }
    }

    /**
     * Return list of Player registered as attacker in the zone.
     */
    @Override
    public List<Player> getAttackersInZone() {
        //@formatter:off
        return getAttackerClans().stream()
                .map(siegeclan -> ClanTable.getInstance().getClan(siegeclan.getClanId()))
                .filter(Objects::nonNull)
                .flatMap(clan -> clan.getOnlineMembers(0).stream())
                .filter(Player::isInSiege)
                .collect(Collectors.toList());
        //@formatter:on
    }

    /**
     * @return list of Player in the zone.
     */
    public List<Player> getPlayersInZone() {
        return _castle.getZone().getPlayersInside();
    }

    /**
     * @return list of Player owning the castle in the zone.
     */
    public List<Player> getOwnersInZone() {
        //@formatter:off
        return getDefenderClans().stream()
                .filter(siegeclan -> siegeclan.getClanId() == _castle.getOwnerId())
                .map(siegeclan -> ClanTable.getInstance().getClan(siegeclan.getClanId()))
                .filter(Objects::nonNull)
                .flatMap(clan -> clan.getOnlineMembers(0).stream())
                .filter(Player::isInSiege)
                .collect(Collectors.toList());
        //@formatter:on
    }

    /**
     * @return list of Player not registered as attacker or defender in the zone.
     */
    public List<Player> getSpectatorsInZone() {
        return _castle.getZone().getPlayersInside().stream().filter(p -> !p.isInSiege()).collect(Collectors.toList());
    }

    /**
     * Control Tower was killed
     *
     * @param ct
     */
    public void killedCT(Npc ct) {
        _controlTowerCount = Math.max(_controlTowerCount - 1, 0);
    }

    /**
     * Remove the flag that was killed
     *
     * @param flag
     */
    public void killedFlag(Npc flag) {
        getAttackerClans().forEach(siegeClan -> siegeClan.removeFlag(flag));
    }

    /**
     * Display list of registered clans
     *
     * @param player
     */
    public void listRegisterClan(Player player) {
        player.sendPacket(new SiegeInfo(_castle, player));
    }

    /**
     * Register clan as attacker<BR>
     * <BR>
     *
     * @param player The Player of the player trying to register
     */
    public void registerAttacker(Player player) {
        registerAttacker(player, false);
    }

    public void registerAttacker(Player player, boolean force) {
        if (player.getClan() == null) {
            return;
        }
        int allyId = 0;
        if (_castle.getOwnerId() != 0) {
            allyId = ClanTable.getInstance().getClan(getCastle().getOwnerId()).getAllyId();
        }
        if (allyId != 0) {
            if ((player.getClan().getAllyId() == allyId) && !force) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_REGISTER_AS_AN_ATTACKER_BECAUSE_YOU_ARE_IN_AN_ALLIANCE_WITH_THE_CASTLE_OWNING_CLAN);
                return;
            }
        }

        if (force) {
            if (SiegeManager.getInstance().checkIsRegistered(player.getClan(), getCastle().getResidenceId())) {
                player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
            } else {
                saveSiegeClan(player.getClan(), ATTACKER, false); // Save to database
            }
            return;
        }

        if (checkIfCanRegister(player, ATTACKER)) {
            saveSiegeClan(player.getClan(), ATTACKER, false); // Save to database
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
        if (_castle.getOwnerId() <= 0) {
            player.sendMessage("You cannot register as a defender because " + _castle.getName() + " is owned by NPC.");
            return;
        }

        if (force) {
            if (SiegeManager.getInstance().checkIsRegistered(player.getClan(), getCastle().getResidenceId())) {
                player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
            } else {
                saveSiegeClan(player.getClan(), DEFENDER_NOT_APPROVED, false); // Save to database
            }
            return;
        }

        if (checkIfCanRegister(player, DEFENDER_NOT_APPROVED)) {
            saveSiegeClan(player.getClan(), DEFENDER_NOT_APPROVED, false); // Save to database
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
            statement.setInt(1, _castle.getResidenceId());
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
        if ((clan == null) || (clan.getCastleId() == getCastle().getResidenceId()) || !SiegeManager.getInstance().checkIsRegistered(clan, getCastle().getResidenceId())) {
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
     * Start the auto tasks<BR>
     * <BR>
     */
    public void startAutoTask() {
        correctSiegeDateTime();

        LOGGER.info("Siege of " + _castle.getName() + ": " + _castle.getSiegeDate().getTime());

        loadSiegeClan();

        // Schedule siege auto start
        if (_scheduledStartSiegeTask != null) {
            _scheduledStartSiegeTask.cancel(false);
        }
        _scheduledStartSiegeTask = ThreadPoolManager.getInstance().schedule(new ScheduleStartSiegeTask(_castle), 1000);
    }

    /**
     * Teleport players
     *
     * @param teleportWho
     * @param teleportWhere
     */
    public void teleportPlayer(SiegeTeleportWhoType teleportWho, TeleportWhereType teleportWhere) {
        final List<Player> players;
        switch (teleportWho) {
            case Owner: {
                players = getOwnersInZone();
                break;
            }
            case NotOwner: {
                players = _castle.getZone().getPlayersInside();
                final Iterator<Player> it = players.iterator();
                while (it.hasNext()) {
                    final Player player = it.next();
                    if ((player == null) || player.inObserverMode() || ((player.getClanId() > 0) && (player.getClanId() == _castle.getOwnerId()))) {
                        it.remove();
                    }
                }
                break;
            }
            case Attacker: {
                players = getAttackersInZone();
                break;
            }
            case Spectator: {
                players = getSpectatorsInZone();
                break;
            }
            default: {
                players = Collections.emptyList();
            }
        }

        for (Player player : players) {
            if (player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) || player.isJailed()) {
                continue;
            }
            player.teleToLocation(teleportWhere);
        }
    }

    /**
     * Add clan as attacker<BR>
     * <BR>
     *
     * @param clanId The int of clan's id
     */
    private void addAttacker(int clanId) {
        getAttackerClans().add(new SiegeClan(clanId, SiegeClanType.ATTACKER)); // Add registered attacker to attacker list
    }

    /**
     * Add clan as defender<BR>
     * <BR>
     *
     * @param clanId The int of clan's id
     */
    private void addDefender(int clanId) {
        getDefenderClans().add(new SiegeClan(clanId, SiegeClanType.DEFENDER)); // Add registered defender to defender list
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
        getDefenderClans().add(new SiegeClan(clanId, type));
    }

    /**
     * Add clan as defender waiting approval<BR>
     * <BR>
     *
     * @param clanId The int of clan's id
     */
    private void addDefenderWaiting(int clanId) {
        _defenderWaitingClans.add(new SiegeClan(clanId, SiegeClanType.DEFENDER_PENDING)); // Add registered defender to defender list
    }

    /**
     * @param player The Player of the player trying to register
     * @param typeId -1 = owner 0 = defender, 1 = attacker, 2 = defender waiting
     * @return true if the player can register.
     */
    private boolean checkIfCanRegister(Player player, byte typeId) {
        if (_isRegistrationOver) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED);
            sm.addCastleId(_castle.getResidenceId());
            player.sendPacket(sm);
        } else if (_isInProgress) {
            player.sendPacket(SystemMessageId.THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_CANNOT_BE_DONE);
        } else if ((player.getClan() == null) || (player.getClan().getLevel() < SiegeManager.getInstance().getSiegeClanMinLevel())) {
            player.sendPacket(SystemMessageId.ONLY_CLANS_OF_LEVEL_3_OR_ABOVE_MAY_REGISTER_FOR_A_CASTLE_SIEGE);
        } else if (player.getClan().getId() == _castle.getOwnerId()) {
            player.sendPacket(SystemMessageId.CASTLE_OWNING_CLANS_ARE_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE);
        } else if (player.getClan().getCastleId() > 0) {
            player.sendPacket(SystemMessageId.A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE);
        } else if (SiegeManager.getInstance().checkIsRegistered(player.getClan(), getCastle().getResidenceId())) {
            player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
        } else if (checkIfAlreadyRegisteredForSameDay(player.getClan())) {
            player.sendPacket(SystemMessageId.YOUR_APPLICATION_HAS_BEEN_DENIED_BECAUSE_YOU_HAVE_ALREADY_SUBMITTED_A_REQUEST_FOR_ANOTHER_CASTLE_SIEGE);
        } else if ((typeId == ATTACKER) && (getAttackerClans().size() >= SiegeManager.getInstance().getAttackerMaxClans())) {
            player.sendPacket(SystemMessageId.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE);
        } else if (((typeId == DEFENDER) || (typeId == DEFENDER_NOT_APPROVED) || (typeId == OWNER)) && ((getDefenderClans().size() + getDefenderWaitingClans().size()) >= SiegeManager.getInstance().getDefenderMaxClans())) {
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
            if (siege.getSiegeDate().get(Calendar.DAY_OF_WEEK) == getSiegeDate().get(Calendar.DAY_OF_WEEK)) {
                if (siege.checkIsAttacker(clan)) {
                    return true;
                }
                if (siege.checkIsDefender(clan)) {
                    return true;
                }
                if (siege.checkIsDefenderWaiting(clan)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return the correct siege date as Calendar.<BR>
     * <BR>
     */
    public void correctSiegeDateTime() {
        boolean corrected = false;

        if (getCastle().getSiegeDate().getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            // Since siege has past reschedule it to the next one
            // This is usually caused by server being down
            corrected = true;
            setNextSiegeDate();
        }

        if (corrected) {
            saveSiegeDate();
        }
    }

    /**
     * Load siege clans.
     */
    private void loadSiegeClan() {

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT clan_id,type FROM siege_clans where castle_id=?")) {
            getAttackerClans().clear();
            getDefenderClans().clear();
            _defenderWaitingClans.clear();

            // Add castle owner as defender (add owner first so that they are on the top of the defender list)
            if (_castle.getOwnerId() > 0) {
                addDefender(_castle.getOwnerId(), SiegeClanType.OWNER);
            }

            statement.setInt(1, _castle.getResidenceId());
            try (ResultSet rs = statement.executeQuery()) {
                int typeId;
                while (rs.next()) {
                    typeId = rs.getInt("type");
                    if (typeId == DEFENDER) {
                        addDefender(rs.getInt("clan_id"));
                    } else if (typeId == ATTACKER) {
                        addAttacker(rs.getInt("clan_id"));
                    } else if (typeId == DEFENDER_NOT_APPROVED) {
                        addDefenderWaiting(rs.getInt("clan_id"));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Exception: loadSiegeClan(): " + e.getMessage(), e);
        }
    }

    /**
     * Remove all spawned towers.
     */
    private void removeTowers() {
        for (FlameTower ct : _flameTowers) {
            ct.deleteMe();
        }

        for (ControlTower ct : _controlTowers) {
            ct.deleteMe();
        }

        _flameTowers.clear();
        _controlTowers.clear();
    }

    /**
     * Remove all flags.
     */
    private void removeFlags() {
        for (SiegeClan sc : getAttackerClans()) {
            if (sc != null) {
                sc.removeFlags();
            }
        }
        for (SiegeClan sc : getDefenderClans()) {
            if (sc != null) {
                sc.removeFlags();
            }
        }
    }

    /**
     * Remove flags from defenders.
     */
    private void removeDefenderFlags() {
        for (SiegeClan sc : getDefenderClans()) {
            if (sc != null) {
                sc.removeFlags();
            }
        }
    }

    /**
     * Save castle siege related to database.
     */
    private void saveCastleSiege() {
        setNextSiegeDate(); // Set the next set date for 2 weeks from now
        // Schedule Time registration end
        getTimeRegistrationOverDate().setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        _castle.getTimeRegistrationOverDate().add(Calendar.DAY_OF_MONTH, 1);
        _castle.setIsTimeRegistrationOver(false);

        saveSiegeDate(); // Save the new date
        startAutoTask(); // Prepare auto start siege and end registration
    }

    /**
     * Save siege date to database.
     */
    public void saveSiegeDate() {
        if (_scheduledStartSiegeTask != null) {
            _scheduledStartSiegeTask.cancel(true);
            _scheduledStartSiegeTask = ThreadPoolManager.getInstance().schedule(new ScheduleStartSiegeTask(_castle), 1000);
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE castle SET siegeDate = ?, regTimeEnd = ?, regTimeOver = ?  WHERE id = ?")) {
            statement.setLong(1, _castle.getSiegeDate().getTimeInMillis());
            statement.setLong(2, _castle.getTimeRegistrationOverDate().getTimeInMillis());
            statement.setString(3, String.valueOf(_castle.getIsTimeRegistrationOver()));
            statement.setInt(4, _castle.getResidenceId());
            statement.execute();
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Exception: saveSiegeDate(): " + e.getMessage(), e);
        }
    }

    /**
     * Save registration to database.<BR>
     * <BR>
     *
     * @param clan                 The Clan of player
     * @param typeId               -1 = owner 0 = defender, 1 = attacker, 2 = defender waiting
     * @param isUpdateRegistration
     */
    private void saveSiegeClan(Clan clan, byte typeId, boolean isUpdateRegistration) {
        if (clan.getCastleId() > 0) {
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            if ((typeId == DEFENDER) || (typeId == DEFENDER_NOT_APPROVED) || (typeId == OWNER)) {
                if ((getDefenderClans().size() + getDefenderWaitingClans().size()) >= SiegeManager.getInstance().getDefenderMaxClans()) {
                    return;
                }
            } else if (getAttackerClans().size() >= SiegeManager.getInstance().getAttackerMaxClans()) {
                return;
            }

            if (!isUpdateRegistration) {
                try (PreparedStatement statement = con.prepareStatement("INSERT INTO siege_clans (clan_id,castle_id,type,castle_owner) values (?,?,?,0)")) {
                    statement.setInt(1, clan.getId());
                    statement.setInt(2, _castle.getResidenceId());
                    statement.setInt(3, typeId);
                    statement.execute();
                }
            } else {
                try (PreparedStatement statement = con.prepareStatement("UPDATE siege_clans SET type = ? WHERE castle_id = ? AND clan_id = ?")) {
                    statement.setInt(1, typeId);
                    statement.setInt(2, _castle.getResidenceId());
                    statement.setInt(3, clan.getId());
                    statement.execute();
                }
            }

            if ((typeId == DEFENDER) || (typeId == OWNER)) {
                addDefender(clan.getId());
            } else if (typeId == ATTACKER) {
                addAttacker(clan.getId());
            } else if (typeId == DEFENDER_NOT_APPROVED) {
                addDefenderWaiting(clan.getId());
            }
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Exception: saveSiegeClan(Clan clan, int typeId, boolean isUpdateRegistration): " + e.getMessage(), e);
        }
    }

    /**
     * Set the date for the next siege.
     */
    private void setNextSiegeDate() {
        final Calendar cal = _castle.getSiegeDate();
        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.setTimeInMillis(System.currentTimeMillis());
        }

        for (SiegeScheduleDate holder : SiegeScheduleData.getInstance().getScheduleDates()) {
            cal.set(Calendar.DAY_OF_WEEK, holder.getDay());
            cal.set(Calendar.HOUR_OF_DAY, holder.getHour());
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            if (cal.before(Calendar.getInstance())) {
                cal.add(Calendar.WEEK_OF_YEAR, 2);
            }

            if (CastleManager.getInstance().getSiegeDates(cal.getTimeInMillis()) < holder.getMaxConcurrent()) {
                CastleManager.getInstance().registerSiegeDate(getCastle().getResidenceId(), cal.getTimeInMillis());
                break;
            }
        }

        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME);
        sm.addCastleId(_castle.getResidenceId());
        Broadcast.toAllOnlinePlayers(sm);

        _isRegistrationOver = false; // Allow registration for next siege
    }

    /**
     * Spawn control tower.
     */
    private void spawnControlTower() {
        try {
            for (TowerSpawn ts : SiegeManager.getInstance().getControlTowers(getCastle().getResidenceId())) {
                final Spawn spawn = new Spawn(ts.getId());
                spawn.setLocation(ts.getLocation());
                _controlTowers.add((ControlTower) spawn.doSpawn());
            }
        } catch (Exception e) {
            LOGGER.warn(": Cannot spawn control tower! " + e);
        }
        _controlTowerCount = _controlTowers.size();
    }

    /**
     * Spawn flame tower.
     */
    private void spawnFlameTower() {
        try {
            for (TowerSpawn ts : SiegeManager.getInstance().getFlameTowers(getCastle().getResidenceId())) {
                final Spawn spawn = new Spawn(ts.getId());
                spawn.setLocation(ts.getLocation());
                final FlameTower tower = (FlameTower) spawn.doSpawn();
                tower.setUpgradeLevel(ts.getUpgradeLevel());
                tower.setZoneList(ts.getZoneList());
                _flameTowers.add(tower);
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
        final Set<Spawn> spawned = SiegeGuardManager.getInstance().getSpawnedGuards(getCastle().getResidenceId());
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

                for (ControlTower ct : _controlTowers) {
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
    public final SiegeClan getAttackerClan(Clan clan) {
        if (clan == null) {
            return null;
        }
        return getAttackerClan(clan.getId());
    }

    @Override
    public final SiegeClan getAttackerClan(int clanId) {
        for (SiegeClan sc : getAttackerClans()) {
            if ((sc != null) && (sc.getClanId() == clanId)) {
                return sc;
            }
        }
        return null;
    }

    @Override
    public final List<SiegeClan> getAttackerClans() {
        if (_isNormalSide) {
            return _attackerClans;
        }
        return _defenderClans;
    }

    public final int getAttackerRespawnDelay() {
        return (SiegeManager.getInstance().getAttackerRespawnDelay());
    }

    public final Castle getCastle() {
        return _castle;
    }

    @Override
    public final SiegeClan getDefenderClan(Clan clan) {
        if (clan == null) {
            return null;
        }
        return getDefenderClan(clan.getId());
    }

    @Override
    public final SiegeClan getDefenderClan(int clanId) {
        for (SiegeClan sc : getDefenderClans()) {
            if ((sc != null) && (sc.getClanId() == clanId)) {
                return sc;
            }
        }
        return null;
    }

    @Override
    public final List<SiegeClan> getDefenderClans() {
        if (_isNormalSide) {
            return _defenderClans;
        }
        return _attackerClans;
    }

    public final SiegeClan getDefenderWaitingClan(Clan clan) {
        if (clan == null) {
            return null;
        }
        return getDefenderWaitingClan(clan.getId());
    }

    public final SiegeClan getDefenderWaitingClan(int clanId) {
        for (SiegeClan sc : _defenderWaitingClans) {
            if ((sc != null) && (sc.getClanId() == clanId)) {
                return sc;
            }
        }
        return null;
    }

    public final List<SiegeClan> getDefenderWaitingClans() {
        return _defenderWaitingClans;
    }

    public final boolean isInProgress() {
        return _isInProgress;
    }

    public final boolean getIsRegistrationOver() {
        return _isRegistrationOver;
    }

    public final boolean getIsTimeRegistrationOver() {
        return _castle.getIsTimeRegistrationOver();
    }

    @Override
    public final Calendar getSiegeDate() {
        return _castle.getSiegeDate();
    }

    public final Calendar getTimeRegistrationOverDate() {
        return _castle.getTimeRegistrationOverDate();
    }

    public void endTimeRegistration(boolean automatic) {
        _castle.setIsTimeRegistrationOver(true);
        if (!automatic) {
            saveSiegeDate();
        }
    }

    @Override
    public Set<Npc> getFlag(Clan clan) {
        if (clan != null) {
            final SiegeClan sc = getAttackerClan(clan);
            if (sc != null) {
                return sc.getFlag();
            }
        }
        return null;
    }

    public int getControlTowerCount() {
        return _controlTowerCount;
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
            if (!_isInProgress) {
                return;
            }

            try {
                final long timeRemaining = _siegeEndDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                if (timeRemaining > 3600000) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HOUR_S_UNTIL_CASTLE_SIEGE_CONCLUSION);
                    sm.addInt(2);
                    announceToPlayer(sm, true);
                    ThreadPoolManager.getInstance().schedule(new ScheduleEndSiegeTask(_castleInst), timeRemaining - 3600000); // Prepare task for 1 hr left.
                } else if ((timeRemaining <= 3600000) && (timeRemaining > 600000)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_MINUTE_S_UNTIL_CASTLE_SIEGE_CONCLUSION);
                    sm.addInt((int) timeRemaining / 60000);
                    announceToPlayer(sm, true);
                    ThreadPoolManager.getInstance().schedule(new ScheduleEndSiegeTask(_castleInst), timeRemaining - 600000); // Prepare task for 10 minute left.
                } else if ((timeRemaining <= 600000) && (timeRemaining > 300000)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_MINUTE_S_UNTIL_CASTLE_SIEGE_CONCLUSION);
                    sm.addInt((int) timeRemaining / 60000);
                    announceToPlayer(sm, true);
                    ThreadPoolManager.getInstance().schedule(new ScheduleEndSiegeTask(_castleInst), timeRemaining - 300000); // Prepare task for 5 minute left.
                } else if ((timeRemaining <= 300000) && (timeRemaining > 10000)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_MINUTE_S_UNTIL_CASTLE_SIEGE_CONCLUSION);
                    sm.addInt((int) timeRemaining / 60000);
                    announceToPlayer(sm, true);
                    ThreadPoolManager.getInstance().schedule(new ScheduleEndSiegeTask(_castleInst), timeRemaining - 10000); // Prepare task for 10 seconds count down
                } else if ((timeRemaining <= 10000) && (timeRemaining > 0)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECOND_S);
                    sm.addInt((int) timeRemaining / 1000);
                    announceToPlayer(sm, true);
                    ThreadPoolManager.getInstance().schedule(new ScheduleEndSiegeTask(_castleInst), timeRemaining); // Prepare task for second count down
                } else {
                    _castleInst.getSiege().endSiege();
                }
            } catch (Exception e) {
                LOGGER.error(getClass().getSimpleName() + ": ", e);
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
            _scheduledStartSiegeTask.cancel(false);
            if (_isInProgress) {
                return;
            }

            try {
                if (!_castle.getIsTimeRegistrationOver()) {
                    final long regTimeRemaining = getTimeRegistrationOverDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                    if (regTimeRemaining > 0) {
                        _scheduledStartSiegeTask = ThreadPoolManager.getInstance().schedule(new ScheduleStartSiegeTask(_castleInst), regTimeRemaining);
                        return;
                    }
                    endTimeRegistration(true);
                }

                final long timeRemaining = getSiegeDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                if (timeRemaining > 86400000) {
                    _scheduledStartSiegeTask = ThreadPoolManager.getInstance().schedule(new ScheduleStartSiegeTask(_castleInst), timeRemaining - 86400000); // Prepare task for 24 before siege start to end registration
                } else if ((timeRemaining <= 86400000) && (timeRemaining > 13600000)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_REGISTRATION_TERM_FOR_S1_HAS_ENDED);
                    sm.addCastleId(_castle.getResidenceId());
                    Broadcast.toAllOnlinePlayers(sm);
                    _isRegistrationOver = true;
                    clearSiegeWaitingClan();
                    _scheduledStartSiegeTask = ThreadPoolManager.getInstance().schedule(new ScheduleStartSiegeTask(_castleInst), timeRemaining - 13600000); // Prepare task for 1 hr left before siege start.
                } else if ((timeRemaining <= 13600000) && (timeRemaining > 600000)) {
                    _scheduledStartSiegeTask = ThreadPoolManager.getInstance().schedule(new ScheduleStartSiegeTask(_castleInst), timeRemaining - 600000); // Prepare task for 10 minute left.
                } else if ((timeRemaining <= 600000) && (timeRemaining > 300000)) {
                    _scheduledStartSiegeTask = ThreadPoolManager.getInstance().schedule(new ScheduleStartSiegeTask(_castleInst), timeRemaining - 300000); // Prepare task for 5 minute left.
                } else if ((timeRemaining <= 300000) && (timeRemaining > 10000)) {
                    _scheduledStartSiegeTask = ThreadPoolManager.getInstance().schedule(new ScheduleStartSiegeTask(_castleInst), timeRemaining - 10000); // Prepare task for 10 seconds count down
                } else if ((timeRemaining <= 10000) && (timeRemaining > 0)) {
                    _scheduledStartSiegeTask = ThreadPoolManager.getInstance().schedule(new ScheduleStartSiegeTask(_castleInst), timeRemaining); // Prepare task for second count down
                } else {
                    _castleInst.getSiege().startSiege();
                }
            } catch (Exception e) {
                LOGGER.error(getClass().getSimpleName() + ": ", e);
            }
        }
    }
}
