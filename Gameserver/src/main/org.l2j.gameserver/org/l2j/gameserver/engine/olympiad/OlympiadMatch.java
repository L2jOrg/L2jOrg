/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.engine.olympiad;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2j.gameserver.model.events.impl.character.OnCreatureHpChange;
import org.l2j.gameserver.model.events.impl.character.OnCreatureTeleported;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerCpChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.listeners.FunctionEventListener;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.olympiad.*;
import org.l2j.gameserver.world.zone.ZoneType;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.engine.olympiad.MatchState.*;
import static org.l2j.gameserver.model.Party.MessageType;
import static org.l2j.gameserver.network.SystemMessageId.*;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public abstract class OlympiadMatch extends AbstractEvent implements Runnable {

    private static final byte[] COUNT_DOWN_INTERVAL = {20, 10, 5, 4, 3, 2, 1, 0};

    private final ConsumerEventListener spectatorListener;
    private final ConsumerEventListener logoutListener;

    private MatchState state;
    private Instance arena;
    private ScheduledFuture<?> scheduled;
    private Duration duration;
    private FunctionEventListener deathListener;
    private ConsumerEventListener damageListener;
    private ConsumerEventListener hpListener;
    private ConsumerEventListener cpListener;
    private Instant start;
    private Duration battleDuration;
    private int countDownIndex = 0;

    protected boolean runAway;

    OlympiadMatch() {
        state = MatchState.CREATED;
        spectatorListener = new ConsumerEventListener(null, EventType.ON_CREATURE_TELEPORTED, (Consumer<OnCreatureTeleported>) this::onSpectatorEnter, this);
        logoutListener = new ConsumerEventListener(null, EventType.ON_PLAYER_LOGOUT, (Consumer<OnPlayerLogout>) this::onPlayerLogout, this);
    }

    @Override
    public void run() {
        switch (state) {
            case CREATED -> start();
            case STARTED -> teleportToArena();
            case WARM_UP -> countDown();
            case IN_BATTLE -> finishBattle();
            case FINISHED -> countDownTeleportBack();
        }
    }

    protected void finishBattle() {
        if(state == FINISHED) {
            return;
        }

        if(!scheduled.isDone()) {
            scheduled.cancel(true);
        }
        state = FINISHED;
        battleDuration = Duration.between(start, Instant.now());
        forEachParticipant(this::onMatchFinish);
        ThreadPool.schedule(this::processResult, 2, TimeUnit.SECONDS);
    }

    private void processResult() {
        arena.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
        countDownIndex = 0;
        var result = battleResult();
        switch (result) {
            case TIE -> processTie();
            case RED_WIN -> processRedVictory();
            case BLUE_WIN -> processBlueVictory();
        }
        countDownTeleportBack();
    }

    private void processBlueVictory() {
        var redTeam = getRedTeamResultInfo();
        var blueTeam = getBlueTeamResultInfo();

        updateParticipants(blueTeam, redTeam);
        arena.sendPacket(ExOlympiadMatchResult.victory(OlympiadMode.BLUE, blueTeam, redTeam));
        var leader = blueTeam.get(0);
        arena.sendPacket(getSystemMessage(CONGRATULATIONS_C1_YOU_WIN_THE_MATCH).addPcName(leader.getPlayer()));
    }

    private void updateParticipants(List<OlympiadResultInfo> winnerTeam, List<OlympiadResultInfo> loserTeam) {
        var olympiad = Olympiad.getInstance();

        var battlePoints = olympiad.getBattlePoints(loserTeam);

        final var loserLeader = loserTeam.get(0);
        final var winnerLeader = winnerTeam.get(0);

        for (var info : winnerTeam) {
            var points = olympiad.updateVictory(info.getPlayer(), battlePoints, loserLeader.getPlayer(), getBattleDuration());
            info.updatePoints(points, battlePoints);
        }

        for (var info : loserTeam) {
            var points = olympiad.updateDefeat(info.getPlayer(), -battlePoints,  winnerLeader.getPlayer(), getBattleDuration());
            info.updatePoints(points, -battlePoints);
        }
    }

    private Duration getBattleDuration() {
        return Objects.requireNonNullElseGet(battleDuration, () -> Duration.between(start, Instant.now()));
    }

    private void processRedVictory() {
        var redTeam = getRedTeamResultInfo();
        var blueTeam = getBlueTeamResultInfo();
        updateParticipants(redTeam, blueTeam);
        arena.sendPacket(ExOlympiadMatchResult.victory(OlympiadMode.RED, redTeam, blueTeam));
        var leader = redTeam.get(0);
        arena.sendPacket(getSystemMessage(CONGRATULATIONS_C1_YOU_WIN_THE_MATCH).addPcName(leader.getPlayer()));
    }

    private void processTie() {
        final var olympiad = Olympiad.getInstance();

        final var redTeam = getRedTeamResultInfo();
        final var blueTeam = getBlueTeamResultInfo();
        final var blueLeader = blueTeam.get(0);
        final var redLeader = redTeam.get(0);

        for (var info : redTeam) {
            var points = olympiad.updateTie(info.getPlayer(), blueLeader.getPlayer(), getBattleDuration());
            info.updatePoints(points, 0);
        }

        for (var info : blueTeam) {
            var points = olympiad.updateTie(info.getPlayer(), redLeader.getPlayer(), getBattleDuration());
            info.updatePoints(points, 0);
        }
        sendMessage(THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE);
        arena.sendPacket(ExOlympiadMatchResult.tie(redTeam, blueTeam));
    }

    private void onMatchFinish(Player player) {
        stopAttack(player);
        for (Summon summon : player.getServitors().values()) {
            stopAttack(summon);
        }

        player.setIsOlympiadStart(false);
        player.setOlympiadMatchId(-1);
        player.removeListener(deathListener);
        player.removeListener(damageListener);
        player.removeListener(hpListener);
        player.removeListener(cpListener);
        player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
        player.setCurrentCp(player.getMaxCp());
    }

    private void stopAttack(org.l2j.gameserver.model.actor.Playable playable) {
        playable.breakAttack();
        playable.breakCast();
        playable.forgetTarget();
        playable.setInsideZone(ZoneType.PVP, false);
        playable.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

    private void countDownTeleportBack() {
        if(countDownIndex >= COUNT_DOWN_INTERVAL.length - 1) {
            cleanUpMatch();
        } else {
            arena.sendPacket(getSystemMessage(YOU_WILL_BE_MOVED_BACK_TO_TOWN_IN_S1_SECOND_S).addInt(COUNT_DOWN_INTERVAL[countDownIndex]));
            scheduled = ThreadPool.schedule(this, COUNT_DOWN_INTERVAL[countDownIndex] - COUNT_DOWN_INTERVAL[++countDownIndex], TimeUnit.SECONDS);
        }
    }

    private void cleanUpMatch() {
        arena.forEachPlayer(this::leaveOlympiadMode);
        arena.destroy();
        Olympiad.getInstance().finishMatch(this);
        if(!runAway) {
            giveRewards();
        }
    }

    private void giveRewards() {
        final var result = battleResult();
        final var olympiad = Olympiad.getInstance();
        switch (result) {
            case BLUE_WIN -> {
                forBluePlayers(olympiad::giveWinnerRewards);
                forRedPlayers(olympiad::giveLoserRewards);
            }
            case RED_WIN -> {
                forRedPlayers(olympiad::giveWinnerRewards);
                forBluePlayers(olympiad::giveLoserRewards);
            }
            case TIE -> forEachParticipant(olympiad::giveTieRewards);
        }
    }

    protected void leaveOlympiadMode(Player player) {
        player.setOlympiadMatchId(-1);
        player.setOlympiadMode(OlympiadMode.NONE);
        player.sendPackets(new ExOlympiadMode(OlympiadMode.NONE), new ExOlympiadMatchMakingResult(false, getType()));
        arena.ejectPlayer(player);
        player.removeListener(logoutListener);
        if(player.isInObserverMode()) {
            player.setObserving(false);
            player.sendPackets(new ObservationReturn(player.getLocation()), new UserInfo(player));
        }
        Olympiad.getInstance().showOlympiadUI(player);
    }

    private void countDown() {
        if(countDownIndex >= COUNT_DOWN_INTERVAL.length - 1) {
            deathListener = new FunctionEventListener(null, EventType.ON_CREATURE_DEATH, (Function<OnCreatureDeath, TerminateReturn>)this::onDeath, this);
            hpListener = new ConsumerEventListener(null, EventType.ON_CREATURE_HP_CHANGE,  (Consumer<OnCreatureHpChange>) this::onHpChange, this);
            cpListener = new ConsumerEventListener(null, EventType.ON_PLAYER_CP_CHANGE, (Consumer<OnPlayerCpChange>) this::onCpChange, this);
            damageListener = new ConsumerEventListener(null, EventType.ON_CREATURE_DAMAGE_RECEIVED, (Consumer<OnCreatureDamageReceived>) this::onDamageReceived, this);
            forEachParticipant(this::onMatchStart);
            arena.sendPacket(PlaySound.music("ns17_f"));
            forEachParticipant(this::sendOlympiadUserInfo);
            forEachParticipant(this::sendBuffInfo);
            sendMessage(THE_MATCH_HAS_STARTED_FIGHT);
            state = IN_BATTLE;
            scheduled = ThreadPool.schedule(this, duration);
            start = Instant.now();
        } else {
            if(COUNT_DOWN_INTERVAL[countDownIndex] == 10) {
                arena.openAllDoors();
            }
            arena.sendPacket(getSystemMessage(S1_SECOND_S_TO_MATCH_START).addInt(COUNT_DOWN_INTERVAL[countDownIndex]));
            scheduled = ThreadPool.schedule(this, COUNT_DOWN_INTERVAL[countDownIndex] - COUNT_DOWN_INTERVAL[++countDownIndex], TimeUnit.SECONDS);
        }
    }

    private void onMatchStart(Player player) {
        player.setIsOlympiadStart(true);
        player.setInsideZone(ZoneType.PVP, true);
        player.addListener(deathListener);
        player.addListener(hpListener);
        player.addListener(cpListener);
        player.addListener(damageListener);
    }

    protected void sendBuffInfo(Player player) {
        var spellInfo = new ExOlympiadSpelledInfo(player);
        player.getEffectList().getEffects().forEach(spellInfo::addSkill);
        arena.sendPacket(spellInfo);
    }

    protected void sendOlympiadUserInfo(Player player) {
        arena.sendPacket(new ExOlympiadUserInfo(player));
    }

    private void teleportToArena() {
        state = WARM_UP;
        sendMessage(YOU_WILL_SHORTLY_MOVE_TO_THE_OLYMPIAD_ARENA);

        forEachParticipant(this::checkRequirements);

        if(state == FINISHED) {
            cleanUpMatch();
            final var packet = new ExOlympiadMatchMakingResult(false, getType());
            forEachParticipant(packet::sendTo);
            return;
        }

        forRedPlayers(player -> setOlympiadMode(player, OlympiadMode.RED));
        forBluePlayers(player -> setOlympiadMode(player, OlympiadMode.BLUE));
        var locations = arena.getEnterLocations();

        teleportPlayers(locations.get(0), locations.get(1), arena);
        scheduled = ThreadPool.schedule(this, 10, TimeUnit.SECONDS);
        Olympiad.getInstance().startMatch(this);

        forEachParticipant(this::onParticipantEnter);
    }

    private void checkRequirements(Player player) {
        boolean valid = true;

        if(player.isTeleporting()) {
            final var msg = getSystemMessage(C1_IS_CURRENTLY_TELEPORTING_AND_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD).addPcName(player);
            forEachParticipant(msg::sendTo);
            valid = false;
        } else if(player.isDead()) {
            final var msg = getSystemMessage(C1_IS_CURRENTLY_DEAD_AND_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD).addPcName(player);
            forEachParticipant(msg::sendTo);
            valid = false;
        } else if(!player.isInventoryUnder80()) {
            final var msg = getSystemMessage(C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_FOR_OLYMPIAD_AS_THE_INVENTORY_WEIGHT_SLOT_EXCEEDS_80).addPcName(player);
            forEachParticipant(msg::sendTo);
            valid = false;
        } else if(player.isFishing()) {
            final var msg = getSystemMessage(S1_ON_SCREEN).addString(player.getName() + " is currently fishing and cannot participate in the Olympiad");
            forEachParticipant(msg::sendTo);
            valid = false;
        } else if(player.isInsideZone(ZoneType.TIMED) || player.isInInstance()) {
            final var msg = getSystemMessage(S1_ON_SCREEN).addString(player.getName() + " is currently in timed hunting zone and cannot participate in the Olympiad");
            forEachParticipant(msg::sendTo);
            valid = false;
        }


        if(!valid) {
            state = FINISHED;
            Olympiad.getInstance().applyDismissPenalty(player);
        }
    }

    private void onParticipantEnter(Player player) {
        player.addListener(logoutListener);
        player.setOlympiadMatchId(getId());
        player.sendPackets(ExOlympiadInfo.hide(getType()));
        player.getEffectList().stopEffects(this::notAllowedInOlympiad, true, true);
        player.checkItemRestriction();

        final var party = player.getParty();
        if (nonNull(party)) {
            party.removePartyMember(player, MessageType.EXPELLED);
        }

        player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
        player.setCurrentCp(player.getMaxCp());

        for (var skill : player.getAllSkills()) {
            if (skill.getReuseDelay() > 10 && skill.getReuseDelay() <= Duration.ofSeconds(15).toMillis()) {
                player.enableSkill(skill);
            }
        }

        player.sendSkillList();
        player.sendPacket(new SkillCoolTime(player));
    }

    private boolean notAllowedInOlympiad(BuffInfo info) {
        final var skill = info.getSkill();
        return skill.isBlockedInOlympiad() || (skill.isDance() && !Olympiad.getInstance().keepDances());
    }

    private void onDamageReceived(OnCreatureDamageReceived event) {
        var attacker = event.getAttacker().getActingPlayer();
        if(nonNull(attacker) && event.getTarget() instanceof Player target) {
            onDamage(attacker, target, event.getDamage());
        }
    }

    private void onHpChange(OnCreatureHpChange event) {
        if(event.getCreature() instanceof Player player) {
            onStatusChange(player);
        }
    }

    private void onCpChange(OnPlayerCpChange event) {
        onStatusChange(event.getPlayer());
    }

    private void onStatusChange(Player player) {
        arena.sendPacket(new ExOlympiadUserInfo(player));
    }

    private TerminateReturn onDeath(OnCreatureDeath event) {
        if(event.getTarget() instanceof Player player) {
            if(!processPlayerDeath(player)) {
                return new TerminateReturn(true, true, true);
            }
        }
        return null;
    }

    private void onPlayerLogout(OnPlayerLogout event) {
        processPlayerLogout(event.getPlayer());
    }

    private void setOlympiadMode(Player player, OlympiadMode mode) {
        player.setOlympiadMode(mode);
        player.setOlympiadSide(mode.ordinal());
        player.sendPacket(new ExOlympiadMode(mode));
    }

    private void start() {
        state = MatchState.STARTED;
        scheduled = ThreadPool.schedule(this, 1, TimeUnit.MINUTES);
        sendMessage(AFTER_ABOUT_1_MINUTE_YOU_WILL_MOVE_TO_THE_OLYMPIAD_ARENA);
    }

    void addSpectator(Player player) {
        if(player.isInParty()) {
            player.getParty().removePartyMember(player, MessageType.EXPELLED);
        }
        player.setOlympiadMatchId(getId());
        player.addListener(spectatorListener);
        player.setObserving(true);
        player.setOlympiadMode(OlympiadMode.SPECTATOR);
        player.sendPacket(new ExOlympiadMode(OlympiadMode.SPECTATOR));
        player.teleToLocation(arena.getEnterLocations().get(2), arena);
        forEachParticipant(this::sendOlympiadUserInfo);
    }

    private void onSpectatorEnter(OnCreatureTeleported event) {
        var creature = event.getCreature();
        creature.sendPacket(ExOlympiadInfo.hide(getType()));
        creature.removeListener(spectatorListener);
        forEachParticipant(this::sendOlympiadUserInfo);
        forEachParticipant(this::sendBuffInfo);
    }

    void removeSpetator(Player player) {
        leaveOlympiadMode(player);
    }

    public void sendPacket(ServerPacket packet) {
        arena.sendPacket(packet);
    }

    public int getId() {
        return arena.getId();
    }

    public boolean isInBattle() {
        return state == IN_BATTLE;
    }

    void setArenaInstance(Instance arena) {
        this.arena = arena;
    }

    void setMatchDuration(Duration duration) {
        this.duration = duration;
    }

    public abstract OlympiadRuleType getType();

    public abstract void addParticipant(Player player);

    public abstract String getPlayerRedName();

    public abstract String getPlayerBlueName();

    protected abstract List<OlympiadResultInfo> getRedTeamResultInfo();

    protected abstract List<OlympiadResultInfo> getBlueTeamResultInfo();

    protected abstract void forEachParticipant(Consumer<Player> action);

    protected abstract void forBluePlayers(Consumer<Player> action);

    protected abstract void forRedPlayers(Consumer<Player> action);

    protected abstract void teleportPlayers(Location redLocation, Location blueLocation, Instance arena);

    protected abstract OlympiadResult battleResult();

    protected abstract void onDamage(Player attacker, Player target, double damage);

    protected abstract boolean processPlayerDeath(Player player);

    protected abstract void processPlayerLogout(Player player);

    static OlympiadMatch of(OlympiadRuleType type) {
        return new OlympiadClasslessMatch();
    }
}
