package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.EnchantItemData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.model.items.enchant.EnchantScroll;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPutEnchantTargetItemResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KenM
 */
public class RequestExTryToPutEnchantTargetItem extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestExTryToPutEnchantTargetItem.class);
    private int _objectId;

    @Override
    public void readImpl() {
        _objectId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final EnchantItemRequest request = activeChar.getRequest(EnchantItemRequest.class);
        if ((request == null) || request.isProcessing()) {
            return;
        }

        request.setEnchantingItem(_objectId);

        final Item item = request.getEnchantingItem();
        final Item scroll = request.getEnchantingScroll();
        if ((item == null) || (scroll == null)) {
            return;
        }

        final EnchantScroll scrollTemplate = EnchantItemData.getInstance().getEnchantScroll(scroll);
        if ((scrollTemplate == null) || !scrollTemplate.isValid(item)) {
            client.sendPacket(SystemMessageId.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
            activeChar.removeRequest(request.getClass());
            client.sendPacket(new ExPutEnchantTargetItemResult(0));
            if (scrollTemplate == null) {
                LOGGER.warn("Undefined scroll have been used id: {}", scroll.getId());
            }
            return;
        }
        request.setTimestamp(System.currentTimeMillis());
        client.sendPacket(new ExPutEnchantTargetItemResult(_objectId));
    }
}
