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
package org.l2j.gameserver.engine.siege;

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.ArtifactSpawn;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.ScheduleTarget;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.siege.*;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.SystemMessageId.*;

/**
 * @author JoeAlisson
 */
public class SiegeEngine extends AbstractEventManager<Siege> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiegeEngine.class);

    private SiegeSettings settings;
    private ConsumerEventListener loginListener;
    private IntMap<Siege> sieges = Containers.emptyIntMap();

    private SiegeEngine() {  }

    @Override
    public void config(GameXmlReader reader, Node configNode) {
        settings = SiegeSettings.parse(reader, configNode);
    }

    @Override
    public void onInitialized() {
        scheduleUndefinedSiegeDates();
    }

    private void scheduleUndefinedSiegeDates() {
        for (Castle castle : CastleManager.getInstance().getCastles()) {
            if(isNull(castle.getSiegeDate())) {
                scheduleNextSiegeDate(castle);
            }
        }
    }

    private void scheduleNextSiegeDate(Castle castle) {
        var scheduleDays = settings.siegeScheduleDays.get(castle.getId());

        if(Util.isNullOrEmpty(scheduleDays)) {
            return;
        }

        var siegeDate = LocalDate.now().plusWeeks(1).atStartOfDay();
        LocalDateTime nextAvailable = null;

        while(isNull(nextAvailable)) {
            siegeDate = siegeDate.plusWeeks(1);
            nextAvailable = nextAvailableSiegeDate(scheduleDays, siegeDate);
        }

        castle.setSiegeDate(nextAvailable);
    }

    private LocalDateTime nextAvailableSiegeDate(Set<DayOfWeek> scheduleDays, LocalDateTime siegeDate) {
        LocalDateTime nextAvailable = null;
        for (var day : scheduleDays) {
            var temp =  siegeDate.with(TemporalAdjusters.next(day));
            if(countSiegesInDay(temp.toLocalDate()) < settings.maxSiegesInDay && (isNull(nextAvailable) || nextAvailable.isAfter(temp))) {
                nextAvailable = temp;
            }
        }
        return nextAvailable;
    }

    private int countSiegesInDay(LocalDate date) {
        int sieges = 0;
        for (Castle castle : CastleManager.getInstance().getCastles()) {
            var siegeDate = castle.getSiegeDate();
            if(nonNull(siegeDate) && siegeDate.toLocalDate().compareTo(date) == 0) {
                sieges++;
            }
        }
        return sieges;
    }

    @ScheduleTarget
    public void onPreparationStart() {
        filterScheduledSieges();
        if(!sieges.isEmpty()) {
            loginListener = new ConsumerEventListener(null, EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) this::onPlayerLogin, this);
            Listeners.players().addListener(loginListener);

            for (var siege: sieges.values()) {
                siege.setState(SiegeState.PREPARATION);
                Broadcast.toAllOnlinePlayers(new ExMercenarySiegeHUDInfo(siege));
            }
        } else {
            LOGGER.info("There is no siege scheduled at this time");
        }
    }

    private void filterScheduledSieges() {
        var now = LocalDateTime.now();
        sieges = new HashIntMap<>();

        for (var scheduleDay : settings.siegeScheduleDays.entrySet()) {
            var castleId = scheduleDay.getKey();
            var castle = CastleManager.getInstance().getCastleById(castleId);

            if(nonNull(castle) &&  castle.getSiegeDate().withHour(0).compareTo(now) <= 0) {
                var days = scheduleDay.getValue();
                if(days.contains(now.getDayOfWeek())) {
                    sieges.put(castleId, new Siege(castle));
                }
            }
        }
    }

    private void onPlayerLogin(OnPlayerLogin event) {
        final var player = event.getPlayer();
        for (var siege : sieges.values()) {
            player.sendPacket(new ExMercenarySiegeHUDInfo(siege));
        }
    }

    @ScheduleTarget
    public void onSiegeStart() {
        sieges.values().forEach(this::startSiege);
    }

    private void startSiege(Siege siege) {
        if(siege.hasAttackers()) {
            siege.start();
        } else {
            siege.cancelDueNoInterest();
        }
        Broadcast.toAllOnlinePlayers(new ExMercenarySiegeHUDInfo(siege));
    }

    @ScheduleTarget
    public void onSiegeStop() {
        if(nonNull(loginListener)) {
            Listeners.players().removeListener(loginListener);
            loginListener = null;
        }
    }

    public void registerAttacker(Player player, int castleId) {
        var siege = sieges.get(castleId);
        final Clan clan = player.getClan();

        if (!hasSiegeManagerRights(player, siege, clan))
            return;

        if (System.currentTimeMillis() < clan.getDissolvingExpiryTime()) {
            player.sendPacket(SystemMessageId.YOUR_CLAN_MAY_NOT_REGISTER_TO_PARTICIPATE_IN_A_SIEGE_WHILE_UNDER_A_GRACE_PERIOD_OF_THE_CLAN_S_DISSOLUTION);
            return;
        }

        registerAttacker(player, clan, siege);
    }

    private boolean hasSiegeManagerRights(Player player, Siege siege, Clan clan) {
        if(isNull(siege) || isNull(clan)) {
            return false;
        }

        if (!player.hasClanPrivilege(ClanPrivilege.CS_MANAGE_SIEGE)) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return false;
        }
        return true;
    }

    public void registerDefender(Player player, int castleId) {
        var siege = sieges.get(castleId);
        final Clan clan = player.getClan();

        if (!hasSiegeManagerRights(player, siege, clan))
            return;

        registerDefender(player, clan, siege);
    }

    public void cancelAttacker(Player player, int castleId) {
        var siege = sieges.get(castleId);
        final Clan clan = player.getClan();

        if (!hasSiegeManagerRights(player, siege, clan) || !isRegisteredInSiege(clan))
            return;

        siege.removeSiegeClan(clan);
        player.sendPacket(new ExMCWCastleSiegeAttackerList(siege));

    }

    public void cancelDefender(Player player, int castleId) {
        var siege = sieges.get(castleId);
        final Clan clan = player.getClan();

        if (!hasSiegeManagerRights(player, siege, clan) || clan.getCastleId() == castleId || !isRegisteredInSiege(clan))
            return;

        siege.removeSiegeClan(clan);
        player.sendPacket(new ExMCWCastleSiegeDefenderList(siege));

    }

    private void registerDefender(Player player, Clan clan, Siege siege) {
        final var castle = siege.getCastle();
        if (castle.getOwnerId() <= 0) {
            player.sendMessage("You cannot register as a defender because " + castle.getName() + " is owned by NPC.");
            return;
        }

        if(!validateRegistration(player, siege, clan)) {
            return;
        }

        if(siege.registeredDefendersAmount() >= settings.maxDefenders) {
            player.sendPacket(SystemMessageId.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE);
            return;
        }

        siege.registerDefender(clan);
        player.sendPacket(new ExMCWCastleSiegeDefenderList(siege));
        onSiegeRegistration(clan);
    }

    private void registerAttacker(Player player, Clan clan, Siege siege) {
        if(!validateRegistration(player, siege, clan)) {
            return;
        }

        if(siege.registeredAttackersAmount() >= settings.maxAttackers) {
            player.sendPacket(SystemMessageId.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE);
            return;
        }

        var castleOwner = siege.getCastle().getOwner();

        if(nonNull(castleOwner) && castleOwner.isAlly(clan)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_REGISTER_AS_AN_ATTACKER_BECAUSE_YOU_ARE_IN_AN_ALLIANCE_WITH_THE_CASTLE_OWNING_CLAN);
            return;
        }

        siege.registerAttacker(clan);
        player.sendPacket(new ExMCWCastleSiegeAttackerList(siege));
        onSiegeRegistration(clan);
    }

    private void onSiegeRegistration(Clan clan) {
        clan.forEachMember(member -> removeMercenary(member.getObjectId()));
    }

    private void removeMercenary(int playerId) {
        for (Siege siege : sieges.values()) {
            siege.removeMercenary(playerId);
        }
    }

    private boolean validateRegistration(Player player, Siege siege, Clan clan) {
        boolean valid = true;
        if(!siege.isInPreparation()) {
            player.sendPacket(SystemMessageId.THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_CANNOT_BE_DONE);
            valid = false;
        } else if(clan.getLevel() < settings.minClanLevel) {
            player.sendPacket(SystemMessageId.ONLY_CLANS_OF_LEVEL_3_OR_ABOVE_MAY_REGISTER_FOR_A_CASTLE_SIEGE);
            valid = false;
        } else if(clan == siege.getCastle().getOwner()) {
            player.sendPacket(SystemMessageId.CASTLE_OWNING_CLANS_ARE_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE);
            valid = false;
        } else if(clan.getCastleId() > 0) {
            player.sendPacket(SystemMessageId.A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE);
            valid = false;
        } else if (isRegisteredInSiege(clan)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
            valid = false;
        }
        return valid;
    }

    private boolean isRegisteredInSiege(Clan clan) {
        for (Siege siege : sieges.values()) {
            if(siege.isRegistered(clan)) {
                return true;
            }
        }
        return false;
    }

    public void showSiegeInfo(Player player, int castleId) {
        final var siege = sieges.get(castleId);
        if(nonNull(siege)) {
            player.sendPacket(new ExMercenaryCastleWarCastleSiegeInfo(siege));
        }
    }

    public void showDefenderList(Player player, int castleId) {
        final var siege = sieges.get(castleId);
        if(nonNull(siege)) {
            player.sendPacket(new ExMCWCastleSiegeDefenderList(siege));
        }
    }

    public void showAttackerList(Player player, int castleId) {
        final var siege = sieges.get(castleId);
        if(nonNull(siege)) {
            player.sendPacket(new ExMCWCastleSiegeAttackerList(siege));
        }
    }

    public void recruitMercenary(Player player, int castleId, long reward) {
        final var siege = sieges.get(castleId);
        final Clan clan = player.getClan();

        if (!validateRecruitmentRights(player, siege, clan))
            return;

        final var participant = siege.getSiegeParticipant(clan);

        if(isNull(participant)) {
            player.sendPacket(TO_RECRUIT_MERCENARIES_CLANS_MUST_PARTICIPATE_IN_THE_CASTLE_SIEGE);
            return;
        }

        if(participant.isWaitingApproval() || participant.isDeclined()) {
            player.sendPacket(A_CLAN_THAT_IS_NOT_ACCEPTED_AS_DEFENDERS_CANNOT_RECRUIT_MERCENARIES);
            return;
        }

        if(siege.isRecruitingMercenary(clan)) {
            player.sendPacket(ALREADY_RECRUITING_MERCENARIES);
            return;
        }

        siege.registerMercenaryRecruitment(clan, reward);
        sendParticipantSameTypeList(player, clan, siege);
        //MERCENARY_PARTICIPATION_IS_REQUESTED_IN_S1_TERRITORY
    }

    private void sendParticipantSameTypeList(Player player, Clan clan, Siege siege) {
        if(siege.isAttacker(clan)) {
            player.sendPacket(new ExMCWCastleSiegeAttackerList(siege));
        } else if(siege.isDefender(clan)) {
            player.sendPacket(new ExMCWCastleSiegeDefenderList(siege));
        }
    }

    public void cancelMercenaryRecruitment(Player player, int castleId) {
        final var siege = sieges.get(castleId);
        final Clan clan = player.getClan();

        if (validateRecruitmentRights(player, siege, clan)) {
            // THERE_IS_A_MERCENARY_APPLICANT_YOU_CANT_CANCEL_NOW
            siege.removeMercenaryRecruitment(clan);
            sendParticipantSameTypeList(player, clan, siege);
            // MERCENARY_PARTICIPATION_REQUEST_IS_CANCELLED_IN_S1_TERRITORY
        }
    }

    private boolean validateRecruitmentRights(Player player, Siege siege, Clan clan) {
        if (!hasSiegeManagerRights(player, siege, clan))
            return false;

        if (!siege.isInPreparation()) {
            player.sendPacket(IT_IS_NOT_A_MERCENARY_RECRUITMENT_PERIOD);
            return false;
        }
        return true;
    }

    public void showMercenaries(Player player, int castleId, int clanId) {
        final var siege = sieges.get(castleId);
        final var clan = ClanEngine.getInstance().getClan(clanId);
        if(nonNull(siege) && nonNull(clan) && siege.isRecruitingMercenary(clan)) {
            final var mercenaries = updateMercenariesStatus(siege, clan);
            player.sendPacket(new ExPledgeMercenaryMemberList(siege, clan, mercenaries));
        }
    }

    private Collection<Mercenary> updateMercenariesStatus(Siege siege, Clan clan) {
        final var mercenaries = siege.getMercenaries(clan);
        for (Mercenary mercenary : mercenaries)
            updateMercenary(mercenary);
        return mercenaries;
    }

    private void updateMercenary(Mercenary mercenary) {
        var player = World.getInstance().findPlayer(mercenary.getId());
        if(nonNull(player)) {
            mercenary.update(player);
        } else {
            mercenary.setOnline(false);
        }
    }

    public void joinMercenaries(Player player, int castleId, int clanId) {
        final var siege = sieges.get(castleId);
        if(nonNull(siege)) {
            final var clan = ClanEngine.getInstance().getClan(clanId);

            if (!validateMercenary(player, clan, siege)) {
                player.sendPacket(ExPledgeMercenaryMemberJoin.joinFailed(clanId));
                return;
            }

            siege.joinMercenary(player, clan);
            player.sendPacket(APPLIED_TO_PARTICIPATE_IN_THE_TERRITORY_WAR_AS_A_MERCENARY);
            player.sendPacket(ExPledgeMercenaryMemberJoin.joined(clanId));
        }
    }

    private boolean validateMercenary(Player player, Clan clan, Siege siege) {
        if(siege.hasStarted()) {
            player.sendPacket(YOU_CANNOT_APPLY_FOR_MERCENARY_AFTER_THE_CASTLE_SIEGE_STARTS);
            return false;
        }

        if(player.getLevel() < settings.minMercenaryLevel) {
            player.sendPacket(YOUR_LEVEL_CANNOT_BE_A_MERCENARY);
            return false;
        }

        if(player.isInParty()) {
            player.sendPacket(YOU_CANNOT_BE_A_MERCENARY_WHEN_YOU_BELONG_TO_A_PARTY);
            return false;
        }

        if(isNull(clan) || !siege.isRecruitingMercenary(clan)) {
            player.sendPacket(THE_CLAN_IS_NOT_RECRUITING_MERCENARIES);
            return false;
        }

        final var participant = siege.getSiegeParticipant(clan);
        if(participant.getMercenariesCount() >= settings.maxMercenaries) {
            player.sendPacket(EXCEEDED_THE_MAXIMUM_NUMBER_OF_MERCENARIES_YOU_CANNOT_APPLY);
            return false;
        }

        final var playerClan = player.getClan();

        if(nonNull(playerClan)) {
            if(clan.isAtWarWith(playerClan)) {
                player.sendPacket(MERCENARIES_CANNOT_JOIN_A_CLAN_THAT_HAS_BEEN_DECLARED_HAS_DECLARED_OR_HAS_MUTUALLY_DECLARED_WAR);
                return false;
            }

            if(siege.isRegistered(playerClan)) {
                player.sendPacket(MEMBERS_OF_THE_CLANS_THAT_ARE_REGISTERED_AS_ATTACKERS_DEFENDERS_OR_OWN_THE_CASTLE_CANNOT_BE_MERCENARIES);
                return false;
            }
        }

        if(playerClan == clan) {
            player.sendPacket(YOU_CANNOT_BE_A_MERCENARY_FOR_YOUR_OWN_CLAN);
            return false;
        }

        final var castle = siege.getCastle();
        if(nonNull(castle.getOwner()) && castle.getOwner() == player.getClan()) {
            player.sendPacket(THE_CLAN_WHO_OWNS_THE_TERRITORY_CANNOT_PARTICIPATE_IN_THE_TERRITORY_WAR_AS_MERCENARIES);
            return false;
        }

        if(isMercenary(player.getId())) {
            player.sendPacket(THE_CHARACTER_IS_PARTICIPATING_AS_A_MERCENARY);
            return false;
        }

        if(player.getAccountPlayers().keySet().anyMatch(this::isMercenary)) {
            player.sendPacket(ANOTHER_CHARACTER_OF_YOUR_ACCOUNT_IS_A_MERCENARY_ONLY_ONE_CHARACTER_PER_ACCOUNT_CAN_BE_A_MERCENARY);
            return false;
        }

        return true;
    }

    private boolean isMercenary(int playerId) {
        for (Siege siege : sieges.values()) {
            if(siege.isMercenary(playerId)) {
                return true;
            }
        }
        return false;
    }

    public void leaveMercenaries(Player player, int castleId, int clanId) {
        final var siege = sieges.get(castleId);
        final var clan = ClanEngine.getInstance().getClan(clanId);
        if(nonNull(siege) && nonNull(clan)) {
            if(!validateLeaveMercenary(player)) {
                player.sendPacket(ExPledgeMercenaryMemberJoin.leaveFailed(clanId));
                return;
            }
            siege.leaveMercenary(player, clan);
            player.sendPacket(ExPledgeMercenaryMemberJoin.left(clanId));
        }
    }

    private boolean validateLeaveMercenary(Player player) {
        if (player.isInParty()) {
            player.sendPacket(YOU_CANNOT_CANCEL_THE_MERCENARY_STATUS_WHEN_YOU_BELONG_TO_A_PARTY);
            return false;
        }

        if (!isMercenary(player.getId())) {
            player.sendPacket(NOT_IN_MERCENARY_MODE);
            return false;
        }

        return true;
    }

    public int remainTimeToStart() {
        return (int) getScheduler("start-siege").getRemainingTime(TimeUnit.SECONDS);
    }

    public int remainTimeToFinish() {
        return (int) getScheduler("stop-siege").getRemainingTime(TimeUnit.SECONDS);
    }

    Collection<ArtifactSpawn> controlTowersOf(Castle castle) {
       return settings.controlTowers.getOrDefault(castle.getId(), Collections.emptyList());
    }

    Collection<ArtifactSpawn> flameTowersOf(Castle castle) {
        return settings.flameTowers.getOrDefault(castle.getId(), Collections.emptyList());
    }

    ArtifactSpawn castleLordOf(Castle castle) {
        return settings.castleLords.get(castle.getId());
    }

    public static SiegeEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final SiegeEngine INSTANCE = new SiegeEngine();
    }
}
