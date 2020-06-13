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
package org.l2j.gameserver.model.quest;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;


public class QuestTimer {
    protected static final Logger LOGGER = LoggerFactory.getLogger(QuestTimer.class);
    final String _name;
    final Quest _quest;
    final Npc _npc;
    final Player _player;
    final boolean _isRepeating;
    private final ScheduledFuture<?> _scheduler;
    boolean _isActive = true;
    public QuestTimer(Quest quest, String name, long time, Npc npc, Player player, boolean repeating) {
        _name = name;
        _quest = quest;
        _player = player;
        _npc = npc;
        _isRepeating = repeating;
        _scheduler = repeating ? ThreadPool.scheduleAtFixedRate(new ScheduleTimerTask(), time, time) : ThreadPool.schedule(new ScheduleTimerTask(), time);
    }

    public QuestTimer(Quest quest, String name, long time, Npc npc, Player player) {
        this(quest, name, time, npc, player, false);
    }

    public QuestTimer(QuestState qs, String name, long time) {
        this(qs.getQuest(), name, time, null, qs.getPlayer(), false);
    }

    /**
     * Cancel this quest timer.
     */
    public void cancel() {
        _isActive = false;
        if (_scheduler != null) {
            _scheduler.cancel(false);
        }
    }

    /**
     * Cancel this quest timer and remove it from the associated quest.
     */
    public void cancelAndRemove() {
        cancel();
        _quest.removeQuestTimer(this);
    }

    /**
     * Compares if this timer matches with the key attributes passed.
     *
     * @param quest  the quest to which the timer is attached
     * @param name   the name of the timer
     * @param npc    the NPC attached to the desired timer (null if no NPC attached)
     * @param player the player attached to the desired timer (null if no player attached)
     * @return
     */
    public boolean isMatch(Quest quest, String name, Npc npc, Player player) {
        if ((quest == null) || (name == null)) {
            return false;
        }
        if ((quest != _quest) || !name.equalsIgnoreCase(_name)) {
            return false;
        }
        return ((npc == _npc) && (player == _player));
    }

    public final boolean getIsActive() {
        return _isActive;
    }

    public final boolean getIsRepeating() {
        return _isRepeating;
    }

    public final Quest getQuest() {
        return _quest;
    }

    public final String getName() {
        return _name;
    }

    public final Npc getNpc() {
        return _npc;
    }

    public final Player getPlayer() {
        return _player;
    }

    @Override
    public final String toString() {
        return _name;
    }

    public class ScheduleTimerTask implements Runnable {
        @Override
        public void run() {
            if (!_isActive) {
                return;
            }

            try {
                if (!_isRepeating) {
                    cancelAndRemove();
                }
                _quest.notifyEvent(_name, _npc, _player);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }
}
