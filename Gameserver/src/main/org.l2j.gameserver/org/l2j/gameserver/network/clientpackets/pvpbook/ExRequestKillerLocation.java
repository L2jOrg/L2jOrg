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
package org.l2j.gameserver.network.clientpackets.pvpbook;

import org.l2j.gameserver.data.database.data.PlayerVariableData;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.pvpbook.ExKillerLocation;
import org.l2j.gameserver.world.World;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.SystemMessageId.CANNOT_LOCATE_THE_SELECTED_FOE_THE_FOE_IS_NOT_ONLINE;

/**
 * @author JoeAlisson
 */
public class ExRequestKillerLocation extends ClientPacket {

    private String killerName;

    @Override
    protected void readImpl() throws Exception {
        killerName = readSizedString();
    }

    @Override
    protected void runImpl() {
        var killer = World.getInstance().findPlayer(killerName);
        if(isNull(killer)) {
            client.sendPacket(CANNOT_LOCATE_THE_SELECTED_FOE_THE_FOE_IS_NOT_ONLINE);
        } else {
            var player = client.getPlayer();
            if(player.getRevengeUsableLocation() > 0 && (player.getRevengeUsableLocation() == PlayerVariableData.REVENGE_USABLE_FUNCTIONS || player.reduceAdena("Killer Location",50000, player, true))) {
                client.sendPacket(new ExKillerLocation(killer));
                player.useRevengeLocation();
            }
        }
    }
}
