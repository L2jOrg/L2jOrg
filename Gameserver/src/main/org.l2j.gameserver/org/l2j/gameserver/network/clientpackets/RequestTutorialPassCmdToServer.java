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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;

public class RequestTutorialPassCmdToServer extends ClientPacket {
    private String bypass = null;

    @Override
    public void readImpl() {
        bypass = readString();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();

        if (bypass.startsWith("admin_")) {
            AdminCommandHandler.getInstance().useAdminCommand(player, bypass, true);
        } else {
            final IBypassHandler handler = BypassHandler.getInstance().getHandler(bypass);
            if (handler != null) {
                handler.useBypass(bypass, player, null);
            }
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerBypass(player, bypass), player);
    }
}
