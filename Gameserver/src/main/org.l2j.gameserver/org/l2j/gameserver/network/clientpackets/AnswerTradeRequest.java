package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.TradeDone;

/**
 * This class ...
 *
 * @version $Revision: 1.5.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class AnswerTradeRequest extends ClientPacket {
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

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final Player partner = player.getActiveRequester();
        if (partner == null) {
            // Trade partner not found, cancel trade
            player.sendPacket(new TradeDone(0));
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE));
            player.setActiveRequester(null);
            return;
        } else if (World.getInstance().findPlayer(partner.getObjectId()) == null) {
            // Trade partner not found, cancel trade
            player.sendPacket(new TradeDone(0));
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE));
            player.setActiveRequester(null);
            return;
        }

        if ((_response == 1) && !partner.isRequestExpired()) {
            player.startTrade(partner);
        } else {
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE);
            msg.addString(player.getName());
            partner.sendPacket(msg);
        }

        // Clears requesting status
        player.setActiveRequester(null);
        partner.onTransactionResponse();
    }
}
