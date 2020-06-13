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

import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * format: (ch) d
 *
 * @author -Wooden-
 */
public final class RequestExAcceptJoinMPCC extends ClientPacket {
    private int _response;

    @Override
    public void readImpl() {
        _response = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player != null) {
            final Player requestor = player.getActiveRequester();
            SystemMessage sm;
            if (requestor == null) {
                return;
            }

            if (_response == 1) {
                boolean newCc = false;
                if (!requestor.getParty().isInCommandChannel()) {
                    new CommandChannel(requestor); // Create new CC
                    sm = SystemMessage.getSystemMessage(SystemMessageId.THE_COMMAND_CHANNEL_HAS_BEEN_FORMED);
                    requestor.sendPacket(sm);
                    newCc = true;
                }
                requestor.getParty().getCommandChannel().addParty(player.getParty());
                if (!newCc) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_JOINED_THE_COMMAND_CHANNEL);
                    player.sendPacket(sm);
                }
            } else {
                requestor.sendMessage("The player declined to join your Command Channel.");
            }

            player.setActiveRequester(null);
            requestor.onTransactionResponse();
        }
    }
}
