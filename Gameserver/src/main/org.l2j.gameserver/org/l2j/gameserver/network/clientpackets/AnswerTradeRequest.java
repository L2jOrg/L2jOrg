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
