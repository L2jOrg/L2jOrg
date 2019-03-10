package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2CommandChannel;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

/**
 * format: (ch) d
 *
 * @author -Wooden-
 */
public final class RequestExAcceptJoinMPCC extends IClientIncomingPacket {
    private int _response;

    @Override
    public void readImpl(ByteBuffer packet) {
        _response = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player != null) {
            final L2PcInstance requestor = player.getActiveRequester();
            SystemMessage sm;
            if (requestor == null) {
                return;
            }

            if (_response == 1) {
                boolean newCc = false;
                if (!requestor.getParty().isInCommandChannel()) {
                    new L2CommandChannel(requestor); // Create new CC
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
