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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.network.serverpackets.commission.ExShowCommission;

/**
 * @author NosBit
 */
public class CommissionManagerInstance extends L2Npc {
    public CommissionManagerInstance(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.CommissionManagerInstance);
    }

    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        if (attacker.isMonster()) {
            return true;
        }

        return super.isAutoAttackable(attacker);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (command.equalsIgnoreCase("show_commission")) {
            player.sendPacket(ExShowCommission.STATIC_PACKET);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}
