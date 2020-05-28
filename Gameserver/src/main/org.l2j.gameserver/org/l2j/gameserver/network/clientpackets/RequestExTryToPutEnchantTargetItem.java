package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.EnchantItemData;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPutEnchantTargetItemResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;

/**
 * @author KenM
 */
public class RequestExTryToPutEnchantTargetItem extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestExTryToPutEnchantTargetItem.class);
    private int objectId;

    @Override
    public void readImpl() {
        objectId = readInt();
    }

    @Override
    public void runImpl() {
        final var player = client.getPlayer();
        final var request = player.getRequest(EnchantItemRequest.class);

        if (isNull(request) || request.isProcessing()) {
            return;
        }

        request.setEnchantingItem(objectId);

        final var item = request.getEnchantingItem();
        final var scroll = request.getEnchantingScroll();

        if (isNull(item) || isNull(scroll)) {
            return;
        }

        final var scrollTemplate = EnchantItemData.getInstance().getEnchantScroll(scroll);

        if (isNull(scrollTemplate) || !scrollTemplate.canEnchant(item)) {
            client.sendPacket(SystemMessageId.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
            player.removeRequest(EnchantItemRequest.class);
            client.sendPacket(new ExPutEnchantTargetItemResult(0));
            if (scrollTemplate == null) {
                LOGGER.warn("Undefined scroll have been used id: {}", scroll.getId());
            }
        } else {
            request.setTimestamp(System.currentTimeMillis());
            client.sendPacket(new ExPutEnchantTargetItemResult(objectId));
        }
    }
}
