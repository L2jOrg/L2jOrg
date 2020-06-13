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
package org.l2j.gameserver.model.entity;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.CastleDAO;
import org.l2j.gameserver.data.database.dao.ClanDAO;
import org.l2j.gameserver.data.database.data.CastleData;
import org.l2j.gameserver.data.database.data.CastleFunctionData;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.data.xml.impl.CastleDataManager;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Artefact;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.CastleSpawnHolder;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExCastleState;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.CastleZone;
import org.l2j.gameserver.world.zone.type.ResidenceTeleportZone;
import org.l2j.gameserver.world.zone.type.SiegeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.*;
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

    private final CastleData data;

    public Castle(CastleData data){
        super(data.getId());
        setName(data.getName()); // tempfix

        this.data = data;
        load();
        initResidenceZone();
        spawnSideNpcs();
    }

    @Override
    protected void load() {
        ownerId = getDAO(ClanDAO.class).findOwnerClanIdByCastle(getId());
        if (ownerId != 0) {
            loadFunctions();
            loadDoorUpgrade();
        }
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

    public void addToTreasuryNoTax(long amount) {
        if (ownerId <= 0) {
            return;
        }

        if (amount < 0) {
            amount *= -1;
            if (data.getTreasury() < amount) {
                return;
            }
            data.updateTreasury(-amount);
        } else if (data.getTreasury() + amount > Inventory.MAX_ADENA) {
            data.setTreasury(Inventory.MAX_ADENA);
        } else {
            data.updateTreasury(amount);
        }

        getDAO(CastleDAO.class).updateTreasury(getId(), data.getTreasury());
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

    public boolean checkIfInZone(ILocational loc) {
        return getZone().isInsideZone(loc);
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
        getDAO(CastleDAO.class).withDoorUpgradeDo(getId(), this::processDoorUpgrade);
    }

    private void processDoorUpgrade(ResultSet resultSet) {
        try {
            while(resultSet.next()) {
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
            getDAO(CastleDAO.class).saveDoorUpgrade(getId(), doorId, ratio);
        }
    }

    private void removeDoorUpgrade() {
        for (Door door : doors.values()) {
            door.getStats().setUpgradeHpRatio(1);
            door.setCurrentHp(door.getCurrentHp());
        }
        getDAO(CastleDAO.class).deleteDoorUpgradeByCastle(getId());
    }

    private void updateOwnerInDB(Clan clan) {
        if (nonNull(clan)) {
            ownerId = clan.getId(); // Update owner id property
        } else {
            ownerId = 0; // Remove owner
            CastleManorManager.getInstance().resetManorData(getId());
        }

        var clanDao =getDAO(ClanDAO.class);
        clanDao.removeOwnerClanByCastle(getId());

        if(ownerId != 0) {
            clanDao.updateOwnedCastle(ownerId, getId());
        }

        if (nonNull(clan)) {
            clan.setCastleId(getId()); // Set has castle flag for new owner
            clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
            clan.broadcastToOnlineMembers(new PlaySound(1, "Siege_Victory", 0, 0, 0, 0, 0));
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
        if (ownerId > 0 && (isNull(clan) || clan.getId() != ownerId)) {
            var oldOwner = getOwner();
            if (nonNull(oldOwner)) {

                if (isNull(formerOwner)) {
                    formerOwner = oldOwner;
                    if (getSettings(CharacterSettings.class).removeCastleCirclets()) {
                        CastleManager.getInstance().removeCirclet(formerOwner, getId());
                    }
                }

                doIfNonNull(oldOwner.getLeader().getPlayerInstance(), oldleader -> {
                    if(oldleader.getMountType() == MountType.WYVERN) {
                        oldleader.dismount();
                    }
                });

                oldOwner.setCastleId(0); // Unset has castle flag for old owner
                oldOwner.forEachOnlineMember(m -> {
                    removeResidentialSkills(m);
                    m.sendSkillList();
                    m.broadcastUserInfo();
                });
            }
        }

        updateOwnerInDB(clan); // Update in database
        setShowNpcCrest(false);

        if (getSiege().isInProgress()) {
            getSiege().midVictory(); // Mid victory phase of siege
        }

        if (nonNull(clan)) {
            clan.forEachOnlineMember(m -> {
                giveResidentialSkills(m);
                m.sendSkillList();
            });
        }
    }

    public final Siege getSiege() {
        if (isNull(siege)) {
            siege = new Siege(this);
        }
        return siege;
    }

    public final LocalDateTime getSiegeDate() {
        return data.getSiegeDate();
    }

    public boolean isSiegeTimeRegistrationSeason() {
        return Duration.between(LocalDateTime.now(), data.getSiegeDate()).toDays() > 0;
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
        var owner = getOwner();
        if (nonNull(formerOwner)) {
            if (formerOwner != owner) {
                var maxReward = Math.max(0, formerOwner.getReputationScore());
                formerOwner.takeReputationScore(Config.LOOSE_CASTLE_POINTS, true);

                if (nonNull(owner)) {
                    owner.addReputationScore(Math.min(Config.TAKE_CASTLE_POINTS, maxReward), true);
                }
            } else {
                formerOwner.addReputationScore(Config.CASTLE_DEFENDED_POINTS, true);
            }
        } else if (nonNull(owner)) {
            owner.addReputationScore(Config.TAKE_CASTLE_POINTS, true);
        }
    }

    public void updateShowNpcCrest() {
        getDAO(CastleDAO.class).updateShowNpcCrest(getId(), data.isShowNpcCrest());
    }

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
        return zeroIfNullOrElse(SiegeManager.getInstance().getFlameTowers(getId()).get(towerIndex), TowerSpawn::getUpgradeLevel);
    }

    public void setTrapUpgrade(int towerIndex, int level, boolean save) {
        if (save) {
            getDAO(CastleDAO.class).saveTrapUpgrade(getId(), towerIndex, level);
        }

        doIfNonNull(SiegeManager.getInstance().getFlameTowers(getId()).get(towerIndex), spawn -> spawn.setUpgradeLevel(level));
    }

    private void removeTrapUpgrade() {
        SiegeManager.getInstance().getFlameTowers(getId()).forEach(tower -> tower.setUpgradeLevel(0));
        getDAO(CastleDAO.class).deleteTrapUpgradeByCastle(getId());
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
        return CastleDataManager.getInstance().getSpawnsForSide(getId(), getSide());
    }

    public CastleSide getSide() {
        return data.getSide();
    }

    public void setSide(CastleSide side) {
        if (getSide() == side) {
            return;
        }

        data.setSide(side);
        getDAO(CastleDAO.class).updateSide(getId(), side);
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

        private final CastleFunctionData functionData;
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
            getDAO(CastleDAO.class).save(functionData);
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
                    var owner = getOwner();

                    if ( nonNull(owner) && (owner.getWarehouse().getAdena() >= functionData.getLease() || !cwh)) {

                        int fee = functionData.getLease();
                        if (functionData.getEndTime() == -1) {
                            fee = tempFee;
                        }

                        functionData.setEndTime(System.currentTimeMillis() + functionData.getRate());
                        dbSave();
                        if (cwh) {
                            owner.getWarehouse().destroyItemByItemId("CS_function_fee", CommonItem.ADENA, fee, null, null);
                        }
                        ThreadPool.schedule(new FunctionTask(true), functionData.getRate());
                    } else {
                        removeFunction(functionData.getType());
                    }
                } catch (Exception e) {
                    Castle.LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }
}
