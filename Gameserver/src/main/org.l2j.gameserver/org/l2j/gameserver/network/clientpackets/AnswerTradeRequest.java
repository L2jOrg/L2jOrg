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

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.TradeDone;
import org.l2j.gameserver.world.World;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public final class AnswerTradeRequest extends ClientPacket {
    private int response;

    @Override
    public void readImpl() {
        response = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (isNull(player)) {
            return;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final Player partner = player.getActiveRequester();

        if (isNull(partner) || isNull(World.getInstance().findPlayer(partner.getObjectId()))) {
            // Trade partner not found, cancel trade
            player.sendPacket(TradeDone.CANCELLED);
            player.sendPacket(getSystemMessage(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE));
            player.setActiveRequester(null);
            return;
        }

        if (response == 1 && !partner.isRequestExpired()) {
            player.startTrade(partner);
        } else {
            partner.sendPacket(getSystemMessage(SystemMessageId.C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE).addString(player.getName()));
        }

        // Clears requesting status
        player.setActiveRequester(null);
        partner.onTransactionResponse();
    }
}
