package org.l2j.gameserver.model.entity;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.CastleData;
import org.l2j.gameserver.data.xml.impl.DoorData;
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.instancemanager.*;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Artefact;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.CastleSpawnHolder;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.zone.type.L2CastleZone;
import org.l2j.gameserver.model.zone.type.L2ResidenceTeleportZone;
import org.l2j.gameserver.model.zone.type.L2SiegeZone;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExCastleState;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public final class Castle extends AbstractResidence {
    /**
     * Castle Functions
     */
    public static final int FUNC_TELEPORT = 1;
    public static final int FUNC_RESTORE_HP = 2;
    public static final int FUNC_RESTORE_MP = 3;
    public static final int FUNC_RESTORE_EXP = 4;
    public static final int FUNC_SUPPORT = 5;
    protected static final Logger LOGGER = LoggerFactory.getLogger(Castle.class);
    private final List<Door> _doors = new ArrayList<>();
    private final List<Npc> _sideNpcs = new ArrayList<>();
    private final List<Artefact> _artefacts = new ArrayList<>(1);
    private final Map<Integer, CastleFunction> _function = new ConcurrentHashMap<>();
    int _ownerId = 0;
    private Siege _siege = null;
    private Calendar _siegeDate;
    private boolean _isTimeRegistrationOver = true; // true if Castle Lords set the time, or 24h is elapsed after the siege
    private Calendar _siegeTimeRegistrationEndDate; // last siege end date + 1 day
    private CastleSide _castleSide = null;
    private long _treasury = 0;
    private boolean _showNpcCrest = false;
    private L2SiegeZone _zone = null;
    private L2ResidenceTeleportZone _teleZone;
    private L2Clan _formerOwner = null;
    private int _ticketBuyCount = 0;

    public Castle(int castleId) {
        super(castleId);
        load();
        initResidenceZone();
        // initFunctions();
        spawnSideNpcs();
        if (_ownerId != 0) {
            loadFunctions();
            loadDoorUpgrade();
        }
    }

    /**
     * Return function with id
     *
     * @param type
     * @return
     */
    public CastleFunction getCastleFunction(int type) {
        if (_function.containsKey(type)) {
            return _function.get(type);
        }
        return null;
    }

    public synchronized void engrave(L2Clan clan, WorldObject target, CastleSide side) {
        if (!_artefacts.contains(target)) {
            return;
        }
        setSide(side);
        setOwner(clan);
        final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CLAN_S1_HAS_SUCCEEDED_IN_S2);
        msg.addString(clan.getName());
        msg.addString(getName());
        getSiege().announceToPlayer(msg, true);
    }

    /**
     * Add amount to castle instance's treasury (warehouse).
     *
     * @param amount
     */
    public void addToTreasury(long amount) {
        // check if owned
        if (_ownerId <= 0) {
            return;
        }

        switch (getName().toLowerCase()) {
            case "schuttgart":
            case "goddard": {
                final Castle rune = CastleManager.getInstance().getCastle("rune");
                if (rune != null) {
                    final long runeTax = (long) (amount * rune.getTaxRate(TaxType.BUY));
                    if (rune.getOwnerId() > 0) {
                        rune.addToTreasury(runeTax);
                    }
                    amount -= runeTax;
                }
                break;
            }
            case "dion":
            case "giran":
            case "gludio":
            case "innadril":
            case "oren": {
                final Castle aden = CastleManager.getInstance().getCastle("aden");
                if (aden != null) {
                    final long adenTax = (long) (amount * aden.getTaxRate(TaxType.BUY)); // Find out what Aden gets from the current castle instance's income
                    if (aden.getOwnerId() > 0) {
                        aden.addToTreasury(adenTax); // Only bother to really add the tax to the treasury if not npc owned
                    }
                    amount -= adenTax; // Subtract Aden's income from current castle instance's income
                }
                break;
            }
        }
        addToTreasuryNoTax(amount);
    }

    // This method add to the treasury

    /**
     * Add amount to castle instance's treasury (warehouse), no tax paying.
     *
     * @param amount
     * @return
     */
    public boolean addToTreasuryNoTax(long amount) {
        if (_ownerId <= 0) {
            return false;
        }

        if (amount < 0) {
            amount *= -1;
            if (_treasury < amount) {
                return false;
            }
            _treasury -= amount;
        } else if ((_treasury + amount) > Inventory.MAX_ADENA) {
            _treasury = Inventory.MAX_ADENA;
        } else {
            _treasury += amount;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE castle SET treasury = ? WHERE id = ?")) {
            ps.setLong(1, _treasury);
            ps.setInt(2, getResidenceId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return true;
    }

    /**
     * Move non clan members off castle area and to nearest town.
     */
    public void banishForeigners() {
        getResidenceZone().banishForeigners(_ownerId);
    }

    /**
     * Return true if object is inside the zone
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean checkIfInZone(int x, int y, int z) {
        return getZone().isInsideZone(x, y, z);
    }

    public L2SiegeZone getZone() {
        if (_zone == null) {
            for (L2SiegeZone zone : ZoneManager.getInstance().getAllZones(L2SiegeZone.class)) {
                if (zone.getSiegeObjectId() == getResidenceId()) {
                    _zone = zone;
                    break;
                }
            }
        }
        return _zone;
    }

    @Override
    public L2CastleZone getResidenceZone() {
        return (L2CastleZone) super.getResidenceZone();
    }

    public L2ResidenceTeleportZone getTeleZone() {
        if (_teleZone == null) {
            for (L2ResidenceTeleportZone zone : ZoneManager.getInstance().getAllZones(L2ResidenceTeleportZone.class)) {
                if (zone.getResidenceId() == getResidenceId()) {
                    _teleZone = zone;
                    break;
                }
            }
        }
        return _teleZone;
    }

    public void oustAllPlayers() {
        getTeleZone().oustAllPlayers();
    }

    /**
     * Get the objects distance to this castle
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
        if ((activeChar.getClanId() != _ownerId) && !activeChar.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS)) {
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

    public void openCloseDoor(Player activeChar, String doorName, boolean open) {
        if ((activeChar.getClanId() != _ownerId) && !activeChar.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS)) {
            return;
        }

        final Door door = getDoor(doorName);
        if (door != null) {
            if (open) {
                door.openMe();
            } else {
                door.closeMe();
            }
        }
    }

    // This method is used to begin removing all castle upgrades
    public void removeUpgrade() {
        removeDoorUpgrade();
        removeTrapUpgrade();
        for (Integer fc : _function.keySet()) {
            removeFunction(fc);
        }
        _function.clear();
    }

    public void removeOwner(L2Clan clan) {
        if (clan != null) {
            _formerOwner = clan;
            if (Config.REMOVE_CASTLE_CIRCLETS) {
                CastleManager.getInstance().removeCirclet(_formerOwner, getResidenceId());
            }
            for (Player member : clan.getOnlineMembers(0)) {
                removeResidentialSkills(member);
                member.sendSkillList();
            }
            clan.setCastleId(0);
            clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
        }

        setSide(CastleSide.NEUTRAL);
        updateOwnerInDB(null);
        if (getSiege().isInProgress()) {
            getSiege().midVictory();
        }

        for (Integer fc : _function.keySet()) {
            removeFunction(fc);
        }
        _function.clear();
    }

    /**
     * Respawn all doors on castle grounds.
     */
    public void spawnDoor() {
        spawnDoor(false);
    }

    /**
     * Respawn all doors on castle grounds<BR>
     * <BR>
     *
     * @param isDoorWeak
     */
    public void spawnDoor(boolean isDoorWeak) {
        for (Door door : _doors) {
            if (door.isDead()) {
                door.doRevive();
                door.setCurrentHp((isDoorWeak) ? (door.getMaxHp() / 2) : (door.getMaxHp()));
            }

            if (door.isOpen()) {
                door.closeMe();
            }
        }
    }

    // This method loads castle
    @Override
    protected void load() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps1 = con.prepareStatement("SELECT * FROM castle WHERE id = ?");
             PreparedStatement ps2 = con.prepareStatement("SELECT clan_id FROM clan_data WHERE hasCastle = ?")) {
            ps1.setInt(1, getResidenceId());
            try (ResultSet rs = ps1.executeQuery()) {
                while (rs.next()) {
                    setName(rs.getString("name"));
                    // _OwnerId = rs.getInt("ownerId");

                    _siegeDate = Calendar.getInstance();
                    _siegeDate.setTimeInMillis(rs.getLong("siegeDate"));
                    _siegeTimeRegistrationEndDate = Calendar.getInstance();
                    _siegeTimeRegistrationEndDate.setTimeInMillis(rs.getLong("regTimeEnd"));
                    _isTimeRegistrationOver = rs.getBoolean("regTimeOver");

                    _castleSide = Enum.valueOf(CastleSide.class, rs.getString("side"));

                    _treasury = rs.getLong("treasury");

                    _showNpcCrest = rs.getBoolean("showNpcCrest");

                    _ticketBuyCount = rs.getInt("ticketBuyCount");
                }
            }

            ps2.setInt(1, getResidenceId());
            try (ResultSet rs = ps2.executeQuery()) {
                while (rs.next()) {
                    _ownerId = rs.getInt("clan_id");
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Exception: loadCastleData(): " + e.getMessage(), e);
        }
    }

    /**
     * Load All Functions
     */
    private void loadFunctions() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM castle_functions WHERE castle_id = ?")) {
            ps.setInt(1, getResidenceId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    _function.put(rs.getInt("type"), new CastleFunction(rs.getInt("type"), rs.getInt("lvl"), rs.getInt("lease"), 0, rs.getLong("rate"), rs.getLong("endTime"), true));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception: Castle.loadFunctions(): " + e.getMessage(), e);
        }
    }

    /**
     * Remove function In List and in DB
     *
     * @param functionType
     */
    public void removeFunction(int functionType) {
        _function.remove(functionType);
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM castle_functions WHERE castle_id=? AND type=?")) {
            ps.setInt(1, getResidenceId());
            ps.setInt(2, functionType);
            ps.execute();
        } catch (Exception e) {
            LOGGER.error("Exception: Castle.removeFunctions(int functionType): " + e.getMessage(), e);
        }
    }

    public boolean updateFunctions(Player player, int type, int lvl, int lease, long rate, boolean addNew) {
        if (player == null) {
            return false;
        }
        if (lease > 0) {
            if (!player.destroyItemByItemId("Consume", CommonItem.ADENA, lease, null, true)) {
                return false;
            }
        }
        if (addNew) {
            _function.put(type, new CastleFunction(type, lvl, lease, 0, rate, 0, false));
        } else if ((lvl == 0) && (lease == 0)) {
            removeFunction(type);
        } else {
            final int diffLease = lease - _function.get(type).getLease();
            if (diffLease > 0) {
                _function.remove(type);
                _function.put(type, new CastleFunction(type, lvl, lease, 0, rate, -1, false));
            } else {
                _function.get(type).setLease(lease);
                _function.get(type).setLvl(lvl);
                _function.get(type).dbSave();
            }
        }
        return true;
    }

    public void activateInstance() {
        loadDoor();
    }

    // This method loads castle door data from database
    private void loadDoor() {
        for (Door door : DoorData.getInstance().getDoors()) {
            if ((door.getCastle() != null) && (door.getCastle().getResidenceId() == getResidenceId())) {
                _doors.add(door);
            }
        }
    }

    // This method loads castle door upgrade data from database
    private void loadDoorUpgrade() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM castle_doorupgrade WHERE castleId=?")) {
            ps.setInt(1, getResidenceId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    setDoorUpgrade(rs.getInt("doorId"), rs.getInt("ratio"), false);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Exception: loadCastleDoorUpgrade(): " + e.getMessage(), e);
        }
    }

    private void removeDoorUpgrade() {
        for (Door door : _doors) {
            door.getStat().setUpgradeHpRatio(1);
            door.setCurrentHp(door.getCurrentHp());
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM castle_doorupgrade WHERE castleId=?")) {
            ps.setInt(1, getResidenceId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Exception: removeDoorUpgrade(): " + e.getMessage(), e);
        }
    }

    public void setDoorUpgrade(int doorId, int ratio, boolean save) {
        final Door door = (getDoors().isEmpty()) ? DoorData.getInstance().getDoor(doorId) : getDoor(doorId);
        if (door == null) {
            return;
        }

        door.getStat().setUpgradeHpRatio(ratio);
        door.setCurrentHp(door.getMaxHp());

        if (save) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("REPLACE INTO castle_doorupgrade (doorId, ratio, castleId) values (?,?,?)")) {
                ps.setInt(1, doorId);
                ps.setInt(2, ratio);
                ps.setInt(3, getResidenceId());
                ps.execute();
            } catch (Exception e) {
                LOGGER.warn("Exception: setDoorUpgrade(int doorId, int ratio, int castleId): " + e.getMessage(), e);
            }
        }
    }

    private void updateOwnerInDB(L2Clan clan) {
        if (clan != null) {
            _ownerId = clan.getId(); // Update owner id property
        } else {
            _ownerId = 0; // Remove owner
            CastleManorManager.getInstance().resetManorData(getResidenceId());
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            // Need to remove has castle flag from clan_data, should be checked from castle table.
            try (PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET hasCastle = 0 WHERE hasCastle = ?")) {
                ps.setInt(1, getResidenceId());
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET hasCastle = ? WHERE clan_id = ?")) {
                ps.setInt(1, getResidenceId());
                ps.setInt(2, _ownerId);
                ps.execute();
            }

            // Announce to clan members
            if (clan != null) {
                clan.setCastleId(getResidenceId()); // Set has castle flag for new owner
                clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
                clan.broadcastToOnlineMembers(new PlaySound(1, "Siege_Victory", 0, 0, 0, 0, 0));
            }
        } catch (Exception e) {
            LOGGER.warn("Exception: updateOwnerInDB(L2Clan clan): " + e.getMessage(), e);
        }
    }

    public final Door getDoor(int doorId) {
        return _doors.stream().filter(d -> d.getId() == doorId).findFirst().orElse(null);
    }

    public final Door getDoor(String doorName) {
        return _doors.stream().filter(d -> d.getTemplate().getName().equals(doorName)).findFirst().orElse(null);
    }

    public final List<Door> getDoors() {
        return _doors;
    }

    @Override
    public final int getOwnerId() {
        return _ownerId;
    }

    public final L2Clan getOwner() {
        return (_ownerId != 0) ? ClanTable.getInstance().getClan(_ownerId) : null;
    }

    // This method updates the castle tax rate
    public void setOwner(L2Clan clan) {
        // Remove old owner
        if ((_ownerId > 0) && ((clan == null) || (clan.getId() != _ownerId))) {
            final L2Clan oldOwner = ClanTable.getInstance().getClan(getOwnerId()); // Try to find clan instance
            if (oldOwner != null) {
                if (_formerOwner == null) {
                    _formerOwner = oldOwner;
                    if (Config.REMOVE_CASTLE_CIRCLETS) {
                        CastleManager.getInstance().removeCirclet(_formerOwner, getResidenceId());
                    }
                }
                try {
                    final Player oldleader = oldOwner.getLeader().getPlayerInstance();
                    if (oldleader != null) {
                        if (oldleader.getMountType() == MountType.WYVERN) {
                            oldleader.dismount();
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn("Exception in setOwner: " + e.getMessage(), e);
                }
                oldOwner.setCastleId(0); // Unset has castle flag for old owner
                for (Player member : oldOwner.getOnlineMembers(0)) {
                    removeResidentialSkills(member);
                    member.sendSkillList();
                    member.broadcastUserInfo();
                }
            }
        }

        updateOwnerInDB(clan); // Update in database
        setShowNpcCrest(false);

        // if clan have fortress, remove it
        if ((clan != null) && (clan.getFortId() > 0)) {
            FortDataManager.getInstance().getFortByOwner(clan).removeOwner(true);
        }

        if (getSiege().isInProgress()) {
            getSiege().midVictory(); // Mid victory phase of siege
        }

        if (clan != null) {
            for (Player member : clan.getOnlineMembers(0)) {
                giveResidentialSkills(member);
                member.sendSkillList();
            }
        }
    }

    public final Siege getSiege() {
        if (_siege == null) {
            _siege = new Siege(this);
        }
        return _siege;
    }

    public final Calendar getSiegeDate() {
        return _siegeDate;
    }

    public boolean getIsTimeRegistrationOver() {
        return _isTimeRegistrationOver;
    }

    public void setIsTimeRegistrationOver(boolean val) {
        _isTimeRegistrationOver = val;
    }

    public Calendar getTimeRegistrationOverDate() {
        if (_siegeTimeRegistrationEndDate == null) {
            _siegeTimeRegistrationEndDate = Calendar.getInstance();
        }
        return _siegeTimeRegistrationEndDate;
    }

    public final int getTaxPercent(TaxType type) {
        final int taxPercent;
        switch (_castleSide) {
            case LIGHT: {
                taxPercent = type == TaxType.BUY ? Config.CASTLE_BUY_TAX_LIGHT : Config.CASTLE_SELL_TAX_LIGHT;
                break;
            }
            case DARK: {
                taxPercent = type == TaxType.BUY ? Config.CASTLE_BUY_TAX_DARK : Config.CASTLE_SELL_TAX_DARK;
                break;
            }
            default: {
                taxPercent = type == TaxType.BUY ? Config.CASTLE_BUY_TAX_NEUTRAL : Config.CASTLE_SELL_TAX_NEUTRAL;
                break;
            }
        }
        return taxPercent;
    }

    public final double getTaxRate(TaxType taxType) {
        return getTaxPercent(taxType) / 100.0;
    }

    public final long getTreasury() {
        return _treasury;
    }

    public final boolean getShowNpcCrest() {
        return _showNpcCrest;
    }

    public final void setShowNpcCrest(boolean showNpcCrest) {
        if (_showNpcCrest != showNpcCrest) {
            _showNpcCrest = showNpcCrest;
            updateShowNpcCrest();
        }
    }

    public void updateClansReputation() {
        if (_formerOwner != null) {
            if (_formerOwner != ClanTable.getInstance().getClan(getOwnerId())) {
                final int maxreward = Math.max(0, _formerOwner.getReputationScore());
                _formerOwner.takeReputationScore(Config.LOOSE_CASTLE_POINTS, true);
                final L2Clan owner = ClanTable.getInstance().getClan(getOwnerId());
                if (owner != null) {
                    owner.addReputationScore(Math.min(Config.TAKE_CASTLE_POINTS, maxreward), true);
                }
            } else {
                _formerOwner.addReputationScore(Config.CASTLE_DEFENDED_POINTS, true);
            }
        } else {
            final L2Clan owner = ClanTable.getInstance().getClan(getOwnerId());
            if (owner != null) {
                owner.addReputationScore(Config.TAKE_CASTLE_POINTS, true);
            }
        }
    }

    public void updateShowNpcCrest() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE castle SET showNpcCrest = ? WHERE id = ?")) {
            ps.setString(1, String.valueOf(_showNpcCrest));
            ps.setInt(2, getResidenceId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.info("Error saving showNpcCrest for castle " + getName() + ": " + e.getMessage());
        }
    }

    /**
     * Register Artefact to castle
     *
     * @param artefact
     */
    public void registerArtefact(Artefact artefact) {
        _artefacts.add(artefact);
    }

    public List<Artefact> getArtefacts() {
        return _artefacts;
    }

    /**
     * @return the tickets exchanged for this castle
     */
    public int getTicketBuyCount() {
        return _ticketBuyCount;
    }

    /**
     * Set the exchanged tickets count.<br>
     * Performs database update.
     *
     * @param count the ticket count to set
     */
    public void setTicketBuyCount(int count) {
        _ticketBuyCount = count;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE castle SET ticketBuyCount = ? WHERE id = ?")) {
            ps.setInt(1, _ticketBuyCount);
            ps.setInt(2, getResidenceId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public int getTrapUpgradeLevel(int towerIndex) {
        final TowerSpawn spawn = SiegeManager.getInstance().getFlameTowers(getResidenceId()).get(towerIndex);
        return (spawn != null) ? spawn.getUpgradeLevel() : 0;
    }

    public void setTrapUpgrade(int towerIndex, int level, boolean save) {
        if (save) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("REPLACE INTO castle_trapupgrade (castleId, towerIndex, level) values (?,?,?)")) {
                ps.setInt(1, getResidenceId());
                ps.setInt(2, towerIndex);
                ps.setInt(3, level);
                ps.execute();
            } catch (Exception e) {
                LOGGER.warn("Exception: setTrapUpgradeLevel(int towerIndex, int level, int castleId): " + e.getMessage(), e);
            }
        }
        final TowerSpawn spawn = SiegeManager.getInstance().getFlameTowers(getResidenceId()).get(towerIndex);
        if (spawn != null) {
            spawn.setUpgradeLevel(level);
        }
    }

    private void removeTrapUpgrade() {
        for (TowerSpawn ts : SiegeManager.getInstance().getFlameTowers(getResidenceId())) {
            ts.setUpgradeLevel(0);
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM castle_trapupgrade WHERE castleId=?")) {
            ps.setInt(1, getResidenceId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Exception: removeDoorUpgrade(): " + e.getMessage(), e);
        }
    }

    @Override
    protected void initResidenceZone() {
        for (L2CastleZone zone : ZoneManager.getInstance().getAllZones(L2CastleZone.class)) {
            if (zone.getResidenceId() == getResidenceId()) {
                setResidenceZone(zone);
                break;
            }
        }
    }

    @Override
    public void giveResidentialSkills(Player player) {
        super.giveResidentialSkills(player);
        final Skill skill = _castleSide == CastleSide.DARK ? CommonSkill.ABILITY_OF_DARKNESS.getSkill() : CommonSkill.ABILITY_OF_LIGHT.getSkill();
        player.addSkill(skill);
    }

    @Override
    public void removeResidentialSkills(Player player) {
        super.removeResidentialSkills(player);
        player.removeSkill(CommonSkill.ABILITY_OF_DARKNESS.getId());
        player.removeSkill(CommonSkill.ABILITY_OF_LIGHT.getId());
    }

    public void spawnSideNpcs() {
        _sideNpcs.stream().filter(Objects::nonNull).forEach(Npc::deleteMe);
        _sideNpcs.clear();

        for (CastleSpawnHolder holder : getSideSpawns()) {
            if (holder != null) {
                L2Spawn spawn;
                try {
                    spawn = new L2Spawn(holder.getNpcId());
                } catch (Exception e) {
                    LOGGER.warn(Castle.class.getSimpleName() + ": " + e.getMessage());
                    return;
                }
                spawn.setXYZ(holder);
                spawn.setHeading(holder.getHeading());
                final Npc npc = spawn.doSpawn(false);
                spawn.stopRespawn();
                npc.broadcastInfo();
                _sideNpcs.add(npc);
            }
        }
    }

    public List<CastleSpawnHolder> getSideSpawns() {
        return CastleData.getInstance().getSpawnsForSide(getResidenceId(), getSide());
    }

    public CastleSide getSide() {
        return _castleSide;
    }

    public void setSide(CastleSide side) {
        if (_castleSide == side) {
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE castle SET side = ? WHERE id = ?")) {
            ps.setString(1, side.toString());
            ps.setInt(2, getResidenceId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        _castleSide = side;
        Broadcast.toAllOnlinePlayers(new ExCastleState(this));
        spawnSideNpcs();
    }

    public class CastleFunction {
        final int _type;
        final long _rate;
        public boolean _cwh;
        protected int _fee;
        protected int _tempFee;
        protected boolean _inDebt;
        long _endDate;
        private int _lvl;

        public CastleFunction(int type, int lvl, int lease, int tempLease, long rate, long time, boolean cwh) {
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
            if (_ownerId <= 0) {
                return;
            }
            final long currentTime = System.currentTimeMillis();
            if (_endDate > currentTime) {
                ThreadPoolManager.getInstance().schedule(new FunctionTask(cwh), _endDate - currentTime);
            } else {
                ThreadPoolManager.getInstance().schedule(new FunctionTask(cwh), 0);
            }
        }

        public void dbSave() {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("REPLACE INTO castle_functions (castle_id, type, lvl, lease, rate, endTime) VALUES (?,?,?,?,?,?)")) {
                ps.setInt(1, getResidenceId());
                ps.setInt(2, _type);
                ps.setInt(3, _lvl);
                ps.setInt(4, _fee);
                ps.setLong(5, _rate);
                ps.setLong(6, _endDate);
                ps.execute();
            } catch (Exception e) {
                LOGGER.error("Exception: Castle.updateFunctions(int type, int lvl, int lease, long rate, long time, boolean addNew): " + e.getMessage(), e);
            }
        }

        private class FunctionTask implements Runnable {
            public FunctionTask(boolean cwh) {
                _cwh = cwh;
            }

            @Override
            public void run() {
                try {
                    if (_ownerId <= 0) {
                        return;
                    }
                    if ((ClanTable.getInstance().getClan(getOwnerId()).getWarehouse().getAdena() >= _fee) || !_cwh) {
                        int fee = _fee;
                        if (_endDate == -1) {
                            fee = _tempFee;
                        }

                        setEndTime(System.currentTimeMillis() + _rate);
                        dbSave();
                        if (_cwh) {
                            ClanTable.getInstance().getClan(getOwnerId()).getWarehouse().destroyItemByItemId("CS_function_fee", CommonItem.ADENA, fee, null, null);
                        }
                        ThreadPoolManager.schedule(new FunctionTask(true), _rate);
                    } else {
                        removeFunction(_type);
                    }
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
    }
}
