package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.engine.item.EnchantItemEngine;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.EnchantResult;
import org.l2j.gameserver.util.GameUtils;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public final class RequestEnchantItem extends ClientPacket {

    private int objectId;
    private int supportId;

    @Override
    public void readImpl() {
        objectId = readInt();
        supportId = readInt();
    }

    @Override
    public void runImpl() {
        final var player = client.getPlayer();

        final var request = player.getRequest(EnchantItemRequest.class);
        if (isNull(request) || request.isProcessing()) {
            return;
        }

        request.setEnchantingItem(objectId);
        request.setProcessing(true);

        if (!player.isOnline() || client.isDetached()) {
            player.removeRequest(EnchantItemRequest.class);
            return;
        }

        if (player.isProcessingTransaction() || player.isInStoreMode()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            player.removeRequest(EnchantItemRequest.class);
            return;
        }

        if (request.getTimestamp() == 0 || (System.currentTimeMillis() - request.getTimestamp()) < 2000 ) {
            GameUtils.handleIllegalPlayerAction(player, player + " use auto enchant program");
            player.removeRequest(EnchantItemRequest.class);
            client.sendPacket(EnchantResult.error());
            return;
        }

        final Item item = request.getEnchantingItem();
        final Item scroll = request.getEnchantingScroll();

        if (isNull(item) || isNull(scroll)) {
            player.removeRequest(EnchantItemRequest.class);
            return;
        }

        EnchantItemEngine.getInstance().enchant(player, item, scroll);
        request.setProcessing(false);
    }
}
