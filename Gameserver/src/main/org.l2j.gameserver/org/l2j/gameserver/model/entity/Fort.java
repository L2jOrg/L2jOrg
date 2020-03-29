package org.l2j.gameserver.model.entity;

import io.github.joealisson.primitive.*;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.FortUpdater;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.data.xml.impl.StaticObjectData;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.FortZone;
import org.l2j.gameserver.world.zone.type.SiegeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author JoeAlisson
 */
public final class Fort extends AbstractResidence {
    /**
     * Fortress Functions
     */
    public static final int FUNC_TELEPORT = 1;
    public static final int FUNC_RESTORE_HP = 2;
    public static final int FUNC_RESTORE_MP = 3;
    public static final int FUNC_RESTORE_EXP = 4;
    public static final int FUNC_SUPPORT = 5;

    protected static final Logger LOGGER = LoggerFactory.getLogger(Fort.class);

    private final List<Door> doors = new ArrayList<>();
    private final IntMap<FortFunction> functions = new CHashIntMap<>();
    private final ScheduledFuture<?>[] updater = new ScheduledFuture<?>[2];
    private final Set<Spawn> siegeNpcs = ConcurrentHashMap.newKeySet();
    private final Set<Spawn> npcCommanders = ConcurrentHashMap.newKeySet();
    private final Set<Spawn> specialEnvoys = ConcurrentHashMap.newKeySet();
    private final IntIntMap envoyCastles = new HashIntIntMap(2);
    private final IntSet availableCastles = new HashIntSet(1);

    Clan owner = null;

    private StaticWorldObject flagPole = null;
    private volatile FortSiege siege = null;
    private Calendar siegeDate;
    private Calendar lastOwnedTime;
    private SiegeZone zone;
    private int fortType = 0;
    private int state = 0;
    private int castleId = 0;
    private int supplyLevel = 0;

    private boolean isSuspiciousMerchantSpawned = false;

    public Fort(int fortId) {
        super(fortId);
        load();
        loadFlagPoles();
        if (owner != null) {
            setVisibleFlag(true);
            loadFunctions();
        }
        initResidenceZone();
        // initFunctions();
        initNpcs(); // load and spawn npcs (Always spawned)
        initSiegeNpcs(); // load suspicious merchants (Despawned 10mins before siege)
        // spawnSuspiciousMerchant(); // spawn suspicious merchants
        initNpcCommanders(); // npc Commanders (not monsters) (Spawned during siege)
        spawnNpcCommanders(); // spawn npc Commanders
        initSpecialEnvoys(); // envoys from castles (Spawned after fort taken)
        if ((owner != null) && (state == 0)) {
            spawnSpecialEnvoys();
        }
    }

    /**
     * Return function with id
     *
     * @param type
     * @return
     */
    public FortFunction getFortFunction(int type) {
        return functions.get(type);
    }

    public void endOfSiege(Clan clan) {
        ThreadPool.execute(new endFortressSiege(this, clan));
    }

