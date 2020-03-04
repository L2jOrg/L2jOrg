package org.l2j.gameserver.model.entity;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.CastleDAO;
import org.l2j.gameserver.data.database.dao.ClanDAO;
import org.l2j.gameserver.data.database.data.CastleFunctionData;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.data.xml.impl.CastleData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Artefact;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.CastleSpawnHolder;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExCastleState;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.CastleZone;
import org.l2j.gameserver.world.zone.type.ResidenceTeleportZone;
import org.l2j.gameserver.world.zone.type.SiegeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.falseIfNullOrElse;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public final class Castle extends AbstractResidence {

    public static final int FUNC_TELEPORT = 1;
    public static final int FUNC_RESTORE_HP = 2;
    public static final int FUNC_RESTORE_MP = 3;
    public static final int FUNC_RESTORE_EXP = 4;
    public static final int FUNC_SUPPORT = 5;

    protected static final Logger LOGGER = LoggerFactory.getLogger(Castle.class);

    private final IntMap<Door> doors = new HashIntMap<>();
    private final List<Npc> sideNpcs = new ArrayList<>();
    private final List<Artefact> artefacts = new ArrayList<>(1);
    private final IntMap<CastleFunction> functions = new CHashIntMap<>();

    int ownerId = 0;
    private Siege siege = null;
    private SiegeZone zone = null;
    private ResidenceTeleportZone teleZone;
    private Clan formerOwner = null;

    private org.l2j.gameserver.data.database.data.CastleData data;

    public Castle(int castleId) {
        super(castleId);
        load();
        initResidenceZone();
        spawnSideNpcs();
        if (ownerId != 0) {
            loadFunctions();
            loadDoorUpgrade();
        }
    }

    @Override
    protected void load() {
        data = getDAO(CastleDAO.class).findById(getId());
        setName(data.getName());
        ownerId = getDAO(ClanDAO.class).findClanIdByCastle(getId());
    }

    @Override
    protected void initResidenceZone() {
        for (CastleZone zone : ZoneManager.getInstance().getAllZones(CastleZone.class)) {
            if (zone.getResidenceId() == getId()) {
                setResidenceZone(zone);
                break;
            }
        }
    }

    private void spawnSideNpcs() {
        sideNpcs.stream().filter(Objects::nonNull).forEach(Npc::deleteMe);
        sideNpcs.clear();

        for (CastleSpawnHolder holder : getSideSpawns()) {
            try {
                var spawn = new Spawn(holder.getNpcId());
                spawn.setXYZ(holder);
                spawn.setHeading(holder.getHeading());
                final Npc npc = spawn.doSpawn(false);
                spawn.stopRespawn();
                npc.broadcastInfo();
                sideNpcs.add(npc);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

    private void loadFunctions() {
        getDAO(CastleDAO.class).findFunctionsByCastle(getId()).forEach(functionData -> functions.put(functionData.getType(), new CastleFunction(functionData, 0, true)));
    }

    public CastleFunction getCastleFunction(int type) {
        if (functions.containsKey(type)) {
            return functions.get(type);
        }
        return null;
    }

    public synchronized void engrave(Clan clan, WorldObject target, CastleSide side) {
        if (!(target instanceof Artefact) || !artefacts.contains(target)) {
            return;
        }
        setSide(side);
        setOwner(clan);
        getSiege().announceToPlayer(getSystemMessage(SystemMessageId.CLAN_S1_HAS_SUCCEEDED_IN_S2).addString(clan.getName()).addString(getName()), true);
    }

    /**
     * Add amount to castle instance's treasury (warehouse).
     *
     */
    public void addToTreasury(long amount) {
        // check if owned
        if (ownerId <= 0) {
            return;
        }

        switch (getName().toLowerCase()) {
            case "schuttgart", "goddard" -> {
                final Castle rune = CastleManager.getInstance().getCastle("rune");
                if (nonNull(rune)) {
                    final long runeTax = (long) (amount * rune.getTaxRate(TaxType.BUY));
                    if (rune.getOwnerId() > 0) {
                        rune.addToTreasury(runeTax);
                    }
                    amount -= runeTax;
                }
            }
            case "dion", "giran", "gludio", "innadril", "oren" -> {
                final Castle aden = CastleManager.getInstance().getCastle("aden");
                if (aden != null) {
                    final long adenTax = (long) (amount * aden.getTaxRate(TaxType.BUY)); // Find out what Aden gets from the current castle instance's income
                    if (aden.getOwnerId() > 0) {
                        aden.addToTreasury(adenTax); // Only bother to really add the tax to the treasury if not npc owned
                    }
                    amount -= adenTax; // Subtract Aden's income from current castle instance's income
                }
            }
        }
        addToTreasuryNoTax(amount);
    }

    public boolean addToTreasuryNoTax(long amount) {
        if (ownerId <= 0) {
            return false;
        }

        if (amount < 0) {
            amount *= -1;
            if (data.getTreasury() < amount) {
                return false;
            }
            data.updateTreasury(-amount);
        } else if (data.getTreasury() + amount > Inventory.MAX_ADENA) {
            data.setTreasury(Inventory.MAX_ADENA);
        } else {
            data.updateTreasury(amount);
        }

        getDAO(CastleDAO.class).updateTreasury(getId(), data.getTreasury());
        return true;
    }

    /**
     * Move non clan members off castle area and to nearest town.
     */
    public void banishForeigners() {
        getResidenceZone().banishForeigners(ownerId);
    }

    public boolean checkIfInZone(int x, int y, int z) {
        return getZone().isInsideZone(x, y, z);
    }

    public SiegeZone getZone() {
        if (isNull(zone)) {
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
    public CastleZone getResidenceZone() {
        return (CastleZone) super.getResidenceZone();
    }

    public ResidenceTeleportZone getTeleZone() {
        if (isNull(teleZone)) {
            for (ResidenceTeleportZone zone : ZoneManager.getInstance().getAllZones(ResidenceTeleportZone.class)) {
                if (zone.getResidenceId() == getId()) {
                    teleZone = zone;
                    break;
                }
            }
        }
        return teleZone;
    }

    public void oustAllPlayers() {
        getTeleZone().oustAllPlayers();
    }

    public double getDistance(WorldObject obj) {
        return getZone().getDistanceToZone(obj);
    }

    private void openCloseDoor(Player player, Door door, boolean open) {
        if (isNull(door) || (player.getClanId() != ownerId && !player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS))) {
            return;
        }
        if(open) {
            door.openMe();
        } else {
            door.closeMe();
        }
    }

    public void openCloseDoor(Player player, int doorId, boolean open) {
        openCloseDoor(player, getDoor(doorId), open);

    }

    public void openCloseDoor(Player activeChar, String doorName, boolean open) {
        openCloseDoor(activeChar, getDoor(doorName), open);
    }

    // This method is used to begin removing all castle upgrades
    public void removeUpgrade() {
        removeDoorUpgrade();
        removeTrapUpgrade();
        functions.keySet().forEach(this::removeFunction);
        functions.clear();
    }

    public void removeOwner(Clan clan) {
        if (nonNull(clan)) {
            formerOwner = clan;
            if (getSettings(CharacterSettings.class).removeCastleCirclets()) {
                CastleManager.getInstance().removeCirclet(formerOwner, getId());
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
        functions.keySet().forEach(this::removeFunction);
        functions.clear();
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
     */
    public void spawnDoor(boolean isDoorWeak) {
        for (Door door : doors.values()) {
            if (door.isDead()) {
                door.doRevive();
                door.setCurrentHp((isDoorWeak) ? door.getMaxHp() / 2f : door.getMaxHp());
            }

            if (door.isOpen()) {
                door.closeMe();
            }
        }
    }

    public void removeFunction(int functionType) {
        functions.remove(functionType);
        getDAO(CastleDAO.class).deleteFunction(getId(), functionType);
    }

    public boolean updateFunctions(Player player, int type, int level, int lease, long rate, boolean addNew) {
        if (isNull(player)) {
            return false;
        }
        if (lease > 0) {
            if (!player.destroyItemByItemId("Consume", CommonItem.ADENA, lease, null, true)) {
                return false;
            }
        }
        if (addNew) {
            functions.put(type, new CastleFunction(type, level, lease, 0, rate, 0, false));
        } else if (level == 0 && lease == 0) {
            removeFunction(type);
        } else {
            final int diffLease = lease - functions.get(type).getLease();
            if (diffLease > 0) {
                functions.remove(type);
                functions.put(type, new CastleFunction(type, level, lease, 0, rate, -1, false));
            } else {
                functions.get(type).setLease(lease);
                functions.get(type).setLevel(level);
                functions.get(type).dbSave();
            }
        }
        return true;
    }

    public void activateInstance() {
        loadDoor();
    }

    private void loadDoor() {
        DoorDataManager.getInstance().getDoors().stream().filter(d -> falseIfNullOrElse(d.getCastle(), c -> c.getId() == getId())).forEach(d -> doors.put(d.getId(), d));
    }

    private void loadDoorUpgrade() {
        getDAO(CastleDAO.class).findDoorUpgrade(getId(), this::processDoorUpgrade);
    }

    private void processDoorUpgrade(ResultSet resultSet) {
        try {
            while(!resultSet.next()) {
                setDoorUpgrade(resultSet.getInt(1), resultSet.getInt(2), false);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void setDoorUpgrade(int doorId, int ratio, boolean save) {
        final Door door = doors.isEmpty() ? DoorDataManager.getInstance().getDoor(doorId) : getDoor(doorId);
        if (isNull(door)) {
            return;
        }

        door.getStats().setUpgradeHpRatio(ratio);
        door.setCurrentHp(door.getMaxHp());

        if (save) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("REPLACE INTO castle_doorupgrade (doorId, ratio, castleId) values (?,?,?)")) {
                ps.setInt(1, doorId);
                ps.setInt(2, ratio);
                ps.setInt(3, getId());
                ps.execute();
            } catch (Exception e) {
                LOGGER.warn("Exception: setDoorUpgrade(int doorId, int ratio, int castleId): " + e.getMessage(), e);
            }
        }
    }

    private void removeDoorUpgrade() {
        for (Door door : doors.values()) {
            door.getStats().setUpgradeHpRatio(1);
            door.setCurrentHp(door.getCurrentHp());
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM castle_doorupgrade WHERE castleId=?")) {
            ps.setInt(1, getId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Exception: removeDoorUpgrade(): " + e.getMessage(), e);
        }
    }

    private void updateOwnerInDB(Clan clan) {
        if (clan != null) {
            ownerId = clan.getId(); // Update owner id property
        } else {
            ownerId = 0; // Remove owner
            CastleManorManager.getInstance().resetManorData(getId());
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            // Need to remove has castle flag from clan_data, should be checked from castle table.
            try (PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET hasCastle = 0 WHERE hasCastle = ?")) {
                ps.setInt(1, getId());
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET hasCastle = ? WHERE clan_id = ?")) {
                ps.setInt(1, getId());
                ps.setInt(2, ownerId);
                ps.execute();
            }

            // Announce to clan members
            if (clan != null) {
                clan.setCastleId(getId()); // Set has castle flag for new owner
                clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
                clan.broadcastToOnlineMembers(new PlaySound(1, "Siege_Victory", 0, 0, 0, 0, 0));
            }
        } catch (Exception e) {
            LOGGER.warn("Exception: updateOwnerInDB(Clan clan): " + e.getMessage(), e);
        }
    }

    public final Door getDoor(int doorId) {
        return doors.get(doorId);
    }

    public final Door getDoor(String doorName) {
        return doors.values().stream().filter(d -> d.getTemplate().getName().equals(doorName)).findFirst().orElse(null);
    }

    public final Collection<Door> getDoors() {
        return doors.values();
    }

    @Override
    public final int getOwnerId() {
        return ownerId;
    }

    public final Clan getOwner() {
        return (ownerId != 0) ? ClanTable.getInstance().getClan(ownerId) : null;
    }

    // This method updates the castle tax rate
    public void setOwner(Clan clan) {
        // Remove old owner
        if ((ownerId > 0) && ((clan == null) || (clan.getId() != ownerId))) {
            final Clan oldOwner = ClanTable.getInstance().getClan(getOwnerId()); // Try to find clan instance
            if (oldOwner != null) {
                if (formerOwner == null) {
                    formerOwner = oldOwner;
                    if (getSettings(CharacterSettings.class).removeCastleCirclets()) {
                        CastleManager.getInstance().removeCirclet(formerOwner, getId());
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
        if (siege == null) {
            siege = new Siege(this);
        }
        return siege;
    }

    public final LocalDateTime getSiegeDate() {
        return data.getSiegeDate();
    }

    public boolean isTimeRegistrationOver() {
        return data.getSiegeTimeRegistrationEnd().isBefore(LocalDateTime.now());
    }

    public LocalDateTime getSiegeTimeRegistrationEnd() {
        return data.getSiegeTimeRegistrationEnd();
    }

    public void setSiegeTimeRegistrationEnd(LocalDateTime date) {
        data.setSiegeTimeRegistrationEnd(date);
    }

    public final int getTaxPercent(TaxType type) {
        return switch (data.getSide()) {
            case LIGHT -> type == TaxType.BUY ? Config.CASTLE_BUY_TAX_LIGHT : Config.CASTLE_SELL_TAX_LIGHT;
            case DARK -> type == TaxType.BUY ? Config.CASTLE_BUY_TAX_DARK : Config.CASTLE_SELL_TAX_DARK;
            default -> type == TaxType.BUY ? Config.CASTLE_BUY_TAX_NEUTRAL : Config.CASTLE_SELL_TAX_NEUTRAL;
        };
    }

    public final double getTaxRate(TaxType taxType) {
        return getTaxPercent(taxType) / 100.0;
    }

    public final long getTreasury() {
        return data.getTreasury();
    }

    public final boolean isShowNpcCrest() {
        return data.isShowNpcCrest();
    }

    public final void setShowNpcCrest(boolean showNpcCrest) {
        if (data.isShowNpcCrest() != showNpcCrest) {
            data.setShowNpcCrest(showNpcCrest);
            updateShowNpcCrest();
        }
    }

    public void updateClansReputation() {
        if (formerOwner != null) {
            if (formerOwner != ClanTable.getInstance().getClan(getOwnerId())) {
                final int maxreward = Math.max(0, formerOwner.getReputationScore());
                formerOwner.takeReputationScore(Config.LOOSE_CASTLE_POINTS, true);
                final Clan owner = ClanTable.getInstance().getClan(getOwnerId());
                if (owner != null) {
                    owner.addReputationScore(Math.min(Config.TAKE_CASTLE_POINTS, maxreward), true);
                }
            } else {
                formerOwner.addReputationScore(Config.CASTLE_DEFENDED_POINTS, true);
            }
        } else {
            final Clan owner = ClanTable.getInstance().getClan(getOwnerId());
            if (owner != null) {
                owner.addReputationScore(Config.TAKE_CASTLE_POINTS, true);
            }
        }
    }

    public void updateShowNpcCrest() {
        getDAO(CastleDAO.class).updateShowNpcCrest(getId(), data.isShowNpcCrest());
    }

    /**
     * Register Artefact to castle
     *
     * @param artefact
     */
    public void registerArtefact(Artefact artefact) {
        artefacts.add(artefact);
    }

    public List<Artefact> getArtefacts() {
        return artefacts;
    }

    /**
     * Set the exchanged tickets count.<br>
     * Performs database update.
     *
     * @param count the ticket count to set
     */
    public void setTicketBuyCount(int count) {
        data.setTicketBuyCount(count);
        getDAO(CastleDAO.class).updateTicketBuyCount(getId(), data.getTicketBuyCount());
    }

    public int getTrapUpgradeLevel(int towerIndex) {
        final TowerSpawn spawn = SiegeManager.getInstance().getFlameTowers(getId()).get(towerIndex);
        return (spawn != null) ? spawn.getUpgradeLevel() : 0;
    }

    public void setTrapUpgrade(int towerIndex, int level, boolean save) {
        if (save) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("REPLACE INTO castle_trapupgrade (castleId, towerIndex, level) values (?,?,?)")) {
                ps.setInt(1, getId());
                ps.setInt(2, towerIndex);
                ps.setInt(3, level);
                ps.execute();
            } catch (Exception e) {
                LOGGER.warn("Exception: setTrapUpgradeLevel(int towerIndex, int level, int castleId): " + e.getMessage(), e);
            }
        }
        final TowerSpawn spawn = SiegeManager.getInstance().getFlameTowers(getId()).get(towerIndex);
        if (spawn != null) {
            spawn.setUpgradeLevel(level);
        }
    }

    private void removeTrapUpgrade() {
        for (TowerSpawn ts : SiegeManager.getInstance().getFlameTowers(getId())) {
            ts.setUpgradeLevel(0);
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM castle_trapupgrade WHERE castleId=?")) {
            ps.setInt(1, getId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Exception: removeDoorUpgrade(): " + e.getMessage(), e);
        }
    }

    @Override
    public void giveResidentialSkills(Player player) {
        super.giveResidentialSkills(player);
        final Skill skill = data.getSide() == CastleSide.DARK ? CommonSkill.ABILITY_OF_DARKNESS.getSkill() : CommonSkill.ABILITY_OF_LIGHT.getSkill();
        player.addSkill(skill);
    }

    @Override
    public void removeResidentialSkills(Player player) {
        super.removeResidentialSkills(player);
        player.removeSkill(CommonSkill.ABILITY_OF_DARKNESS.getId());
        player.removeSkill(CommonSkill.ABILITY_OF_LIGHT.getId());
    }

    public List<CastleSpawnHolder> getSideSpawns() {
        return CastleData.getInstance().getSpawnsForSide(getId(), getSide());
    }

    public CastleSide getSide() {
        return data.getSide();
    }

    public void setSide(CastleSide side) {
        if (getSide() == side) {
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE castle SET side = ? WHERE id = ?")) {
            ps.setString(1, side.toString());
            ps.setInt(2, getId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        data.setSide(side);
        Broadcast.toAllOnlinePlayers(new ExCastleState(this));
        spawnSideNpcs();
    }

    public boolean isInSiege() {
        return getSiege().isInProgress();
    }

    public void updateSiegeDate() {
        getDAO(CastleDAO.class).save(data);
    }

    public void setSiegeDate(LocalDateTime siegeDate) {
        data.setSiegeDate(siegeDate);
        updateSiegeDate();
    }

    public class CastleFunction {

        private CastleFunctionData functionData;
        public boolean cwh;
        private final int tempFee;


        CastleFunction(CastleFunctionData data, int tempLease, boolean cwh) {
            this.functionData = data;
            this.tempFee = tempLease;
            initializeTask(cwh);
        }

        public CastleFunction(int type, int level, int lease, int tempLease, long rate, int time, boolean cwh) {
            this.functionData = new CastleFunctionData(type, level, lease, rate, time);
            this.cwh = cwh;
            this.tempFee = tempLease;
        }

        private void initializeTask(boolean cwh) {
            if (ownerId <= 0) {
                return;
            }
            final long currentTime = System.currentTimeMillis();
            if (functionData.getEndTime() > currentTime) {
                ThreadPool.schedule(new FunctionTask(cwh), functionData.getEndTime() - currentTime);
            } else {
                ThreadPool.schedule(new FunctionTask(cwh), 0);
            }
        }

        public void dbSave() {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("REPLACE INTO castle_functions (castle_id, type, lvl, lease, rate, endTime) VALUES (?,?,?,?,?,?)")) {
                ps.setInt(1, getId());
                ps.setInt(2, functionData.getType());
                ps.setInt(3, functionData.getLevel());
                ps.setInt(4, functionData.getLease());
                ps.setLong(5, functionData.getRate());
                ps.setLong(6, functionData.getEndTime());
                ps.execute();
            } catch (Exception e) {
                Castle.LOGGER.error("Exception: Castle.updateFunctions(int type, int lvl, int lease, long rate, long time, boolean addNew): " + e.getMessage(), e);
            }
        }

        public int getLease() {
            return functionData.getLease();
        }

        public void setLease(int lease) {
            functionData.setLease(lease);
        }

        public void setLevel(int level) {
            functionData.setLevel(level);
        }

        public int getLevel() {
            return functionData.getLevel();
        }

        public long getEndTime() {
            return functionData.getEndTime();
        }

        public long getRate() {
            return functionData.getRate();
        }

        private class FunctionTask implements Runnable {
            public FunctionTask(boolean cwh) {
                CastleFunction.this.cwh = cwh;
            }

            @Override
            public void run() {
                try {
                    if (ownerId <= 0) {
                        return;
                    }
                    if ((ClanTable.getInstance().getClan(getOwnerId()).getWarehouse().getAdena() >= functionData.getLease()) || !cwh) {
                        int fee = functionData.getLease();
                        if (functionData.getEndTime() == -1) {
                            fee = tempFee;
                        }

                        functionData.setEndTime(System.currentTimeMillis() + functionData.getRate());
                        dbSave();
                        if (cwh) {
                            ClanTable.getInstance().getClan(getOwnerId()).getWarehouse().destroyItemByItemId("CS_function_fee", CommonItem.ADENA, fee, null, null);
                        }
                        ThreadPool.schedule(new FunctionTask(true), functionData.getRate());
                    } else {
                        removeFunction(functionData.getType());
                    }
                } catch (Exception e) {
                    Castle.LOGGER.error("", e);
                }
            }
        }
    }
}
