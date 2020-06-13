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
import io.github.joealisson.primitive.IntMap;
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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.computeIfNonNull;
import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public class Siege implements Siegable {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Siege.class);

    private final IntMap<SiegeClanData> attackers = new CHashIntMap<>();
    private final IntMap<SiegeClanData> defenders = new CHashIntMap<>();
    private final IntMap<SiegeClanData> defendersWaiting = new CHashIntMap<>();
    private final List<ControlTower> controlTowers = new ArrayList<>();
    private final List<FlameTower> flameTowers = new ArrayList<>();
    private final Castle castle;

    private Instant endTime;
    private int controlTowerCount;

    protected boolean isRegistrationOver = false;
    protected ScheduledFuture<?> scheduledStartSiegeTask = null;
    protected int firstOwnerClanId = -1;
    boolean isInProgress = false;

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
        if (isNull(castle.getSiegeDate()) || LocalDateTime.now().isAfter(castle.getSiegeDate())) {
            setNextSiegeDate();
            saveSiegeDate();
        }
    }

    private void saveSiegeDate() {
        if (nonNull(scheduledStartSiegeTask)) {
            scheduledStartSiegeTask.cancel(true);
            scheduledStartSiegeTask = ThreadPool.schedule(new ScheduleStartSiegeTask(castle), 1000);
        }

        castle.updateSiegeDate();
    }

    private void setNextSiegeDate() {
        var siegeDate = LocalDateTime.now().plusWeeks(2).withMinute(0).withSecond(0);

        var castleManager = CastleManager.getInstance();

        for (SiegeScheduleDate holder : SiegeScheduleData.getInstance().getScheduleDates()) {
            siegeDate = siegeDate.with(TemporalAdjusters.next(holder.getDay())).withHour(holder.getHour());

            if (castleManager.getSiegesOnDate(siegeDate) < holder.getMaxConcurrent()) {
                castleManager.registerSiegeDate(castle, siegeDate);
                break;
            }
        }

        Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME).addCastleId(castle.getId()));
        isRegistrationOver = false;
    }

    private void loadSiegeClan() {
        attackers.clear();
        defenders.clear();
        defendersWaiting.clear();

        if (castle.getOwnerId() > 0) {
            addOwnerDefender(castle.getOwnerId());
        }

        getDAO(CastleDAO.class).findSiegeClansByCastle(castle.getId()).forEach(siegeClan -> {
            switch (siegeClan.getType()) {
                case DEFENDER -> addDefender(siegeClan);
                case ATTACKER -> addAttacker(siegeClan);
                case DEFENDER_PENDING -> addDefenderWaiting(siegeClan);
            }
        });
    }

    private void addOwnerDefender(int clanId) {
        defenders.put(clanId, new SiegeClanData(clanId, SiegeClanType.OWNER, castle.getId()));
    }

    private void addAttacker(SiegeClanData siegeClan) {
        siegeClan.setType(SiegeClanType.ATTACKER);
        getAttackerClans().put(siegeClan.getClanId(), siegeClan);
    }

    private void addDefender(SiegeClanData siegeClan) {
        defenders.put(siegeClan.getClanId(), siegeClan);
    }

    private void addDefenderWaiting(SiegeClanData siegeClan) {
        defendersWaiting.put(siegeClan.getClanId(), siegeClan);
    }

    @Override
    public void startSiege() {
        if (!isInProgress) {
            firstOwnerClanId = castle.getOwnerId();

            if (attackers.isEmpty()) {
                SystemMessage sm;
                if (firstOwnerClanId <= 0) {
                    sm = getSystemMessage(SystemMessageId.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST);
                } else {
                    ClanTable.getInstance().getClan(firstOwnerClanId).increaseBloodAllianceCount();
                    sm = getSystemMessage(SystemMessageId.S1_S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED);
                }
                sm.addCastleId(castle.getId());
                Broadcast.toAllOnlinePlayers(sm);
                saveCastleSiege();
                return;
            }

            isInProgress = true;

            loadSiegeClan();
            updatePlayerSiegeStateFlags(false);
            teleportPlayer(SiegeTeleportWhoType.NotOwner, TeleportWhereType.TOWN);

            controlTowerCount = 0;
            spawnControlTower();
            spawnFlameTower();

            castle.spawnDoor();
            spawnSiegeGuard();

            SiegeGuardManager.getInstance().deleteTickets(getCastle().getId());

            var zone = castle.getZone();
            zone.setSiegeInstance(this);
            zone.setIsActive(true);
            zone.updateZoneStatusForCharactersInside();

            Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.THE_S1_SIEGE_HAS_STARTED).addCastleId(castle.getId()), new PlaySound("systemmsg_eu.17"));
            EventDispatcher.getInstance().notifyEventAsync(new OnCastleSiegeStart(this), getCastle());

            endTime = Instant.now().plus(SiegeManager.getInstance().getSiegeLength(), ChronoUnit.MINUTES);
            ThreadPool.schedule(new ScheduleEndSiegeTask(), 1000);
        }
    }

    @Override
    public final IntMap<SiegeClanData> getAttackerClans() {
        return attackers;
    }

    private void saveCastleSiege() {
        setNextSiegeDate();
        castle.setSiegeTimeRegistrationEnd(LocalDateTime.now().plusDays(1));
        saveSiegeDate();
        startAutoTask();
    }

    private void updatePlayerSiegeStateFlags(boolean clear) {
        attackers.values().stream().map(siegeClan -> ClanTable.getInstance().getClan(siegeClan.getClanId())).forEach(clan -> updateClanMemberSiegeState(clear, clan, (byte) 1));
        attackers.values().stream().map(siegeClan -> ClanTable.getInstance().getClan(siegeClan.getClanId())).forEach(clan -> updateClanMemberSiegeState(clear, clan, (byte) 2));
    }

    private void updateClanMemberSiegeState(boolean clear, Clan clan, byte state) {
        clan.forEachOnlineMember(member -> {
            if (clear) {
                member.setSiegeState((byte) 0);
                member.setSiegeSide(0);
                member.setIsInSiege(false);
                member.stopFameTask();
            } else {
                member.setSiegeState(state);
                member.setSiegeSide(castle.getId());
                if (checkIfInZone(member)) {
                    member.setIsInSiege(true);
                    member.startFameTask(Config.CASTLE_ZONE_FAME_TASK_FREQUENCY * 1000, Config.CASTLE_ZONE_FAME_AQUIRE_POINTS);
                }
            }
            broadcastMemberInfo(member);
        });
    }

    private void broadcastMemberInfo(Player member) {
        member.sendPacket(new UserInfo(member));

        World.getInstance().forEachVisibleObject(member, Player.class, player -> {
            if (!member.isVisibleFor(player)) {
                return;
            }

            final int relation = member.getRelation(player);
            final Integer oldRelation = member.getKnownRelations().get(player.getObjectId());

            if (isNull(oldRelation) || oldRelation != relation) {
                final RelationChanged rc = new RelationChanged();
                rc.addRelation(member, relation, member.isAutoAttackable(player));

                if (member.hasSummon()) {

                    doIfNonNull(member.getPet(), pet -> rc.addRelation(pet, relation, member.isAutoAttackable(player)));

                    if (member.hasServitors()) {
                        member.getServitors().values().forEach(s -> rc.addRelation(s, relation, member.isAutoAttackable(player)));
                    }
                }
                player.sendPacket(rc);
                member.getKnownRelations().put(player.getObjectId(), relation);
            }
        });
    }

    private void teleportPlayer(SiegeTeleportWhoType teleportWho, TeleportWhereType teleportWhere) {
        switch (teleportWho) {
            case Owner -> teleportOnwersInZone(teleportWhere);
            case NotOwner -> teleportNotOwnerInZone(teleportWhere);
            case Attacker -> teleportAttackersInZone(teleportWhere);
            case Spectator -> teleportSpectatorsInZone(teleportWhere);
        }
    }

    private void spawnControlTower() {
        try {
            for (TowerSpawn ts : SiegeManager.getInstance().getControlTowers(castle.getId())) {
                final Spawn spawn = new Spawn(ts.getId());
                spawn.setLocation(ts.getLocation());
                controlTowers.add((ControlTower) spawn.doSpawn());
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot spawn control tower!", e);
        }
        controlTowerCount = controlTowers.size();
    }

    private void spawnFlameTower() {
        try {
            for (TowerSpawn ts : SiegeManager.getInstance().getFlameTowers(castle.getId())) {
                final Spawn spawn = new Spawn(ts.getId());
                spawn.setLocation(ts.getLocation());

                final FlameTower tower = (FlameTower) spawn.doSpawn();
                tower.setUpgradeLevel(ts.getUpgradeLevel());
                tower.setZoneList(ts.getZoneList());
                flameTowers.add(tower);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot spawn flame tower!", e);
        }
    }

    private void spawnSiegeGuard() {
        var siegeGuardManager = SiegeGuardManager.getInstance();
        siegeGuardManager.spawnSiegeGuard(castle);
        var spawns = siegeGuardManager.getSpawnedGuards(castle.getId());

        if (!spawns.isEmpty()) {
            registerToClosestTower(spawns);
        }
    }

    private void registerToClosestTower(Set<Spawn> spawns) {
        for (var spawn : spawns) {
            doIfNonNull( controlTowers.stream().min(Comparator.comparingDouble(ct -> MathUtil.calculateDistanceSq3D(ct, spawn)) ).orElse(null), ct -> ct.registerGuard(spawn));
        }
    }

    public void midVictory() {
        if (isInProgress) {

            if (castle.getOwnerId() > 0) {
                SiegeGuardManager.getInstance().removeSiegeGuards(castle);
            }
             // castle owned by npcs
            if (defenders.isEmpty() && attackers.size() == 1) {

                doIfNonNull(getAttackerClan(castle.getOwnerId()), newOwner -> {
                    removeAttacker(newOwner);
                    addDefender(newOwner, SiegeClanType.OWNER);
                });
                endSiege();
                return;
            }

            if (castle.getOwnerId() > 0) {

                final int allyId = ClanTable.getInstance().getClan(castle.getOwnerId()).getAllyId();

                if (defenders.isEmpty()) {
                    if (allyId != 0) {
                        boolean allInSameAlliance = attackers.values().stream()
                                    .map(siegeClan -> ClanTable.getInstance().getClan(siegeClan.getClanId()))
                                    .allMatch(clan -> clan.getAllyId() == allyId);

                        if (allInSameAlliance) {
                            doIfNonNull(getAttackerClan(castle.getOwnerId()), newOwner -> {
                                removeAttacker(newOwner);
                                addDefender(newOwner, SiegeClanType.OWNER);
                            });
                            endSiege();
                            return;
                        }
                    }
                }

                var iterator = defenders.values().iterator();

                while (iterator.hasNext()) {
                    addAttacker(iterator.next());
                    iterator.remove();
                }

                doIfNonNull(getAttackerClan(castle.getOwnerId()), newOwner -> {
                    removeAttacker(newOwner);
                    addDefender(newOwner, SiegeClanType.OWNER);
                });

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

    private void removeAttacker(SiegeClanData sc) {
        attackers.remove(sc.getClanId());
    }

    private void addDefender(SiegeClanData sc, SiegeClanType type) {
        sc.setType(type);
        defenders.put(sc.getClanId(), sc);
    }

    @Override
    public void endSiege() {
        if (isInProgress) {
            Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.THE_S1_SIEGE_HAS_FINISHED).addCastleId(castle.getId()), new PlaySound("systemmsg_eu.18"));

            if (castle.getOwnerId() > 0) {
                var clan = ClanTable.getInstance().getClan(castle.getOwnerId());

                Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.CLAN_S1_IS_VICTORIOUS_OVER_S2_S_CASTLE_SIEGE).addString(clan.getName()).addCastleId(castle.getId()));

                if (clan.getId() == firstOwnerClanId) {
                    clan.increaseBloodAllianceCount();
                } else {
                    castle.setTicketBuyCount(0);
                    clan.forEachOnlineMember(noble -> Hero.getInstance().setCastleTaken(noble.getObjectId(), castle.getId()), Player::isNoble);
                }
            } else {
                Broadcast.toAllOnlinePlayers( getSystemMessage(SystemMessageId.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addCastleId(castle.getId()));
            }

            Stream.concat(attackers.values().stream(), defenders.values().stream()).forEach(siegeClan ->
                    doIfNonNull(ClanTable.getInstance().getClan(siegeClan.getClanId()), clan -> {
                    clan.forEachOnlineMember(Player::checkItemRestriction);
                    clan.clearSiegeKills();
                    clan.clearSiegeDeaths();
            }));

            castle.updateClansReputation();
            removeFlags();
            teleportPlayer(SiegeTeleportWhoType.NotOwner, TeleportWhereType.TOWN);
            isInProgress = false;
            updatePlayerSiegeStateFlags(true);
            saveCastleSiege();
            clearSiegeClan();
            removeTowers();

            SiegeGuardManager.getInstance().unspawnSiegeGuard(getCastle());

            if (castle.getOwnerId() > 0) {
                SiegeGuardManager.getInstance().removeSiegeGuards(getCastle());
            }
            castle.spawnDoor();

            var zone = castle.getZone();
            zone.setIsActive(false);
            zone.updateZoneStatusForCharactersInside();
            zone.setSiegeInstance(null);

            EventDispatcher.getInstance().notifyEventAsync(new OnCastleSiegeFinish(this), getCastle());
        }
    }

    public void approveSiegeDefenderClan(int clanId) {
        if (clanId <= 0) {
            return;
        }
        var siegeClan = defendersWaiting.remove(clanId);
        siegeClan.setType(SiegeClanType.DEFENDER);
        addDefender(siegeClan);
        getDAO(CastleDAO.class).save(siegeClan);
    }

    public boolean checkIfInZone(ILocational loc) {
        return isInProgress && castle.checkIfInZone(loc);
    }

    @Override
    public boolean checkIsAttacker(Clan clan) {
        return nonNull(getAttackerClan(clan));
    }

    @Override
    public boolean checkIsDefender(Clan clan) {
        return nonNull(getDefenderClan(clan));
    }

    public boolean checkIsDefenderWaiting(Clan clan) {
        return nonNull(getDefenderWaitingClan(clan));
    }

    public void clearSiegeClan() {
        var castleDAO = getDAO(CastleDAO.class);
        castleDAO.deleteSiegeByCastle(castle.getId());
        castleDAO.deleteSiegeByClan(castle.getOwnerId());

        attackers.clear();
        defenders.clear();
        defendersWaiting.clear();
    }

    private void clearSiegeWaitingClan() {
        getDAO(CastleDAO.class).deleteWaintingClansByCastle(castle.getId());
        defendersWaiting.clear();
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
        if (isNull(player.getClan())) {
            return;
        }

        var clan = player.getClan();

        if (castle.getOwnerId() != 0) {
            var allyId = ClanTable.getInstance().getClan(castle.getOwnerId()).getAllyId();

            if(allyId != 0 && clan.getAllyId() == allyId && !force) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_REGISTER_AS_AN_ATTACKER_BECAUSE_YOU_ARE_IN_AN_ALLIANCE_WITH_THE_CASTLE_OWNING_CLAN);
                return;
            }
        }


        if (force) {
            if (SiegeManager.getInstance().checkIsRegistered(clan, castle.getId())) {
                player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
            } else {
                saveSiegeClan(clan, SiegeClanType.ATTACKER);
            }
            return;
        }

        if (checkIfCanRegister(player, SiegeClanType.ATTACKER)) {
            saveSiegeClan(clan, SiegeClanType.ATTACKER);
        }
    }

    private void saveSiegeClan(Clan clan, SiegeClanType typeId) {
        SiegeClanData siegeClan = new SiegeClanData(clan.getId(), typeId, castle.getId());
        getDAO(CastleDAO.class).save(siegeClan);

        switch (typeId) {
            case DEFENDER, OWNER -> addDefender(siegeClan);
            case DEFENDER_PENDING -> addDefenderWaiting(siegeClan);
            case ATTACKER ->  addAttacker(siegeClan);
        }
    }

    private boolean checkIfCanRegister(Player player, SiegeClanType type) {
        if (isRegistrationOver) {
            player.sendPacket(getSystemMessage(SystemMessageId.THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED).addCastleId(castle.getId()));

        } else if (isInProgress) {
            player.sendPacket(SystemMessageId.THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_CANNOT_BE_DONE);

        } else if (isNull(player.getClan()) || player.getClan().getLevel() < SiegeManager.getInstance().getSiegeClanMinLevel()) {
            player.sendPacket(SystemMessageId.ONLY_CLANS_OF_LEVEL_3_OR_ABOVE_MAY_REGISTER_FOR_A_CASTLE_SIEGE);

        } else if (player.getClan().getId() == castle.getOwnerId()) {
            player.sendPacket(SystemMessageId.CASTLE_OWNING_CLANS_ARE_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE);

        } else if (player.getClan().getCastleId() > 0) {
            player.sendPacket(SystemMessageId.A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE);

        } else if (SiegeManager.getInstance().checkIsRegistered(player.getClan(), castle.getId())) {
            player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);

        } else if (checkIfAlreadyRegisteredForSameDay(player.getClan())) {
            player.sendPacket(SystemMessageId.YOUR_APPLICATION_HAS_BEEN_DENIED_BECAUSE_YOU_HAVE_ALREADY_SUBMITTED_A_REQUEST_FOR_ANOTHER_CASTLE_SIEGE);

        } else if (type == SiegeClanType.ATTACKER && getAttackerClans().size() >= SiegeManager.getInstance().getAttackerMaxClans()) {
            player.sendPacket(SystemMessageId.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE);

        } else if ((type == SiegeClanType.DEFENDER || type == SiegeClanType.DEFENDER_PENDING) && getDefenderClans().size() + getDefendersWaiting().size() >= SiegeManager.getInstance().getDefenderMaxClans()) {
            player.sendPacket(SystemMessageId.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE);

        } else {
            return true;
        }
        return false;
    }

    public void registerDefender(Player player) {
        registerDefender(player, false);
    }

    public void registerDefender(Player player, boolean force) {
        if (castle.getOwnerId() <= 0) {
            player.sendMessage("You cannot register as a defender because " + castle.getName() + " is owned by NPC.");
            return;
        }

        var clan = player.getClan();

        if (force) {
            if (SiegeManager.getInstance().checkIsRegistered(clan, castle.getId())) {
                player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
            } else {
                saveSiegeClan(clan, SiegeClanType.DEFENDER_PENDING);
            }
            return;
        }

        if (checkIfCanRegister(player, SiegeClanType.DEFENDER_PENDING)) {
            saveSiegeClan(clan, SiegeClanType.DEFENDER_PENDING);
        }
    }

    public void removeSiegeClan(Player player) {
        removeSiegeClan(player.getClan());
    }

    public void removeSiegeClan(Clan clan) {
        if (isNull(clan) || clan.getCastleId() == castle.getId() || !SiegeManager.getInstance().checkIsRegistered(clan, castle.getId())) {
            return;
        }
        removeSiegeClan(clan.getId());
    }

    public void removeSiegeClan(int clanId) {
        if (clanId <= 0) {
            return;
        }

        getDAO(CastleDAO.class).deleteSiegeClanByCastle(clanId, castle.getId());
        attackers.remove(clanId);
        defendersWaiting.remove(clanId);
        defenders.remove(clanId);
    }


    private void teleportSpectatorsInZone(TeleportWhereType teleportWhere) {
        castle.getZone().forEachPlayer(p -> p.teleToLocation(teleportWhere), p -> !p.isInSiege() && !p.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) && !p.isJailed());
    }

    private void teleportAttackersInZone(TeleportWhereType teleportWhere) {
        attackers.values().stream().map(a -> ClanTable.getInstance().getClan(a.getClanId())).forEach(
                clan -> clan.forEachOnlineMember(p -> p.teleToLocation(teleportWhere), p -> p.isInSiege() && !p.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) && !p.isJailed() ));
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

    private void removeTowers() {
        flameTowers.forEach(FlameTower::deleteMe);
        flameTowers.clear();

        controlTowers.forEach(ControlTower::deleteMe);
        controlTowers.clear();
    }

    private void removeFlags() {
        attackers.values().forEach(SiegeClanData::removeFlags);
        attackers.values().forEach(SiegeClanData::removeFlags);
    }

    /**
     * Remove flags from defenders.
     */
    private void removeDefenderFlags() {
        defenders.values().forEach(SiegeClanData::removeFlags);
    }

    @Override
    public final SiegeClanData getAttackerClan(Clan clan) {
        if (isNull(clan)) {
            return null;
        }
        return getAttackerClan(clan.getId());
    }

    @Override
    public final SiegeClanData getAttackerClan(int clanId) {
        return attackers.get(clanId);
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
        return defenders;
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
        if (nonNull(clan)) {
            return computeIfNonNull(getAttackerClan(clan), SiegeClanData::getFlags);
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

    public void announceToPlayer(SystemMessage message, boolean bothSides) {
        var stream = bothSides ? Stream.concat(getDefenderClans().values().stream(), getAttackerClans().values().stream()) : getDefenderClans().values().stream();
        stream.map(siegeClan -> ClanTable.getInstance().getClan(siegeClan.getClanId())).filter(Objects::nonNull).forEach(clan -> clan.forEachOnlineMember(message::sendTo));
    }

    private class ScheduleEndSiegeTask implements Runnable {

        @Override
        public void run() {
            if (!isInProgress) {
                return;
            }

            var timeRemaining = Duration.between(Instant.now(), endTime);
            if(timeRemaining.compareTo(ChronoUnit.HOURS.getDuration()) > 0) {
                announceToPlayer(getSystemMessage(SystemMessageId.S1_HOUR_S_UNTIL_CASTLE_SIEGE_CONCLUSION).addInt((int) timeRemaining.toHours()), true);
                ThreadPool.schedule(this, timeRemaining.minusHours(1));

            } else if(timeRemaining.toMinutes() > 10) {
                announceToPlayer(getSystemMessage(SystemMessageId.S1_MINUTE_S_UNTIL_CASTLE_SIEGE_CONCLUSION).addInt((int) timeRemaining.toMinutes()), true);
                ThreadPool.schedule(this, timeRemaining.minusMinutes(10));

            } else if(timeRemaining.toMinutes() > 5) {
                announceToPlayer(getSystemMessage(SystemMessageId.S1_MINUTE_S_UNTIL_CASTLE_SIEGE_CONCLUSION).addInt((int) timeRemaining.toMinutes()), true);
                ThreadPool.schedule(this, timeRemaining.minusMinutes(5));

            } else if(timeRemaining.toSeconds() > 10) {
                announceToPlayer(getSystemMessage(SystemMessageId.THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECOND_S).addInt((int) timeRemaining.toSeconds()), true);
                ThreadPool.schedule(this, timeRemaining.minusSeconds(10));

            } else if(timeRemaining.toSeconds() > 0) {
                announceToPlayer(getSystemMessage(SystemMessageId.THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECOND_S).addInt((int) timeRemaining.toSeconds()), true);
                ThreadPool.schedule(this, 1, TimeUnit.SECONDS);

            } else {
                castle.getSiege().endSiege();
            }
        }
    }

    private class ScheduleStartSiegeTask implements Runnable {
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
                scheduledStartSiegeTask = ThreadPool.schedule(new ScheduleStartSiegeTask(_castleInst), duration.minusDays(1));

            } else if(duration.compareTo(ChronoUnit.HOURS.getDuration()) > 0) {
                isRegistrationOver = true;
                Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.THE_REGISTRATION_TERM_FOR_S1_HAS_ENDED).addCastleId(castle.getId()));
                clearSiegeWaitingClan();
                scheduledStartSiegeTask = ThreadPool.schedule(new ScheduleStartSiegeTask(_castleInst), duration.minusHours(1));
            } else if(duration.compareTo(Duration.ZERO) > 0) {
                scheduledStartSiegeTask = ThreadPool.schedule(new ScheduleStartSiegeTask(_castleInst), duration);
            } else {
                _castleInst.getSiege().startSiege();
            }
        }
    }
}