    /**
     * Move non clan members off fort area and to nearest town.<BR>
     * <BR>
     */
    public void banishForeigners() {
        getResidenceZone().banishForeigners(owner.getId());
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return true if object is inside the zone
     */
    public boolean checkIfInZone(int x, int y, int z) {
        return getZone().isInsideZone(x, y, z);
    }

    public SiegeZone getZone() {
        if (zone == null) {
            for (SiegeZone zone : ZoneManager.getInstance().getAllZones(SiegeZone.class)) {
                if (zone.getSiegeObjectId() == getId()) {
                    this.zone = zone;
                    break;
                }
            }
        }
        return zone;
    }

    @Override
    public FortZone getResidenceZone() {
        return (FortZone) super.getResidenceZone();
    }

    /**
     * Get the objects distance to this fort
     *
     * @param obj
     * @return
     */
    public double getDistance(WorldObject obj) {
        return getZone().getDistanceToZone(obj);
    }

    public void closeDoor(Player activeChar, int doorId) {
        openCloseDoor(activeChar, doorId, false);
    }

    public void openDoor(Player activeChar, int doorId) {
        openCloseDoor(activeChar, doorId, true);
    }

    public void openCloseDoor(Player activeChar, int doorId, boolean open) {
        if (activeChar.getClan() != owner) {
            return;
        }

        final Door door = getDoor(doorId);
        if (door != null) {
            if (open) {
                door.openMe();
            } else {
                door.closeMe();
            }
        }
    }

    // This method is used to begin removing all fort upgrades
    public void removeUpgrade() {
        removeDoorUpgrade();
    }

    /**
     * This method will set owner for Fort
     *
     * @param clan
     * @param updateClansReputation
     * @return
     */
    public boolean setOwner(Clan clan, boolean updateClansReputation) {
        if (clan == null) {
            LOGGER.warn(": Updating Fort owner with null clan!!!");
            return false;
        }

        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_FORTRESS_BATTLE_OF_S1_HAS_FINISHED);
        sm.addCastleId(getId());
        getSiege().announceToPlayer(sm);

        final Clan oldowner = owner;
        if ((oldowner != null) && (clan != oldowner)) {
            // Remove points from old owner
            updateClansReputation(oldowner, true);
            try {
                final Player oldleader = oldowner.getLeader().getPlayerInstance();
                if (oldleader != null) {
                    if (oldleader.getMountType() == MountType.WYVERN) {
                        oldleader.dismount();
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Exception in setOwner: " + e.getMessage(), e);
            }
            removeOwner(true);
        }
        setFortState(0, 0); // initialize fort state

        // if clan already have castle, don't store him in fortress
        if (clan.getCastleId() > 0) {
            getSiege().announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.THE_REBEL_ARMY_RECAPTURED_THE_FORTRESS));
            return false;
        }

        // Give points to new owner
        if (updateClansReputation) {
            updateClansReputation(clan, false);
        }

        spawnSpecialEnvoys();
        // if clan have already fortress, remove it
        if (clan.getFortId() > 0) {
            FortDataManager.getInstance().getFortByOwner(clan).removeOwner(true);
        }

        setSupplyLvL(0);
        setOwnerClan(clan);
        updateOwnerInDB(); // Update in database
        saveFortVariables();

        if (getSiege().isInProgress()) {
            getSiege().endSiege();
        }

        for (Player member : clan.getOnlineMembers(0)) {
            giveResidentialSkills(member);
            member.sendSkillList();
        }
        return true;
    }

    public void removeOwner(boolean updateDB) {
        final Clan clan = owner;
        if (clan != null) {
            for (Player member : clan.getOnlineMembers(0)) {
                removeResidentialSkills(member);
                member.sendSkillList();
            }
            clan.setFortId(0);
            clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
            setOwnerClan(null);
            setSupplyLvL(0);
            saveFortVariables();
            removeAllFunctions();
            if (updateDB) {
                updateOwnerInDB();
            }
        }
    }

    public void raiseSupplyLvL() {
        supplyLevel++;
        if (supplyLevel > Config.FS_MAX_SUPPLY_LEVEL) {
            supplyLevel = Config.FS_MAX_SUPPLY_LEVEL;
        }
    }

    public int getSupplyLvL() {
        return supplyLevel;
    }

    public void setSupplyLvL(int val) {
        if (val <= Config.FS_MAX_SUPPLY_LEVEL) {
            supplyLevel = val;
        }
    }

