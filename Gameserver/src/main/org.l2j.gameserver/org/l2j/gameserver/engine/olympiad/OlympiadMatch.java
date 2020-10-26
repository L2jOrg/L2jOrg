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
package org.l2j.gameserver.engine.olympiad;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2j.gameserver.model.events.impl.character.OnCreatureHpChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerCpChange;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.listeners.FunctionEventListener;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.olympiad.*;
import org.l2j.gameserver.world.zone.ZoneType;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.l2j.gameserver.engine.olympiad.MatchState.*;
import static org.l2j.gameserver.network.SystemMessageId.*;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public abstract class OlympiadMatch extends AbstractEvent implements Runnable {

    private static final byte[] COUNT_DOWN_INTERVAL = {20, 10, 5, 4, 3, 2, 1, 0};

    protected MatchState state;
    private Instance arena;
    private int countDownIndex = 0;
    private ScheduledFuture<?> scheduled;
    private Duration duration;
    private FunctionEventListener deathListener;
    private ConsumerEventListener hpListener;
    private ConsumerEventListener cpListener;

    OlympiadMatch() {
        state = MatchState.CREATED;
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
        if(!scheduled.isDone()) {
            scheduled.cancel(true);
        }
        state = FINISHED;
        forEachParticipant(this::onMatchFinish);
        calculateResults();
        sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
        countDownIndex = 0;
        countDownTeleportBack();
    }

    private void onMatchFinish(Player player) {
        player.setIsOlympiadStart(false);
        player.setOlympiadGameId(0);
        player.setInsideZone(ZoneType.PVP, false);
        player.removeListener(deathListener);
        player.removeListener(hpListener);
        player.removeListener(cpListener);
    }

    private void countDownTeleportBack() {
        if(countDownIndex >= COUNT_DOWN_INTERVAL.length - 1) {
            teleportBack();
            cleanUpMatch();
        } else {
            sendPacket(getSystemMessage(YOU_WILL_BE_MOVED_BACK_TO_TOWN_IN_S1_SECOND_S).addInt(COUNT_DOWN_INTERVAL[countDownIndex]));
            scheduled = ThreadPool.schedule(this, COUNT_DOWN_INTERVAL[countDownIndex] - COUNT_DOWN_INTERVAL[++countDownIndex], TimeUnit.SECONDS);
        }
    }

    private void cleanUpMatch() {
        forEachParticipant(this::leaveOlympiadMode);
        arena.destroy();
        Olympiad.getInstance().finishMatch(this);
    }

    private void leaveOlympiadMode(Player player) {
        player.setOlympiadMode(OlympiadMode.NONE);
        player.sendPacket(new ExOlympiadMode(OlympiadMode.NONE));
    }

    private void countDown() {
        if(countDownIndex >= COUNT_DOWN_INTERVAL.length - 1) {
            deathListener = new FunctionEventListener(null, EventType.ON_CREATURE_DEATH, (Function<OnCreatureDeath, TerminateReturn>)this::onDeath, this);
            hpListener = new ConsumerEventListener(null, EventType.ON_CREATURE_HP_CHANGE,  (Consumer<OnCreatureHpChange>) this::onHpChange, this);
            cpListener = new ConsumerEventListener(null, EventType.ON_PLAYER_CP_CHANGE, (Consumer<OnPlayerCpChange>) this::onCpChange, this);
            forEachParticipant(this::onMatchStart);
            sendPacket(PlaySound.music("ns17_f"));
            forRedPlayers(this::sendOlympiadUserInfo);
            forBluePlayers(this::sendOlympiadUserInfo);
            sendMessage(THE_MATCH_HAS_STARTED_FIGHT);
            state = IN_BATTLE;
            scheduled = ThreadPool.schedule(this, duration);
        } else {
            if(COUNT_DOWN_INTERVAL[countDownIndex] == 10) {
                arena.openAllDoors();
            }
            sendPacket(getSystemMessage(S1_SECOND_S_TO_MATCH_START).addInt(COUNT_DOWN_INTERVAL[countDownIndex]));
            scheduled = ThreadPool.schedule(this, COUNT_DOWN_INTERVAL[countDownIndex] - COUNT_DOWN_INTERVAL[++countDownIndex], TimeUnit.SECONDS);
        }
    }

    private void onMatchStart(Player player) {
        player.setOlympiadGameId(getId());
        player.setIsOlympiadStart(true);
        player.setInsideZone(ZoneType.PVP, true);
        player.addListener(deathListener);
        player.addListener(hpListener);
        player.addListener(cpListener);
    }

    private void sendOlympiadUserInfo(Player player) {
        sendPacket(new ExOlympiadUserInfo(player));
        var spellInfo = new ExOlympiadSpelledInfo(player);
        player.getEffectList().getEffects().forEach(spellInfo::addSkill);
        sendPacket(spellInfo);
    }

    private void teleportToArena() {
        state = WARM_UP;
        sendMessage(YOU_WILL_SHORTLY_MOVE_TO_THE_OLYMPIAD_ARENA);
        forRedPlayers(player -> setOlympiadMode(player, OlympiadMode.RED));
        forBluePlayers(player -> setOlympiadMode(player, OlympiadMode.BLUE));

        var locations = arena.getEnterLocations();
        teleportPlayers(locations.get(0), locations.get(1), arena);
        scheduled = ThreadPool.schedule(this, 10, TimeUnit.SECONDS);
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
        sendPacket(new ExOlympiadUserInfo(player));
    }

    private TerminateReturn onDeath(OnCreatureDeath event) {
        if(event.getTarget() instanceof Player player) {
            if(!processPlayerDeath(player)) {
                return new TerminateReturn(true, true, true);
            }
        }
        return null;
    }

    protected abstract boolean processPlayerDeath(Player player);

    private void setOlympiadMode(Player player, OlympiadMode mode) {
        player.setOlympiadMode(mode);
        player.setOlympiadSide(mode.ordinal());
        player.sendPacket(new ExOlympiadMode(mode));
    }

    private void start() {
        state = MatchState.STARTED;
        scheduled = ThreadPool.schedule(this, 1, TimeUnit.MINUTES);
        sendMessage(AFTER_ABOUT_1_MINUTE_YOU_WILL_MOVE_TO_THE_OLYMPIAD_ARENA);
        sendPacket(new ExOlympiadMatchMakingResult(false, getType()));
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

    protected abstract void forEachParticipant(Consumer<Player> action);

    protected abstract void forBluePlayers(Consumer<Player> action);

    protected abstract void forRedPlayers(Consumer<Player> action);

    protected abstract void teleportPlayers(Location redLocation, Location blueLocation, Instance arena);

    protected abstract void calculateResults();

    protected abstract void teleportBack();

    static OlympiadMatch of(OlympiadRuleType type) {
        return new OlympiadClasslessMatch();
    }
}
