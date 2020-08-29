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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.eventengine.AbstractEvent;

import java.util.concurrent.TimeUnit;

import static org.l2j.gameserver.engine.olympiad.MatchState.WARM_UP;
import static org.l2j.gameserver.network.SystemMessageId.AFTER_ABOUT_1_MINUTE_YOU_WILL_MOVE_TO_THE_OLYMPIAD_ARENA;

/**
 * @author JoeAlisson
 */
public abstract class OlympiadMatch extends AbstractEvent implements Runnable {

    protected MatchState state;

    OlympiadMatch() {
        state = MatchState.CREATED;
    }

    @Override
    public void run() {
        switch (state) {
            case CREATED -> start();
            case STARTED -> teleportToArena();
        }

    }

    private void teleportToArena() {
        state = WARM_UP;
    }

    private void start() {
        state = MatchState.STARTED;
        sendMessage(AFTER_ABOUT_1_MINUTE_YOU_WILL_MOVE_TO_THE_OLYMPIAD_ARENA);
        ThreadPool.schedule(this, 1, TimeUnit.MINUTES);
    }

    public abstract void addParticipant(Player player);

    static OlympiadMatch of(OlympiadRuleType type) {
        return new OlympiadClassLessMatch();
    }
}
