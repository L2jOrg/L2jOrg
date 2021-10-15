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
package org.l2j.scripts.quests.hunting;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;

import java.util.Collection;
import java.util.Set;

/**
 * @author JoeAlisson
 */
public abstract class MonsterHunting extends HuntingQuest {

    private static final String HUNT_PROGRESS = "hunt_progress";

    public MonsterHunting(int questId, int... huntIds) {
        super(questId, huntIds);
    }

    @Override
    public void onHuntingProgress(Player killer, Npc npc, QuestState qs) {
        var progress = qs.getInt(HUNT_PROGRESS) + 1;
        qs.set(HUNT_PROGRESS, progress);
        playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
        if(progress < huntingAmount(killer, qs)) {
            qs.set(HUNT_PROGRESS, progress);
            sendNpcLogList(killer, huntingLogList(qs));
        } else {
            onCompleteHunting(killer, qs);
            qs.unset(HUNT_PROGRESS);
        }
    }

    protected Collection<NpcLogListHolder> huntingLogList(QuestState qs) {
        return Set.of(new NpcLogListHolder(questHuntingProgressName().getId(), true, qs.getInt(HUNT_PROGRESS)));
    }

    protected abstract NpcStringId questHuntingProgressName();

    protected abstract int huntingAmount(Player killer, QuestState qs);

    protected abstract void onCompleteHunting(Player killer, QuestState qs);
}
