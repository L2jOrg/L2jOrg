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
package org.l2j.gameserver.network.clientpackets.shuttle;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.world.World;

import static java.util.Objects.isNull;

/**
 * @author UnAfraid
 */
public class RequestShuttleGetOn extends ClientPacket {

    private int x;
    private int y;
    private int z;

    @Override
    public void readImpl() {
        readInt(); // charId
        x = readInt();
        y = readInt();
        z = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (isNull(player)) {
            return;
        }

        World.getInstance().forEachVisibleObjectInRange(player, Shuttle.class, 1000, shuttle -> {
            shuttle.addPassenger(player);
            player.getInVehiclePosition().setXYZ(x, y, z);
        });
    }
}
