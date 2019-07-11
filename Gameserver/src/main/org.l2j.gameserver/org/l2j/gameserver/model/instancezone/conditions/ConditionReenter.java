/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Instance reenter conditions
 *
 * @author malyelfik
 */
public final class ConditionReenter extends Condition {

    public ConditionReenter(InstanceTemplate template, StatsSet parameters, boolean onlyLeader, boolean showMessageAndHtml) {
        super(template, parameters, onlyLeader, showMessageAndHtml);
        setSystemMessage(SystemMessageId.C1_MAY_NOT_RE_ENTER_YET, (message, player) -> message.addString(player.getName()));
    }

    @Override
    protected boolean test(Player player, Npc npc) {
        final int instanceId = getParameters().getInt("instanceId", getInstanceTemplate().getId());
        return System.currentTimeMillis() > InstanceManager.getInstance().getInstanceTime(player, instanceId);
    }
}