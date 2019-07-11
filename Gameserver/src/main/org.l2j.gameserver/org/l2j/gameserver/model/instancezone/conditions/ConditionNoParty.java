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

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;

/**
 * Instance no party condition
 *
 * @author St3eT
 */
public final class ConditionNoParty extends Condition {
    public ConditionNoParty(InstanceTemplate template, StatsSet parameters, boolean onlyLeader, boolean showMessageAndHtml) {
        super(template, parameters, true, showMessageAndHtml);
    }

    @Override
    public boolean test(Player player, L2Npc npc) {
        return !player.isInParty();
    }
}
