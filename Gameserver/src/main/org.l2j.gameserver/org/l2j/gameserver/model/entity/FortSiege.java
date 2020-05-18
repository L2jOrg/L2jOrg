package org.l2j.gameserver.model.entity;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.enums.FortTeleportWhoType;
import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.instancemanager.FortSiegeManager;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.FortCommander;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.sieges.OnFortSiegeFinish;
import org.l2j.gameserver.model.events.impl.sieges.OnFortSiegeStart;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.nonNull;


public class FortSiege implements Siegable {
    protected static final Logger LOGGER = LoggerFactory.getLogger(FortSiege.class);

    // SQL
    private static final String DELETE_FORT_SIEGECLANS_BY_CLAN_ID = "DELETE FROM fortsiege_clans WHERE fort_id = ? AND clan_id = ?";
    private static final String DELETE_FORT_SIEGECLANS = "DELETE FROM fortsiege_clans WHERE fort_id = ?";
    protected final Fort fort;
    private final IntMap<SiegeClanData> _attackerClans = new CHashIntMap<>();
    private final Collection<Spawn> _siegeGuards = new LinkedList<>();
    // Fort setting
    protected Set<Spawn> _commanders = ConcurrentHashMap.newKeySet();
    boolean _isInProgress = false;
    ScheduledFuture<?> _siegeEnd = null;
    ScheduledFuture<?> _siegeRestore = null;
    ScheduledFuture<?> _siegeStartTask = null;
    public FortSiege(Fort fort) {
        this.fort = fort;

        checkAutoTask();
        FortSiegeManager.getInstance().addSiege(this);
    }

    /**
     * When siege ends.
     */
    @Override
    public void endSiege() {
        if (_isInProgress) {
            _isInProgress = false; // Flag so that siege instance can be started
            removeFlags(); // Removes all flags. Note: Remove flag before teleporting players
            unSpawnFlags();

            updatePlayerSiegeStateFlags(true);

            int ownerId = -1;
            if (fort.getOwnerClan() != null) {
                ownerId = fort.getOwnerClan().getId();
            }
            fort.getZone().banishForeigners(ownerId);
            fort.getZone().setIsActive(false);
            fort.getZone().updateZoneStatusForCharactersInside();
            fort.getZone().setSiegeInstance(null);

            saveFortSiege(); // Save fort specific data
            clearSiegeClan(); // Clear siege clan from db
            removeCommanders(); // Remove commander from this fort

            fort.spawnNpcCommanders(); // Spawn NPC commanders
            unspawnSiegeGuard(); // Remove all spawned siege guard from this fort
            fort.resetDoors(); // Respawn door to fort

            ThreadPool.schedule(new ScheduleSuspiciousMerchantSpawn(), FortSiegeManager.getInstance().getSuspiciousMerchantRespawnDelay() * 60 * 1000); // Prepare 3hr task for suspicious merchant respawn
            setSiegeDateTime(true); // store suspicious merchant spawn in DB

            if (_siegeEnd != null) {
                _siegeEnd.cancel(true);
                _siegeEnd = null;
            }
            if (_siegeRestore != null) {
                _siegeRestore.cancel(true);
                _siegeRestore = null;
            }

            if ((fort.getOwnerClan() != null) && (fort.getFlagPole().getMeshIndex() == 0)) {
                fort.setVisibleFlag(true);
            }

            LOGGER.info(getClass().getSimpleName() + ": Siege of " + fort.getName() + " fort finished.");

            // Notify to scripts.
            EventDispatcher.getInstance().notifyEventAsync(new OnFortSiegeFinish(this), getFort());
        }
    }

