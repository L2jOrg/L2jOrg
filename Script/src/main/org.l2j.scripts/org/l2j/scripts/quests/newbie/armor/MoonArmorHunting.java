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
package org.l2j.scripts.quests.newbie.armor;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;

import java.util.Collection;
import java.util.Set;

/**
 * @author JoeAlisson
 */
public abstract class MoonArmorHunting extends MoonArmorRewardQuest {

    private static final String QUEST_HUNT_PROGRESS = "hunt_progress";

    protected MoonArmorHunting(int questId, int startNpc, ClassId... classIds) {
        super(questId, startNpc, classIds);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        var qs = getQuestState(killer, false);
        if (qs != null && qs.isCond(1)) {
            qs.set(QUEST_HUNT_PROGRESS, qs.getInt(QUEST_HUNT_PROGRESS) +1);
            playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
            sendNpcLogList(killer, killCountLogList(qs));
        }
        return super.onKill(npc, killer, isSummon);
    }

    @Override
    public Collection<NpcLogListHolder> getNpcLogList(Player player) {
        var qs = getQuestState(player, false);
        if (qs != null && qs.isCond(1)) {
            return killCountLogList(qs);
        }
        return super.getNpcLogList(player);
    }

    private Collection<NpcLogListHolder> killCountLogList(QuestState qs) {
        return Set.of(new NpcLogListHolder(questHuntingProgressName().getId(), true, qs.getInt(QUEST_HUNT_PROGRESS)));
    }

    @Override
    protected int questHuntProgress(Player player) {
        var qs = getQuestState(player, false);
        return qs != null ? qs.getInt(QUEST_HUNT_PROGRESS) : 0;
    }

    protected abstract NpcStringId questHuntingProgressName();


}
