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
package org.l2j.gameserver.model.quest;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class QuestTimer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestTimer.class);

    private final String name;
    private final Quest quest;
    private final Npc npc;
    private final Player player;
    private final boolean repeating;
    private final ScheduledFuture<?> scheduler;
    boolean isActive = true;

    QuestTimer(Quest quest, String name, long time, Npc npc, Player player, boolean repeating) {
        this.name = name;
        this.quest = quest;
        this.player = player;
        this.npc = npc;
        this.repeating = repeating;
        scheduler = repeating ? ThreadPool.scheduleAtFixedRate(this, time, time) : ThreadPool.schedule(this, time);
    }

    @Override
    public void run() {
        if (!isActive) {
            return;
        }

        try {
            if (!repeating) {
                cancelAndRemove();
            }

            if(nonNull(player) && !player.isOnline() && isActive) {
               cancelAndRemove();
            } else {
                quest.notifyEvent(name, npc, player);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    void cancel() {
        isActive = false;
        if (nonNull(scheduler)) {
            scheduler.cancel(false);
        }
    }

    /**
     * Cancel this quest timer and remove it from the associated quest.
     */
    public void cancelAndRemove() {
        cancel();
        QuestTimerManager.getInstance().removeQuestTimer(this);
    }

    /**
     * Compares if this timer matches with the key attributes passed.
     *
     * @param quest  the quest to which the timer is attached
     * @param name   the name of the timer
     * @param npc    the NPC attached to the desired timer (null if no NPC attached)
     * @param player the player attached to the desired timer (null if no player attached)
     * @return if matches
     */
    public boolean matches(Quest quest, String name, Npc npc, Player player) {
        if (isNull(quest) || isNull(name) || quest != this.quest || !name.equalsIgnoreCase(this.name)) {
            return false;
        }
        return npc == this.npc && player == this.player;
    }

    public final Quest getQuest() {
        return quest;
    }

    public final String getName() {
        return name;
    }

    @Override
    public final String toString() {
        return name;
    }
}