    public void saveFortVariables() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE fort SET supplyLvL=? WHERE id = ?")) {
            ps.setInt(1, supplyLevel);
            ps.setInt(2, getId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Exception: saveFortVariables(): " + e.getMessage(), e);
        }
    }

    /**
     * Show or hide flag inside flag pole.
     *
     * @param val
     */
    public void setVisibleFlag(boolean val) {
        final StaticWorldObject flagPole = this.flagPole;
        if (flagPole != null) {
            flagPole.setMeshIndex(val ? 1 : 0);
        }
    }

    /**
     * Respawn all doors on fort grounds<BR>
     * <BR>
     */
    public void resetDoors() {
        for (Door door : doors) {
            if (door.isOpen()) {
                door.closeMe();
            }
            if (door.isDead()) {
                door.doRevive();
            }
            if (door.getCurrentHp() < door.getMaxHp()) {
                door.setCurrentHp(door.getMaxHp());
            }
        }
        loadDoorUpgrade(); // Check for any upgrade the doors may have
    }

    // This method upgrade door
    public void upgradeDoor(int doorId, int hp, int pDef, int mDef) {
        final Door door = getDoor(doorId);
        if (door != null) {
            door.setCurrentHp(door.getMaxHp() + hp);
            saveDoorUpgrade(doorId, hp, pDef, mDef);
        }
    }

    // This method loads fort
    @Override
    protected void load() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM fort WHERE id = ?")) {
            ps.setInt(1, getId());
            int ownerId = 0;
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    setName(rs.getString("name"));

                    siegeDate = Calendar.getInstance();
                    lastOwnedTime = Calendar.getInstance();
                    siegeDate.setTimeInMillis(rs.getLong("siegeDate"));
                    lastOwnedTime.setTimeInMillis(rs.getLong("lastOwnedTime"));
                    ownerId = rs.getInt("owner");
                    fortType = rs.getInt("fortType");
                    state = rs.getInt("state");
                    castleId = rs.getInt("castleId");
                    supplyLevel = rs.getInt("supplyLvL");
                }
            }
            if (ownerId > 0) {
                final Clan clan = ClanTable.getInstance().getClan(ownerId); // Try to find clan instance
                clan.setFortId(getId());
                setOwnerClan(clan);
                final int runCount = getOwnedTime() / (Config.FS_UPDATE_FRQ * 60);
                long initial = System.currentTimeMillis() - lastOwnedTime.getTimeInMillis();
                while (initial > (Config.FS_UPDATE_FRQ * 60000)) {
                    initial -= Config.FS_UPDATE_FRQ * 60000;
                }
                initial = (Config.FS_UPDATE_FRQ * 60000) - initial;
                if ((Config.FS_MAX_OWN_TIME <= 0) || (getOwnedTime() < (Config.FS_MAX_OWN_TIME * 3600))) {
                    updater[0] = ThreadPool.scheduleAtFixedRate(new FortUpdater(this, clan, runCount, FortUpdater.UpdaterType.PERIODIC_UPDATE), initial, Config.FS_UPDATE_FRQ * 60000); // Schedule owner tasks to start running
                    if (Config.FS_MAX_OWN_TIME > 0) {
                        updater[1] = ThreadPool.scheduleAtFixedRate(new FortUpdater(this, clan, runCount, FortUpdater.UpdaterType.MAX_OWN_TIME), 3600000, 3600000); // Schedule owner tasks to remove owener
                    }
                } else {
                    updater[1] = ThreadPool.schedule(new FortUpdater(this, clan, 0, FortUpdater.UpdaterType.MAX_OWN_TIME), 60000); // Schedule owner tasks to remove owner
                }
            } else {
                setOwnerClan(null);
            }
        } catch (Exception e) {
            LOGGER.warn("Exception: loadFortData(): " + e.getMessage(), e);
        }
    }

    /**
     * Load All Functions
     */
    private void loadFunctions() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM fort_functions WHERE fort_id = ?")) {
            ps.setInt(1, getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    functions.put(rs.getInt("type"), new FortFunction(rs.getInt("type"), rs.getInt("lvl"), rs.getInt("lease"), 0, rs.getLong("rate"), rs.getLong("endTime"), true));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception: Fort.loadFunctions(): " + e.getMessage(), e);
        }
    }

    /**
     * Remove function In List and in DB
     *
     * @param functionType
     */
    public void removeFunction(int functionType) {
        functions.remove(functionType);
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM fort_functions WHERE fort_id=? AND type=?")) {
            ps.setInt(1, getId());
            ps.setInt(2, functionType);
            ps.execute();
        } catch (Exception e) {
            LOGGER.error("Exception: Fort.removeFunctions(int functionType): " + e.getMessage(), e);
        }
    }

    /**
     * Remove all fort functions.
     */
    private void removeAllFunctions() {
        functions.keySet().forEach(this::removeFunction);
    }

    public boolean updateFunctions(Player player, int type, int lvl, int lease, long rate, boolean addNew) {
        if (player == null) {
            return false;
        }
        if ((lease > 0) && !player.destroyItemByItemId("Consume", CommonItem.ADENA, lease, null, true)) {
            return false;
        }
        if (addNew) {
            functions.put(type, new FortFunction(type, lvl, lease, 0, rate, 0, false));
        } else if ((lvl == 0) && (lease == 0)) {
            removeFunction(type);
        } else if ((lease - functions.get(type).getLease()) > 0) {
            functions.remove(type);
            functions.put(type, new FortFunction(type, lvl, lease, 0, rate, -1, false));
        } else {
            functions.get(type).setLease(lease);
            functions.get(type).setLvl(lvl);
            functions.get(type).dbSave();
        }
        return true;
    }

    public void activateInstance() {
        loadDoor();
    }

    // This method loads fort door data from database
    private void loadDoor() {
        for (Door door : DoorDataManager.getInstance().getDoors()) {
            if ((door.getFort() != null) && (door.getFort().getId() == getId())) {
                doors.add(door);
            }
        }
    }

    private void loadFlagPoles() {
        for (StaticWorldObject obj : StaticObjectData.getInstance().getStaticObjects()) {
            if ((obj.getType() == 3) && obj.getName().startsWith(getName())) {
                flagPole = obj;
                break;
            }
        }
        if (flagPole == null) {
            throw new NullPointerException("Can't find flagpole for Fort " + this);
        }
    }

    // This method loads fort door upgrade data from database
    private void loadDoorUpgrade() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM fort_doorupgrade WHERE fortId = ?")) {
            ps.setInt(1, getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    upgradeDoor(rs.getInt("id"), rs.getInt("hp"), rs.getInt("pDef"), rs.getInt("mDef"));
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Exception: loadFortDoorUpgrade(): " + e.getMessage(), e);
        }
    }

    private void removeDoorUpgrade() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM fort_doorupgrade WHERE fortId = ?")) {
            ps.setInt(1, getId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Exception: removeDoorUpgrade(): " + e.getMessage(), e);
        }
    }

    private void saveDoorUpgrade(int doorId, int hp, int pDef, int mDef) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO fort_doorupgrade (doorId, hp, pDef, mDef) VALUES (?,?,?,?)")) {
            ps.setInt(1, doorId);
            ps.setInt(2, hp);
            ps.setInt(3, pDef);
            ps.setInt(4, mDef);
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Exception: saveDoorUpgrade(int doorId, int hp, int pDef, int mDef): " + e.getMessage(), e);
        }
    }

    private void updateOwnerInDB() {
        final Clan clan = owner;
        int clanId = 0;
        if (clan != null) {
            clanId = clan.getId();
            lastOwnedTime.setTimeInMillis(System.currentTimeMillis());
        } else {
            lastOwnedTime.setTimeInMillis(0);
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE fort SET owner=?,lastOwnedTime=?,state=?,castleId=? WHERE id = ?")) {
            ps.setInt(1, clanId);
            ps.setLong(2, lastOwnedTime.getTimeInMillis());
            ps.setInt(3, 0);
            ps.setInt(4, 0);
            ps.setInt(5, getId());
            ps.execute();

            // Announce to clan members
            if (clan != null) {
                clan.setFortId(getId()); // Set has fort flag for new owner
                SystemMessage sm;
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_VICTORIOUS_IN_THE_FORTRESS_BATTLE_OF_S2);
                sm.addString(clan.getName());
                sm.addCastleId(getId());
                World.getInstance().getPlayers().forEach(p -> p.sendPacket(sm));
                clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
                clan.broadcastToOnlineMembers(new PlaySound(1, "Siege_Victory", 0, 0, 0, 0, 0));
                if (updater[0] != null) {
                    updater[0].cancel(false);
                }
                if (updater[1] != null) {
                    updater[1].cancel(false);
                }
                updater[0] = ThreadPool.scheduleAtFixedRate(new FortUpdater(this, clan, 0, FortUpdater.UpdaterType.PERIODIC_UPDATE), Config.FS_UPDATE_FRQ * 60000, Config.FS_UPDATE_FRQ * 60000); // Schedule owner tasks to start running
                if (Config.FS_MAX_OWN_TIME > 0) {
                    updater[1] = ThreadPool.scheduleAtFixedRate(new FortUpdater(this, clan, 0, FortUpdater.UpdaterType.MAX_OWN_TIME), 3600000, 3600000); // Schedule owner tasks to remove owner
                }
            } else {
                if (updater[0] != null) {
                    updater[0].cancel(false);
                }
                updater[0] = null;
                if (updater[1] != null) {
                    updater[1].cancel(false);
                }
                updater[1] = null;
            }
        } catch (Exception e) {
            LOGGER.warn("Exception: updateOwnerInDB(Clan clan): " + e.getMessage(), e);
        }
    }

    @Override
    public final int getOwnerId() {
        final Clan clan = owner;
        return clan != null ? clan.getId() : -1;
    }

    public final Clan getOwnerClan() {
        return owner;
    }

    public final void setOwnerClan(Clan clan) {
        setVisibleFlag(clan != null);
        owner = clan;
    }

    public final Door getDoor(int doorId) {
        if (doorId <= 0) {
            return null;
        }

        for (Door door : doors) {
            if (door.getId() == doorId) {
                return door;
            }
        }
        return null;
    }

    public final List<Door> getDoors() {
        return doors;
    }

    public final StaticWorldObject getFlagPole() {
        return flagPole;
    }

    public final FortSiege getSiege() {
        if (siege == null) {
            synchronized (this) {
                if (siege == null) {
                    siege = new FortSiege(this);
                }
            }
        }
        return siege;
    }

    public final Calendar getSiegeDate() {
        return siegeDate;
    }

    public final void setSiegeDate(Calendar siegeDate) {
        this.siegeDate = siegeDate;
    }

    public final int getOwnedTime() {
        return lastOwnedTime.getTimeInMillis() == 0 ? 0 : (int) ((System.currentTimeMillis() - lastOwnedTime.getTimeInMillis()) / 1000);
    }

    public final int getTimeTillRebelArmy() {
        return lastOwnedTime.getTimeInMillis() == 0 ? 0 : (int) (((lastOwnedTime.getTimeInMillis() + (Config.FS_MAX_OWN_TIME * 3600000)) - System.currentTimeMillis()) / 1000);
    }

    public final long getTimeTillNextFortUpdate() {
        return updater[0] == null ? 0 : updater[0].getDelay(TimeUnit.SECONDS);
    }

    public void updateClansReputation(Clan owner, boolean removePoints) {
        if (owner != null) {
            if (removePoints) {
                owner.takeReputationScore(Config.LOOSE_FORT_POINTS, true);
            } else {
                owner.addReputationScore(Config.TAKE_FORT_POINTS, true);
            }
        }
    }

    /**
     * @return Returns state of fortress.<BR>
     * <BR>
     * 0 - not decided yet<BR>
     * 1 - independent<BR>
     * 2 - contracted with castle<BR>
     */
    public final int getFortState() {
        return state;
    }

    /**
     * @param state    <ul>
     *                 <li>0 - not decided yet</li>
     *                 <li>1 - independent</li>
     *                 <li>2 - contracted with castle</li>
     *                 </ul>
     * @param castleId the Id of the contracted castle (0 if no contract with any castle)
     */
    public final void setFortState(int state, int castleId) {
        this.state = state;
        this.castleId = castleId;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE fort SET state=?,castleId=? WHERE id = ?")) {
            ps.setInt(1, this.state);
            ps.setInt(2, this.castleId);
            ps.setInt(3, getId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Exception: setFortState(int state, int castleId): " + e.getMessage(), e);
        }
    }

    /**
     * @return the fortress type (0 - small (3 commanders), 1 - big (4 commanders + control room))
     */
    public final int getFortType() {
        return fortType;
    }

    /**
     * @param npcId the Id of the ambassador NPC
     * @return the Id of the castle this ambassador represents
     */
    public final int getCastleIdByAmbassador(int npcId) {
        return envoyCastles.get(npcId);
    }

    /**
     * @param npcId the Id of the ambassador NPC
     * @return the castle this ambassador represents
     */
    public final Castle getCastleByAmbassador(int npcId) {
        return CastleManager.getInstance().getCastleById(getCastleIdByAmbassador(npcId));
    }

    /**
     * @return the Id of the castle contracted with this fortress
     */
    public final int getContractedCastleId() {
        return castleId;
    }

    /**
     * @return the castle contracted with this fortress ({@code null} if no contract with any castle)
     */
    public final Castle getContractedCastle() {
        return CastleManager.getInstance().getCastleById(getContractedCastleId());
    }

    /**
     * Check if this is a border fortress (associated with multiple castles).
     *
     * @return {@code true} if this is a border fortress (associated with more than one castle), {@code false} otherwise
     */
    public final boolean isBorderFortress() {
        return availableCastles.size() > 1;
    }

    /**
     * @return the amount of barracks in this fortress
     */
    public final int getFortSize() {
        return fortType == 0 ? 3 : 5;
    }

    public void spawnSuspiciousMerchant() {
        if (isSuspiciousMerchantSpawned) {
            return;
        }
        isSuspiciousMerchantSpawned = true;

        for (Spawn spawnDat : siegeNpcs) {
            spawnDat.doSpawn();
            spawnDat.startRespawn();
        }
    }

    public void despawnSuspiciousMerchant() {
        if (!isSuspiciousMerchantSpawned) {
            return;
        }
        isSuspiciousMerchantSpawned = false;

        for (Spawn spawnDat : siegeNpcs) {
            spawnDat.stopRespawn();
            spawnDat.getLastSpawn().deleteMe();
        }
    }

    public void spawnNpcCommanders() {
        for (Spawn spawnDat : npcCommanders) {
            spawnDat.doSpawn();
            spawnDat.startRespawn();
        }
    }

    public void despawnNpcCommanders() {
        for (Spawn spawnDat : npcCommanders) {
            spawnDat.stopRespawn();
            spawnDat.getLastSpawn().deleteMe();
        }
    }

    public void spawnSpecialEnvoys() {
        for (Spawn spawnDat : specialEnvoys) {
            spawnDat.doSpawn();
            spawnDat.startRespawn();
        }
    }

    private void initNpcs() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM fort_spawnlist WHERE fortId = ? AND spawnType = ?")) {
            ps.setInt(1, getId());
            ps.setInt(2, 0);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Spawn spawnDat = new Spawn(rs.getInt("npcId"));
                    spawnDat.setAmount(1);
                    spawnDat.setXYZ(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                    spawnDat.setHeading(rs.getInt("heading"));
                    spawnDat.setRespawnDelay(60);
                    SpawnTable.getInstance().addNewSpawn(spawnDat, false);
                    spawnDat.doSpawn();
                    spawnDat.startRespawn();
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Fort " + getId() + " initNpcs: Spawn could not be initialized: " + e.getMessage(), e);
        }
    }

    private void initSiegeNpcs() {
        siegeNpcs.clear();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, npcId, x, y, z, heading FROM fort_spawnlist WHERE fortId = ? AND spawnType = ? ORDER BY id")) {
            ps.setInt(1, getId());
            ps.setInt(2, 2);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Spawn spawnDat = new Spawn(rs.getInt("npcId"));
                    spawnDat.setAmount(1);
                    spawnDat.setXYZ(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                    spawnDat.setHeading(rs.getInt("heading"));
                    spawnDat.setRespawnDelay(60);
                    siegeNpcs.add(spawnDat);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Fort " + getId() + " initSiegeNpcs: Spawn could not be initialized: " + e.getMessage(), e);
        }
    }

    private void initNpcCommanders() {
        npcCommanders.clear();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, npcId, x, y, z, heading FROM fort_spawnlist WHERE fortId = ? AND spawnType = ? ORDER BY id")) {
            ps.setInt(1, getId());
            ps.setInt(2, 1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Spawn spawnDat = new Spawn(rs.getInt("npcId"));
                    spawnDat.setAmount(1);
                    spawnDat.setXYZ(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                    spawnDat.setHeading(rs.getInt("heading"));
                    spawnDat.setRespawnDelay(60);
                    npcCommanders.add(spawnDat);
                }
            }
        } catch (Exception e) {
            // problem with initializing spawn, go to next one
            LOGGER.warn("Fort " + getId() + " initNpcCommanders: Spawn could not be initialized: " + e.getMessage(), e);
        }
    }

    private void initSpecialEnvoys() {
        specialEnvoys.clear();
        envoyCastles.clear();
        availableCastles.clear();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, npcId, x, y, z, heading, castleId FROM fort_spawnlist WHERE fortId = ? AND spawnType = ? ORDER BY id")) {
            ps.setInt(1, getId());
            ps.setInt(2, 3);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final int castleId = rs.getInt("castleId");
                    final int npcId = rs.getInt("npcId");
                    final Spawn spawnDat = new Spawn(npcId);
                    spawnDat.setAmount(1);
                    spawnDat.setXYZ(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                    spawnDat.setHeading(rs.getInt("heading"));
                    spawnDat.setRespawnDelay(60);
                    specialEnvoys.add(spawnDat);
                    envoyCastles.put(npcId, castleId);
                    availableCastles.add(castleId);
                }
            }
        } catch (Exception e) {
            // problem with initializing spawn, go to next one
            LOGGER.warn("Fort " + getId() + " initSpecialEnvoys: Spawn could not be initialized: " + e.getMessage(), e);
        }
    }

    @Override
    protected void initResidenceZone() {
        for (FortZone zone : ZoneManager.getInstance().getAllZones(FortZone.class)) {
            if (zone.getResidenceId() == getId()) {
                setResidenceZone(zone);
                break;
            }
        }
    }

    private static class endFortressSiege implements Runnable {
        private final Fort _f;
        private final Clan _clan;

        public endFortressSiege(Fort f, Clan clan) {
            _f = f;
            _clan = clan;
        }

        @Override
        public void run() {
            try {
                _f.setOwner(_clan, true);
            } catch (Exception e) {
                LOGGER.warn("Exception in endFortressSiege " + e.getMessage(), e);
            }
        }
    }

    public class FortFunction {
        final int _type;
        final long _rate;
        public boolean _cwh;
        protected int _fee;
        protected int _tempFee;
        protected boolean _inDebt;
        long _endDate;
        private int _lvl;

        public FortFunction(int type, int lvl, int lease, int tempLease, long rate, long time, boolean cwh) {
            _type = type;
            _lvl = lvl;
            _fee = lease;
            _tempFee = tempLease;
            _rate = rate;
            _endDate = time;
            initializeTask(cwh);
        }

        public int getType() {
            return _type;
        }

        public int getLvl() {
            return _lvl;
        }

        public void setLvl(int lvl) {
            _lvl = lvl;
        }

        public int getLease() {
            return _fee;
        }

        public void setLease(int lease) {
            _fee = lease;
        }

        public long getRate() {
            return _rate;
        }

        public long getEndTime() {
            return _endDate;
        }

        public void setEndTime(long time) {
            _endDate = time;
        }

        private void initializeTask(boolean cwh) {
            if (owner == null) {
                return;
            }
            final long currentTime = System.currentTimeMillis();
            if (_endDate > currentTime) {
                ThreadPool.schedule(new FunctionTask(cwh), _endDate - currentTime);
            } else {
                ThreadPool.schedule(new FunctionTask(cwh), 0);
            }
        }

        public void dbSave() {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("REPLACE INTO fort_functions (fort_id, type, lvl, lease, rate, endTime) VALUES (?,?,?,?,?,?)")) {
                ps.setInt(1, getId());
                ps.setInt(2, _type);
                ps.setInt(3, _lvl);
                ps.setInt(4, _fee);
                ps.setLong(5, _rate);
                ps.setLong(6, _endDate);
                ps.execute();
            } catch (Exception e) {
                LOGGER.error("Exception: Fort.updateFunctions(int type, int lvl, int lease, long rate, long time, boolean addNew): " + e.getMessage(), e);
            }
        }

        private class FunctionTask implements Runnable {
            public FunctionTask(boolean cwh) {
                _cwh = cwh;
            }

            @Override
            public void run() {
                try {
                    if (owner == null) {
                        return;
                    }
                    if ((owner.getWarehouse().getAdena() >= _fee) || !_cwh) {
                        final int fee = _endDate == -1 ? _tempFee : _fee;
                        setEndTime(System.currentTimeMillis() + _rate);
                        dbSave();
                        if (_cwh) {
                            owner.getWarehouse().destroyItemByItemId("CS_function_fee", CommonItem.ADENA, fee, null, null);
                        }
                        ThreadPool.schedule(new FunctionTask(true), _rate);
                    } else {
                        removeFunction(_type);
                    }
                } catch (Throwable t) {
                }
            }
        }
    }
}
