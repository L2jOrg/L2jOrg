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
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

public final class TerrainObject extends Npc {
    public TerrainObject(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2TerrainObjectInstance);
    }

    @Override
    public void onAction(Player player, boolean interact) {
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    public void onActionShift(Player player) {
        if (player.isGM()) {
            super.onActionShift(player);
        } else {
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
}