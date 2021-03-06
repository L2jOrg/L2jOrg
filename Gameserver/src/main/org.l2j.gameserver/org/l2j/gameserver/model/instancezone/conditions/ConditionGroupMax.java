/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;
import org.l2j.gameserver.network.SystemMessageId;

import java.util.List;

/**
 * Instance enter group max size
 *
 * @author malyelfik
 */
public final class ConditionGroupMax extends Condition {
    public ConditionGroupMax(InstanceTemplate template, StatsSet parameters, boolean onlyLeader, boolean showMessageAndHtml) {
        super(template, parameters, true, showMessageAndHtml);
        setSystemMessage(SystemMessageId.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
    }

    @Override
    protected boolean test(Player player, Npc npc, List<Player> group) {
        return group.size() <= getLimit();
    }

    public int getLimit() {
        return getParameters().getInt("limit");
    }
}