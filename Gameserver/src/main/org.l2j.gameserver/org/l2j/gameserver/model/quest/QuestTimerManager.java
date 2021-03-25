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

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author JoeAlisson
 */
public class QuestTimerManager {

    private final Map<Quest, Map<String, Set<QuestTimer>>> timers = new ConcurrentHashMap<>();

    private QuestTimerManager() {

    }

    void schedule(Quest quest, String event, Player player, Npc npc, long time, boolean repeat) {
        var questTimers = timers.computeIfAbsent(quest, q -> new ConcurrentHashMap<>());
        var eventTimers = questTimers.computeIfAbsent(event, e -> ConcurrentHashMap.newKeySet());
        eventTimers.add(new QuestTimer(quest, event, time, npc, player, repeat));
    }


    QuestTimer getQuestTimer(Quest quest, String event, Player player, Npc npc) {
        var questTimers = timers.get(quest);

        if(isNull(questTimers) || questTimers.isEmpty()) {
            return null;
        }

        var timers = questTimers.get(event);
        if(nonNull(timers)) {
            for (QuestTimer timer : timers) {
                if (timer.matches(quest, event, npc, player)) {
                    return timer;
                }
            }
        }
        return null;
    }

    public void cancelQuestTimers(Quest quest) {
        var questTimers = timers.remove(quest);

        if(isNull(questTimers) || questTimers.isEmpty()) {
            return;
        }
        questTimers.values().forEach(this::cancelAndClearTimers);
    }

    public void cancelQuestTimers(Quest quest, String event) {
        var questTimers = timers.get(quest);

        if(isNull(questTimers) || questTimers.isEmpty()) {
            return;
        }

        doIfNonNull(questTimers.remove(event), this::cancelAndClearTimers);
    }

    private void cancelAndClearTimers(Set<QuestTimer> timers) {
        timers.forEach(QuestTimer::cancel);
        timers.clear();
    }

    void removeQuestTimer(QuestTimer questTimer) {
        var questTimers = timers.get(questTimer.getQuest());
        if(isNull(questTimers) || questTimers.isEmpty()) {
            return;
        }

        var timers = questTimers.get(questTimer.getName());
        timers.remove(questTimer);

        if(questTimer.isActive) {
            questTimer.cancel();
        }
    }

    public static QuestTimerManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final QuestTimerManager INSTANCE = new QuestTimerManager();
    }

}
