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

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.settings.PartySettings;

import java.util.Collection;

import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * @author JoeAlisson
 */
public abstract class HuntingQuest extends Quest {

    public HuntingQuest(int questId, int... huntIds) {
        super(questId);
        addKillId(huntIds);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        if(processQuestKill(killer, npc)) {
            notifyKillerParty(killer, npc);
        }
        return null;
    }

    private void notifyKillerParty(Player killer, Npc npc) {
        var party = killer.getParty();
        if(party != null) {
            for (var member : party.getMembers()) {
                if(member != killer && isInsideRadius3D(member, npc, PartySettings.partyRange())) {
                    processQuestKill(killer, npc);
                }
            }
        }
    }

    private boolean processQuestKill(Player killer, Npc npc) {
        var qs = getQuestState(killer, false);
        if(qs != null && hasHuntCondition(killer, npc, qs)) {
            onHuntingProgress(killer, npc, qs);
            return true;
        }
        return false;
    }

    @Override
    public Collection<NpcLogListHolder> getNpcLogList(Player player) {
        var qs = getQuestState(player, false);
        if (qs != null && qs.isCond(1)) {
            return huntingLogList(qs);
        }
        return super.getNpcLogList(player);
    }

    public abstract void onHuntingProgress(Player killer, Npc npc, QuestState qs);

    protected abstract Collection<NpcLogListHolder> huntingLogList(QuestState qs);

    protected abstract boolean hasHuntCondition(Player killer, Npc npc, QuestState qs);
}
