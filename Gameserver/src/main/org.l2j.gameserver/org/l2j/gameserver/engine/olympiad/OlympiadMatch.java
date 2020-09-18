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
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.network.serverpackets.PlaySound;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.l2j.gameserver.engine.olympiad.MatchState.IN_BATTLE;
import static org.l2j.gameserver.engine.olympiad.MatchState.WARM_UP;
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
        }
    }

    private void finishBattle() {
        // TODO
    }

    private void countDown() {
        if(countDownIndex >= COUNT_DOWN_INTERVAL.length - 1) {
            broadcastPacket(PlaySound.music("ns17_f"));
            broadcastMessage(THE_MATCH_HAS_STARTED_FIGHT);
            sendOlympiadUserInfo();
            sendOlympiadSpellInfo();
            state = IN_BATTLE;
            scheduled = ThreadPool.schedule(this, 6, TimeUnit.MINUTES);
        } else {
            var msg = getSystemMessage(S1_SECOND_S_TO_MATCH_START).addInt(COUNT_DOWN_INTERVAL[countDownIndex]);
            broadcastPacket(msg);
            scheduled = ThreadPool.schedule(this, COUNT_DOWN_INTERVAL[countDownIndex] - COUNT_DOWN_INTERVAL[++countDownIndex], TimeUnit.SECONDS);
        }
    }

    private void teleportToArena() {
        state = WARM_UP;
        broadcastMessage(YOU_WILL_SHORTLY_MOVE_TO_THE_OLYMPIAD_ARENA);
        var locations = arena.getEnterLocations();
        teleportPlayers(locations.get(0), locations.get(1), arena);
        scheduled = ThreadPool.schedule(this, 1, TimeUnit.MINUTES);
    }

    private void start() {
        state = MatchState.STARTED;
        broadcastMessage(AFTER_ABOUT_1_MINUTE_YOU_WILL_MOVE_TO_THE_OLYMPIAD_ARENA);
        scheduled = ThreadPool.schedule(this, 1, TimeUnit.MINUTES);
    }



    public void setArenaInstance(Instance arena) {
        this.arena = arena;
    }

    public abstract void addParticipant(Player player);

    protected abstract void teleportPlayers(Location first, Location second, Instance arena);

    protected abstract void sendOlympiadUserInfo();

    protected abstract void sendOlympiadSpellInfo();

    static OlympiadMatch of(OlympiadRuleType type) {
        return new OlympiadClasslessMatch();
    }
}
