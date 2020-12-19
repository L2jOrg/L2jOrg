package org.l2j.gameserver.network.clientpackets.bless;

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.BlessItemRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.bless.ExBlessOptionEnchant;
import org.l2j.gameserver.network.serverpackets.bless.ExBlessOptionPutItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestBlessOptionPutItem extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBlessOptionPutItem.class);
    private int _objectId;

    @Override
    protected void readImpl() throws Exception {
        _objectId = readInt();

    }
    @Override
    protected void runImpl() throws Exception {
        final Player player = client.getPlayer();

        if (player == null) {
            return;
        } else if (player.isInStoreMode()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            return;
        } else if (player.isProcessingTransaction() || player.isProcessingRequest()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
            return;
        }

        final BlessItemRequest request = player.getRequest(BlessItemRequest.class);
        if ((request == null) || request.isProcessing()) {
            return;
        }

        // Make sure player owns this item.
        request.setItem(_objectId);
        final Item itemOne = request.getItem();
        if (itemOne == null) {
            player.removeRequest(request.getClass());
            return;
        }

        if (itemOne.getIsBlessed() == 1) {
            player.removeRequest(request.getClass());
            return;
        }

        client.sendPacket(ExBlessOptionPutItem.STATIC_PACKET);
    }

}