    /**
     * When siege starts
     */
    @Override
    public void startSiege() {
        if (!_isInProgress) {
            if (_siegeStartTask != null) // used admin command "admin_startfortsiege"
            {
                _siegeStartTask.cancel(true);
                fort.despawnSuspiciousMerchant();
            }
            _siegeStartTask = null;

            if (_attackerClans.isEmpty()) {
                return;
            }

            _isInProgress = true; // Flag so that same siege instance cannot be started again

            loadSiegeClan(); // Load siege clan from db
            updatePlayerSiegeStateFlags(false);
            teleportPlayer(FortTeleportWhoType.Attacker, TeleportWhereType.TOWN); // Teleport to the closest town

            fort.despawnNpcCommanders(); // Despawn NPC commanders
            spawnCommanders(); // Spawn commanders
            fort.resetDoors(); // Spawn door
            spawnSiegeGuard(); // Spawn siege guard
            fort.setVisibleFlag(false);
            fort.getZone().setSiegeInstance(this);
            fort.getZone().setIsActive(true);
            fort.getZone().updateZoneStatusForCharactersInside();

            // Schedule a task to prepare auto siege end
            _siegeEnd = ThreadPool.schedule(new ScheduleEndSiegeTask(), FortSiegeManager.getInstance().getSiegeLength() * 60 * 1000); // Prepare auto end task

            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_FORTRESS_BATTLE_S1_HAS_BEGUN);
            sm.addCastleId(fort.getId());
            announceToPlayer(sm);
            saveFortSiege();

            LOGGER.info(getClass().getSimpleName() + ": Siege of " + fort.getName() + " fort started.");

            // Notify to scripts.
            EventDispatcher.getInstance().notifyEventAsync(new OnFortSiegeStart(this), getFort());
        }
    }

    /**
     * Announce to player.
     *
     * @param sm the system message to send to player
     */
    public void announceToPlayer(SystemMessage sm) {
        // announce messages only for participants
        Clan clan;
        for (var siegeclan : _attackerClans.values()) {
            clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
            for (Player member : clan.getOnlineMembers(0)) {
                if (member != null) {
                    member.sendPacket(sm);
                }
            }
        }
        if (fort.getOwnerClan() != null) {
            clan = ClanTable.getInstance().getClan(getFort().getOwnerClan().getId());
            for (Player member : clan.getOnlineMembers(0)) {
                if (member != null) {
                    member.sendPacket(sm);
                }
            }
        }
    }

    public void announceToPlayer(SystemMessage sm, String s) {
        sm.addString(s);
        announceToPlayer(sm);
    }

    public void updatePlayerSiegeStateFlags(boolean clear) {
        Clan clan;
        for (var siegeclan : _attackerClans.values()) {
            clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
            for (Player member : clan.getOnlineMembers(0)) {
                if (member == null) {
                    continue;
                }

                if (clear) {
                    member.setSiegeState((byte) 0);
                    member.setSiegeSide(0);
                    member.setIsInSiege(false);
                    member.stopFameTask();
                } else {
                    member.setSiegeState((byte) 1);
                    member.setSiegeSide(fort.getId());
                    if (checkIfInZone(member)) {
                        member.setIsInSiege(true);
                        member.startFameTask(Config.FORTRESS_ZONE_FAME_TASK_FREQUENCY * 1000, Config.FORTRESS_ZONE_FAME_AQUIRE_POINTS);
                    }
                }
                member.broadcastUserInfo();
            }
        }
        if (fort.getOwnerClan() != null) {
            clan = ClanTable.getInstance().getClan(getFort().getOwnerClan().getId());
            for (Player member : clan.getOnlineMembers(0)) {
                if (member == null) {
                    continue;
                }

                if (clear) {
                    member.setSiegeState((byte) 0);
                    member.setSiegeSide(0);
                    member.setIsInSiege(false);
                    member.stopFameTask();
                } else {
                    member.setSiegeState((byte) 2);
                    member.setSiegeSide(fort.getId());
                    if (checkIfInZone(member)) {
                        member.setIsInSiege(true);
                        member.startFameTask(Config.FORTRESS_ZONE_FAME_TASK_FREQUENCY * 1000, Config.FORTRESS_ZONE_FAME_AQUIRE_POINTS);
                    }
                }
                member.broadcastUserInfo();
            }
        }
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
        return (_isInProgress && (fort.checkIfInZone(x, y, z))); // Fort zone during siege
    }

    /**
     * @param clan The Clan of the player
     * @return true if clan is attacker
     */
    @Override
    public boolean checkIsAttacker(Clan clan) {
        return (getAttackerClan(clan) != null);
    }

    /**
     * @param clan The Clan of the player
     * @return true if clan is defender
     */
    @Override
    public boolean checkIsDefender(Clan clan) {
        if ((clan != null) && (fort.getOwnerClan() == clan)) {
            return true;
        }

        return false;
    }

    /**
     * Clear all registered siege clans from database for fort
     */
    public void clearSiegeClan() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM fortsiege_clans WHERE fort_id=?")) {
            ps.setInt(1, fort.getId());
            ps.execute();

            if (fort.getOwnerClan() != null) {
                try (PreparedStatement delete = con.prepareStatement("DELETE FROM fortsiege_clans WHERE clan_id=?")) {
                    delete.setInt(1, fort.getOwnerClan().getId());
                    delete.execute();
                }
            }

            _attackerClans.clear();

            // if siege is in progress, end siege
            if (_isInProgress) {
                endSiege();
            }

            // if siege isn't in progress (1hr waiting time till siege starts), cancel waiting time
            if (_siegeStartTask != null) {
                _siegeStartTask.cancel(true);
                _siegeStartTask = null;
            }
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Exception: clearSiegeClan(): " + e.getMessage(), e);
        }
    }

    /**
     * Set the date for the next siege.
     */
    private void clearSiegeDate() {
        fort.getSiegeDate().setTimeInMillis(0);
    }

    /**
     * @return list of Player owning the fort in the zone.
     */
    public List<Player> getOwnersInZone() {
        final List<Player> players = new LinkedList<>();
        if (fort.getOwnerClan() != null) {
            final Clan clan = ClanTable.getInstance().getClan(getFort().getOwnerClan().getId());
            if (clan != fort.getOwnerClan()) {
                return null;
            }

            for (Player player : clan.getOnlineMembers(0)) {
                if (player == null) {
                    continue;
                }

                if (player.isInSiege()) {
                    players.add(player);
                }
            }
        }
        return players;
    }

    /**
     * TODO: To DP AI<br>
     * Commander was killed
     *
     * @param instance
     */
    public void killedCommander(FortCommander instance) {
        if ((fort != null) && (!_commanders.isEmpty())) {
            final Spawn spawn = instance.getSpawn();
            if (spawn != null) {
                for (FortSiegeSpawn spawn2 : FortSiegeManager.getInstance().getCommanderSpawnList(getFort().getId())) {
                    if (spawn2.getId() == spawn.getId()) {
                        NpcStringId npcString = null;
                        switch (spawn2.getMessageId()) {
                            case 1: {
                                npcString = NpcStringId.YOU_MAY_HAVE_BROKEN_OUR_ARROWS_BUT_YOU_WILL_NEVER_BREAK_OUR_WILL_ARCHERS_RETREAT;
                                break;
                            }
                            case 2: {
                                npcString = NpcStringId.AIIEEEE_COMMAND_CENTER_THIS_IS_GUARD_UNIT_WE_NEED_BACKUP_RIGHT_AWAY;
                                break;
                            }
                            case 3: {
                                npcString = NpcStringId.AT_LAST_THE_MAGIC_CIRCLE_THAT_PROTECTS_THE_FORTRESS_HAS_WEAKENED_VOLUNTEERS_STAND_BACK;
                                break;
                            }
                            case 4: {
                                npcString = NpcStringId.I_FEEL_SO_MUCH_GRIEF_THAT_I_CAN_T_EVEN_TAKE_CARE_OF_MYSELF_THERE_ISN_T_ANY_REASON_FOR_ME_TO_STAY_HERE_ANY_LONGER;
                                break;
                            }
                        }
                        if (npcString != null) {
                            instance.broadcastSay(ChatType.NPC_SHOUT, npcString);
                        }
                    }
                }
                _commanders.remove(spawn);
                if (_commanders.isEmpty()) {
                    // spawn fort flags
                    spawnFlag(fort.getId());
                    // cancel door/commanders respawn
                    if (_siegeRestore != null) {
                        _siegeRestore.cancel(true);
                    }
                    // open doors in main building
                    for (Door door : fort.getDoors()) {
                        if (door.getIsShowHp()) {
                            continue;
                        }

                        // TODO this also opens control room door at big fort
                        door.openMe();
                    }
                    fort.getSiege().announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.ALL_BARRACKS_ARE_OCCUPIED));
                }
                // schedule restoring doors/commanders respawn
                else if (_siegeRestore == null) {
                    fort.getSiege().announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.THE_BARRACKS_HAVE_BEEN_SEIZED));
                    _siegeRestore = ThreadPool.schedule(new ScheduleSiegeRestore(), FortSiegeManager.getInstance().getCountDownLength() * 60 * 1000);
                } else {
                    fort.getSiege().announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.THE_BARRACKS_HAVE_BEEN_SEIZED));
                }
            } else {
                LOGGER.warn(": FortSiege.killedCommander(): killed commander, but commander not registered for fortress. NpcId: " + instance.getId() + " FortId: " + fort.getId());
            }
        }
    }

    /**
     * Remove the flag that was killed
     *
     * @param flag
     */
    public void killedFlag(Npc flag) {
        if (flag == null) {
            return;
        }

        for (var clan : _attackerClans.values()) {
            if (clan.removeFlag(flag)) {
                return;
            }
        }
    }

    /**
     * Register clan as attacker.<BR>
     *
     * @param player          The Player of the player trying to register.
     * @param checkConditions True if should be checked conditions, false otherwise
     * @return Number that defines what happened. <BR>
     * 0 - Player don't have clan.<BR>
     * 1 - Player don't have enough adena to register.<BR>
     * 2 - Is not right time to register Fortress now.<BR>
     * 3 - Players clan is already registered to siege.<BR>
     * 4 - Players clan is successfully registered to siege.
     */
    public int addAttacker(Player player, boolean checkConditions) {
        if (player.getClan() == null) {
            return 0; // Player dont have clan
        }

        if (checkConditions) {
            if (fort.getSiege().getAttackerClans().isEmpty() && (player.getInventory().getAdena() < 250000)) {
                return 1; // Player don't have enough adena to register
            }

            for (Fort fort : FortDataManager.getInstance().getForts()) {
                if (fort.getSiege().getAttackerClan(player.getClanId()) != null) {
                    return 3; // Players clan is already registered to siege
                }

                if ((fort.getOwnerClan() == player.getClan()) && (fort.getSiege().isInProgress() || (fort.getSiege()._siegeStartTask != null))) {
                    return 3; // Players clan is already registered to siege
                }
            }
        }

        saveSiegeClan(player.getClan());
        if (_attackerClans.size() == 1) {
            if (checkConditions) {
                player.reduceAdena("FortressSiege", 250000, null, true);
            }
            startAutoTask(true);
        }
        return 4; // Players clan is successfully registered to siege
    }

    /**
     * Remove clan from siege
     *
     * @param clan The clan being removed
     */
    public void removeAttacker(Clan clan) {
        if ((clan == null) || (clan.getFortId() == getFort().getId()) || !FortSiegeManager.getInstance().checkIsRegistered(clan, getFort().getId())) {
            return;
        }
        removeSiegeClan(clan.getId());
    }

    /**
     * This function does not do any checks and should not be called from bypass !
     *
     * @param clanId
     */
    private void removeSiegeClan(int clanId) {
        final String query = (clanId != 0) ? DELETE_FORT_SIEGECLANS_BY_CLAN_ID : DELETE_FORT_SIEGECLANS;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, fort.getId());
            if (clanId != 0) {
                statement.setInt(2, clanId);
            }
            statement.execute();

            loadSiegeClan();
            if (_attackerClans.isEmpty()) {
                if (_isInProgress) {
                    endSiege();
                } else {
                    saveFortSiege(); // Clear siege time in DB
                }

                if (_siegeStartTask != null) {
                    _siegeStartTask.cancel(true);
                    _siegeStartTask = null;
                }
            }
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Exception on removeSiegeClan: " + e.getMessage(), e);
        }
    }

    /**
     * Start the auto tasks
     */
    public void checkAutoTask() {
        if (_siegeStartTask != null) {
            return;
        }

        final long delay = getFort().getSiegeDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

        if (delay < 0) {
            // siege time in past
            saveFortSiege();
            clearSiegeClan(); // remove all clans
            // spawn suspicious merchant immediately
            ThreadPool.execute(new ScheduleSuspiciousMerchantSpawn());
        } else {
            loadSiegeClan();
            if (_attackerClans.isEmpty()) {
                // no attackers - waiting for suspicious merchant spawn
                ThreadPool.schedule(new ScheduleSuspiciousMerchantSpawn(), delay);
            } else {
                // preparing start siege task
                if (delay > 3600000) // more than hour, how this can happens ? spawn suspicious merchant
                {
                    ThreadPool.execute(new ScheduleSuspiciousMerchantSpawn());
                    _siegeStartTask = ThreadPool.schedule(new ScheduleStartSiegeTask(3600), delay - 3600000);
                }
                if (delay > 600000) // more than 10 min, spawn suspicious merchant
                {
                    ThreadPool.execute(new ScheduleSuspiciousMerchantSpawn());
                    _siegeStartTask = ThreadPool.schedule(new ScheduleStartSiegeTask(600), delay - 600000);
                } else if (delay > 300000) {
                    _siegeStartTask = ThreadPool.schedule(new ScheduleStartSiegeTask(300), delay - 300000);
                } else if (delay > 60000) {
                    _siegeStartTask = ThreadPool.schedule(new ScheduleStartSiegeTask(60), delay - 60000);
                } else {
                    // lower than 1 min, set to 1 min
                    _siegeStartTask = ThreadPool.schedule(new ScheduleStartSiegeTask(60), 0);
                }

                LOGGER.info(getClass().getSimpleName() + ": Siege of " + fort.getName() + " fort: " + fort.getSiegeDate().getTime());
            }
        }
    }

    /**
     * Start the auto task
     *
     * @param setTime
     */
    public void startAutoTask(boolean setTime) {
        if (_siegeStartTask != null) {
            return;
        }

        if (setTime) {
            setSiegeDateTime(false);
        }

        if (fort.getOwnerClan() != null) {
            fort.getOwnerClan().broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.A_FORTRESS_IS_UNDER_ATTACK));
        }

        // Execute siege auto start
        _siegeStartTask = ThreadPool.schedule(new ScheduleStartSiegeTask(3600), 0);
    }


    private void teleportPlayer(FortTeleportWhoType teleportWho, TeleportWhereType teleportWhere) {
        switch (teleportWho) {
            case Owner -> teleportOwnersInZone(teleportWhere);
            case Attacker -> teleportAttackersInZone(teleportWhere);
            default ->
                fort.getZone().forEachPlayer(p -> p.teleToLocation(teleportWhere), this::canTeleport);
        }
    }

    private void teleportAttackersInZone(TeleportWhereType teleportWhere) {
        _attackerClans.values().stream().map(siegeClan -> ClanTable.getInstance().getClan(siegeClan.getClanId()))
            .forEach( clan -> clan.forEachOnlineMember(p -> p.teleToLocation(teleportWhere), p -> canTeleport(p) && p.isInSiege()));
    }

    private void teleportOwnersInZone(TeleportWhereType teleportWhere) {
        if(nonNull(fort.getOwnerClan())) {
            fort.getOwnerClan().forEachOnlineMember(p -> p.teleToLocation(teleportWhere), p -> canTeleport(p) && p.isInSiege());
        }
    }

    private boolean canTeleport(Player p) {
        return !p.isJailed() && !p.canOverrideCond(PcCondOverride.FORTRESS_CONDITIONS);
    }

    /**
     * Add clan as attacker<
     *
     * @param clanId
     */
    private void addAttacker(int clanId) {
        _attackerClans.put(clanId, new SiegeClanData(clanId, SiegeClanType.ATTACKER, fort.getId()));
    }

    /**
     * @param clan
     * @return {@code true} if the clan has already registered to a siege for the same day, {@code false} otherwise.
     */
    public boolean checkIfAlreadyRegisteredForSameDay(Clan clan) {
        for (FortSiege siege : FortSiegeManager.getInstance().getSieges()) {
            if (siege == this) {
                continue;
            }

            if (siege.getSiegeDate().getDayOfWeek() == getSiegeDate().getDayOfWeek()) {
                if (siege.checkIsAttacker(clan)) {
                    return true;
                }
                if (siege.checkIsDefender(clan)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void setSiegeDateTime(boolean merchant) {
        final Calendar newDate = Calendar.getInstance();
        if (merchant) {
            newDate.add(Calendar.MINUTE, FortSiegeManager.getInstance().getSuspiciousMerchantRespawnDelay());
        } else {
            newDate.add(Calendar.MINUTE, 60);
        }
        fort.setSiegeDate(newDate);
        saveSiegeDate();
    }

    /**
     * Load siege clans.
     */
    private void loadSiegeClan() {
        _attackerClans.clear();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT clan_id FROM fortsiege_clans WHERE fort_id=?")) {
            ps.setInt(1, fort.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    addAttacker(rs.getInt("clan_id"));
                }
            }
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Exception: loadSiegeClan(): " + e.getMessage(), e);
        }
    }

    /**
     * Remove commanders.
     */
    private void removeCommanders() {
        // Remove all instance of commanders for this fort
        for (Spawn spawn : _commanders) {
            if (spawn != null) {
                spawn.stopRespawn();
                if (spawn.getLastSpawn() != null) {
                    spawn.getLastSpawn().deleteMe();
                }
            }
        }
        _commanders.clear();
    }

    /**
     * Remove all flags.
     */
    private void removeFlags() {
        for (var sc : _attackerClans.values()) {
            if (sc != null) {
                sc.removeFlags();
            }
        }
    }

    /**
     * Save fort siege related to database.
     */
    private void saveFortSiege() {
        clearSiegeDate(); // clear siege date
        saveSiegeDate(); // Save the new date
    }

    /**
     * Save siege date to database.
     */
    private void saveSiegeDate() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE fort SET siegeDate = ? WHERE id = ?")) {
            ps.setLong(1, fort.getSiegeDate().getTimeInMillis());
            ps.setInt(2, fort.getId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Exception: saveSiegeDate(): " + e.getMessage(), e);
        }
    }

    /**
     * Save registration to database.
     *
     * @param clan
     */
    private void saveSiegeClan(Clan clan) {
        if (getAttackerClans().size() >= FortSiegeManager.getInstance().getAttackerMaxClans()) {
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("INSERT INTO fortsiege_clans (clan_id,fort_id) values (?,?)")) {
            statement.setInt(1, clan.getId());
            statement.setInt(2, fort.getId());
            statement.execute();

            addAttacker(clan.getId());
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Exception: saveSiegeClan(Clan clan): " + e.getMessage(), e);
        }
    }

    /**
     * Spawn commanders.
     */
    private void spawnCommanders() {
        // Set commanders array size if one does not exist
        try {
            _commanders.clear();
            for (FortSiegeSpawn _sp : FortSiegeManager.getInstance().getCommanderSpawnList(getFort().getId())) {
                final Spawn spawnDat = new Spawn(_sp.getId());
                spawnDat.setAmount(1);
                spawnDat.setXYZ(_sp.getLocation());
                spawnDat.setHeading(_sp.getLocation().getHeading());
                spawnDat.setRespawnDelay(60);
                spawnDat.doSpawn();
                spawnDat.stopRespawn();
                _commanders.add(spawnDat);
            }
        } catch (Exception e) {
            // problem with initializing spawn, go to next one
            LOGGER.warn(getClass().getSimpleName() + ": FortSiege.spawnCommander: Spawn could not be initialized: " + e.getMessage(), e);
        }
    }

    private void spawnFlag(int Id) {
        for (CombatFlag cf : FortSiegeManager.getInstance().getFlagList(Id)) {
            cf.spawnMe();
        }
    }

    private void unSpawnFlags() {
        if (FortSiegeManager.getInstance().getFlagList(getFort().getId()) == null) {
            return;
        }

        for (CombatFlag cf : FortSiegeManager.getInstance().getFlagList(getFort().getId())) {
            cf.unSpawnMe();
        }
    }

    public void loadSiegeGuard() {
        _siegeGuards.clear();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT npcId, x, y, z, heading, respawnDelay FROM fort_siege_guards WHERE fortId = ?")) {
            final int fortId = fort.getId();
            ps.setInt(1, fortId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Spawn spawn = new Spawn(rs.getInt("npcId"));
                    spawn.setAmount(1);
                    spawn.setXYZ(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                    spawn.setHeading(rs.getInt("heading"));
                    spawn.setRespawnDelay(rs.getInt("respawnDelay"));
                    spawn.setLocationId(0);

                    _siegeGuards.add(spawn);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Error loading siege guard for fort " + fort.getName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Spawn siege guard.
     */
    private void spawnSiegeGuard() {
        try {
            for (Spawn spawnDat : _siegeGuards) {
                spawnDat.doSpawn();
                if (spawnDat.getRespawnDelay() == 0) {
                    spawnDat.stopRespawn();
                } else {
                    spawnDat.startRespawn();
                }
            }
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Error spawning siege guards for fort " + fort.getName() + ":" + e.getMessage(), e);
        }
    }

    private void unspawnSiegeGuard() {
        try {
            for (Spawn spawnDat : _siegeGuards) {
                spawnDat.stopRespawn();
                if (spawnDat.getLastSpawn() != null) {
                    spawnDat.getLastSpawn().doDie(spawnDat.getLastSpawn());
                }
            }
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Error unspawning siege guards for fort " + fort.getName() + ":" + e.getMessage(), e);
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
        for (var sc : _attackerClans.values()) {
            if ((sc != null) && (sc.getClanId() == clanId)) {
                return sc;
            }
        }

        return null;
    }

    @Override
    public final IntMap<SiegeClanData> getAttackerClans() {
        return _attackerClans;
    }

    public final Fort getFort() {
        return fort;
    }

    public final boolean isInProgress() {
        return _isInProgress;
    }

    @Override
    public final LocalDateTime getSiegeDate() {
        return LocalDateTime.now(); // fort.getSiegeDate();
    }

    @Override
    public Set<Npc> getFlag(Clan clan) {
        if (clan != null) {
            final var sc = getAttackerClan(clan);
            if (sc != null) {
                return sc.getFlags();
            }
        }

        return null;
    }

    public void resetSiege() {
        // reload commanders and repair doors
        removeCommanders();
        spawnCommanders();
        fort.resetDoors();
    }

    public Set<Spawn> getCommanders() {
        return _commanders;
    }

    @Override
    public SiegeClanData getDefenderClan(int clanId) {
        return null;
    }

    @Override
    public SiegeClanData getDefenderClan(Clan clan) {
        return null;
    }

    @Override
    public IntMap<SiegeClanData> getDefenderClans() {
        return null;
    }

    @Override
    public boolean giveFame() {
        return true;
    }

    @Override
    public int getFameFrequency() {
        return Config.FORTRESS_ZONE_FAME_TASK_FREQUENCY;
    }

    @Override
    public int getFameAmount() {
        return Config.FORTRESS_ZONE_FAME_AQUIRE_POINTS;
    }

    @Override
    public void updateSiege() {
    }

    public class ScheduleEndSiegeTask implements Runnable {
        @Override
        public void run() {
            if (!_isInProgress) {
                return;
            }

            try {
                _siegeEnd = null;
                endSiege();
            } catch (Exception e) {
                LOGGER.warn(getClass().getSimpleName() + ": Exception: ScheduleEndSiegeTask() for Fort: " + fort.getName() + " " + e.getMessage(), e);
            }
        }
    }

    public class ScheduleStartSiegeTask implements Runnable {
        private final Fort _fortInst;
        private final int _time;

        public ScheduleStartSiegeTask(int time) {
            _fortInst = fort;
            _time = time;
        }

        @Override
        public void run() {
            if (_isInProgress) {
                return;
            }

            try {
                final SystemMessage sm;
                if (_time == 3600) // 1hr remains
                {
                    ThreadPool.schedule(new ScheduleStartSiegeTask(600), 3000000); // Prepare task for 10 minutes left.
                } else if (_time == 600) // 10min remains
                {
                    fort.despawnSuspiciousMerchant();
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_MINUTE_S_UNTIL_THE_FORTRESS_BATTLE_STARTS);
                    sm.addInt(10);
                    announceToPlayer(sm);
                    ThreadPool.schedule(new ScheduleStartSiegeTask(300), 300000); // Prepare task for 5 minutes left.
                } else if (_time == 300) // 5min remains
                {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_MINUTE_S_UNTIL_THE_FORTRESS_BATTLE_STARTS);
                    sm.addInt(5);
                    announceToPlayer(sm);
                    ThreadPool.schedule(new ScheduleStartSiegeTask(60), 240000); // Prepare task for 1 minute left.
                } else if (_time == 60) // 1min remains
                {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_MINUTE_S_UNTIL_THE_FORTRESS_BATTLE_STARTS);
                    sm.addInt(1);
                    announceToPlayer(sm);
                    ThreadPool.schedule(new ScheduleStartSiegeTask(30), 30000); // Prepare task for 30 seconds left.
                } else if (_time == 30) // 30seconds remains
                {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_SECOND_S_UNTIL_THE_FORTRESS_BATTLE_STARTS);
                    sm.addInt(30);
                    announceToPlayer(sm);
                    ThreadPool.schedule(new ScheduleStartSiegeTask(10), 20000); // Prepare task for 10 seconds left.
                } else if (_time == 10) // 10seconds remains
                {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_SECOND_S_UNTIL_THE_FORTRESS_BATTLE_STARTS);
                    sm.addInt(10);
                    announceToPlayer(sm);
                    ThreadPool.schedule(new ScheduleStartSiegeTask(5), 5000); // Prepare task for 5 seconds left.
                } else if (_time == 5) // 5seconds remains
                {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_SECOND_S_UNTIL_THE_FORTRESS_BATTLE_STARTS);
                    sm.addInt(5);
                    announceToPlayer(sm);
                    ThreadPool.schedule(new ScheduleStartSiegeTask(1), 4000); // Prepare task for 1 seconds left.
                } else if (_time == 1) // 1seconds remains
                {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_SECOND_S_UNTIL_THE_FORTRESS_BATTLE_STARTS);
                    sm.addInt(1);
                    announceToPlayer(sm);
                    ThreadPool.schedule(new ScheduleStartSiegeTask(0), 1000); // Prepare task start siege.
                } else if (_time == 0)// start siege
                {
                    _fortInst.getSiege().startSiege();
                } else {
                    LOGGER.warn(": Exception: ScheduleStartSiegeTask(): unknown siege time: " + _time);
                }
            } catch (Exception e) {
                LOGGER.warn(getClass().getSimpleName() + ": Exception: ScheduleStartSiegeTask() for Fort: " + _fortInst.getName() + " " + e.getMessage(), e);
            }
        }
    }

    public class ScheduleSuspiciousMerchantSpawn implements Runnable {
        @Override
        public void run() {
            if (_isInProgress) {
                return;
            }

            try {
                fort.spawnSuspiciousMerchant();
            } catch (Exception e) {
                LOGGER.warn(getClass().getSimpleName() + ": Exception: ScheduleSuspicoiusMerchantSpawn() for Fort: " + fort.getName() + " " + e.getMessage(), e);
            }
        }
    }

    public class ScheduleSiegeRestore implements Runnable {
        @Override
        public void run() {
            if (!_isInProgress) {
                return;
            }

            try {
                _siegeRestore = null;
                resetSiege();
                announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.THE_BARRACKS_FUNCTION_HAS_BEEN_RESTORED));
            } catch (Exception e) {
                LOGGER.warn(getClass().getSimpleName() + ": Exception: ScheduleSiegeRestore() for Fort: " + fort.getName() + " " + e.getMessage(), e);
            }
        }
    }
}
