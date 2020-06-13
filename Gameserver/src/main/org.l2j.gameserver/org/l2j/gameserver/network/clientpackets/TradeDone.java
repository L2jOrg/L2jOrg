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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;

/**
 * This packet manages the trade response.
 */
public final class TradeDone extends ClientPacket {
    private int _response;

    @Override
    public void readImpl() {
        _response = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("trade")) {
            player.sendMessage("You are trading too fast.");
            return;
        }

        final TradeList trade = player.getActiveTradeList();
        if (trade == null) {
            return;
        }

        if (trade.isLocked()) {
            return;
        }

        if (_response == 1) {
            if ((trade.getPartner() == null) || (World.getInstance().findPlayer(trade.getPartner().getObjectId()) == null)) {
                // Trade partner not found, cancel trade
                player.cancelActiveTrade();
                player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
                return;
            }

            if ((trade.getOwner().hasItemRequest()) || (trade.getPartner().hasItemRequest())) {
                return;
            }

            if (!player.getAccessLevel().allowTransaction()) {
                player.cancelActiveTrade();
                player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }

            if (player.getInstanceWorld() != trade.getPartner().getInstanceWorld()) {
                player.cancelActiveTrade();
                return;
            }

            if (!MathUtil.isInsideRadius3D(player, trade.getPartner(), 150)) {
                player.cancelActiveTrade();
                return;
            }
            trade.confirm();
        } else {
            player.cancelActiveTrade();
        }
    }
}
