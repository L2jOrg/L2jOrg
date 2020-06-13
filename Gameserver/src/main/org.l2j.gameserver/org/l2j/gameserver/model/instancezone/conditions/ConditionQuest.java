/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Instance quest condition
 *
 * @author malyelfik
 */
public final class ConditionQuest extends Condition {
    public ConditionQuest(InstanceTemplate template, StatsSet parameters, boolean onlyLeader, boolean showMessageAndHtml) {
        super(template, parameters, onlyLeader, showMessageAndHtml);
        // Set message
        setSystemMessage(SystemMessageId.C1_S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED, (message, player) -> message.addString(player.getName()));
    }

    @Override
    protected boolean test(Player player, Npc npc) {
        final int id = getParameters().getInt("id");
        final Quest q = QuestManager.getInstance().getQuest(id);
        if (q == null) {
            return false;
        }

        final QuestState qs = player.getQuestState(q.getName());
        if (qs == null) {
            return false;
        }

        final int cond = getParameters().getInt("cond", -1);
        return (cond != -1) ? qs.isCond(cond) : true;
    }
}
