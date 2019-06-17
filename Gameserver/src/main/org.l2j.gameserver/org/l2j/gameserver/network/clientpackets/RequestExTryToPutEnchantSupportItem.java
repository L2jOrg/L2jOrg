package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.EnchantItemData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.model.items.enchant.EnchantScroll;
import org.l2j.gameserver.model.items.enchant.EnchantSupportItem;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
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
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final EnchantItemRequest request = activeChar.getRequest(EnchantItemRequest.class);
        if ((request == null) || request.isProcessing()) {
            return;
        }

        request.setEnchantingItem(_enchantObjectId);
        request.setSupportItem(_supportObjectId);

        final L2ItemInstance item = request.getEnchantingItem();
        final L2ItemInstance scroll = request.getEnchantingScroll();
        final L2ItemInstance support = request.getSupportItem();
        if ((item == null) || (scroll == null) || (support == null)) {
            // message may be custom
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            request.setEnchantingItem(L2PcInstance.ID_NONE);
            request.setSupportItem(L2PcInstance.ID_NONE);
            return;
        }

        final EnchantScroll scrollTemplate = EnchantItemData.getInstance().getEnchantScroll(scroll);
        final EnchantSupportItem supportTemplate = EnchantItemData.getInstance().getSupportItem(support);
        if ((scrollTemplate == null) || (supportTemplate == null) || !scrollTemplate.isValid(item, supportTemplate)) {
            // message may be custom
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            request.setSupportItem(L2PcInstance.ID_NONE);
            activeChar.sendPacket(new ExPutEnchantSupportItemResult(0));
            return;
        }

        request.setSupportItem(support.getObjectId());
        request.setTimestamp(System.currentTimeMillis());
        activeChar.sendPacket(new ExPutEnchantSupportItemResult(_supportObjectId));
    }
}
