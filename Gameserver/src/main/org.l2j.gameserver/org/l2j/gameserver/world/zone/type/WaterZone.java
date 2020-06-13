/*
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
package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.NpcInfo;
import org.l2j.gameserver.network.serverpackets.ServerObjectInfo;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isNpc;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class WaterZone extends Zone {
    public WaterZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
        creature.setInsideZone(ZoneType.WATER, true);

        // TODO: update to only send speed status when that packet is known
        if (isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            if (player.checkTransformed(transform -> !transform.canSwim())) {
                creature.stopTransformation(true);
            } else {
                player.broadcastUserInfo();
            }
        } else if (isNpc(creature)) {
            World.getInstance().forEachVisibleObject(creature, Player.class, player ->
            {
                if (creature.getRunSpeed() == 0) {
                    player.sendPacket(new ServerObjectInfo((Npc) creature, player));
                } else {
                    player.sendPacket(new NpcInfo((Npc) creature));
                }
            });
        }
    }

    @Override
    protected void onExit(Creature creature) {
        creature.setInsideZone(ZoneType.WATER, false);

        // TODO: update to only send speed status when that packet is known
        if (isPlayer(creature)) {
            if (!creature.isInsideZone(ZoneType.WATER)) {
                ((Player) creature).stopWaterTask();
            }
            creature.getActingPlayer().broadcastUserInfo();
        } else if (isNpc(creature)) {
            World.getInstance().forEachVisibleObject(creature, Player.class, player ->
            {
                if (creature.getRunSpeed() == 0) {
                    player.sendPacket(new ServerObjectInfo((Npc) creature, player));
                } else {
                    player.sendPacket(new NpcInfo((Npc) creature));
                }
            });
        }
    }

    public int getWaterZ() {
        return getArea().getHighZ();
    }
}
