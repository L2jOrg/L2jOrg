package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.EnchantItemData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.model.items.enchant.EnchantScroll;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPutEnchantSupportItemResult;

/**
 * @author KenM
 */
public class RequestExTryToPutEnchantSupportItem extends ClientPacket {
    private int _supportObjectId;
    private int _enchantObjectId;

    @Override
    public void readImpl() {
        _supportObjectId = readInt();
        _enchantObjectId = readInt();
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

        request.setEnchantingItem(_enchantObjectId);
        request.setSupportItem(_supportObjectId);

        final Item item = request.getEnchantingItem();
        final Item scroll = request.getEnchantingScroll();
        final Item support = request.getSupportItem();
        if ((item == null) || (scroll == null) || (support == null)) {
            // message may be custom
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            request.setEnchantingItem(Player.ID_NONE);
            request.setSupportItem(Player.ID_NONE);
            return;
        }

        final EnchantScroll scrollTemplate = EnchantItemData.getInstance().getEnchantScroll(scroll);
        if ((scrollTemplate == null) || !scrollTemplate.isValid(item)) {
            // message may be custom
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            request.setSupportItem(Player.ID_NONE);
            activeChar.sendPacket(new ExPutEnchantSupportItemResult(0));
            return;
        }

        request.setSupportItem(support.getObjectId());
        request.setTimestamp(System.currentTimeMillis());
        activeChar.sendPacket(new ExPutEnchantSupportItemResult(_supportObjectId));
    }
}
