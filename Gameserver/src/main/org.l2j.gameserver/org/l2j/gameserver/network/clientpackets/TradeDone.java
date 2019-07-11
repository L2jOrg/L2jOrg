package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

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
        final Player player = client.getActiveChar();
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
            if ((trade.getPartner() == null) || (World.getInstance().getPlayer(trade.getPartner().getObjectId()) == null)) {
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

            if (player.calculateDistance3D(trade.getPartner()) > 150) {
                player.cancelActiveTrade();
                return;
            }
            trade.confirm();
        } else {
            player.cancelActiveTrade();
        }
    }
}
