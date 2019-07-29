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
