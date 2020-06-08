package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.engine.item.EnchantItemEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPutEnchantScrollItemResult;

/**
 * @author Sdw
 */
public class RequestExAddEnchantScrollItem extends ClientPacket {
    private int _scrollObjectId;
    private int _enchantObjectId;

    @Override
    public void readImpl() {
        _scrollObjectId = readInt();
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
        request.setEnchantingScroll(_scrollObjectId);

        final Item item = request.getEnchantingItem();
        final Item scroll = request.getEnchantingScroll();
        if ((item == null) || (scroll == null)) {
            // message may be custom
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            activeChar.sendPacket(new ExPutEnchantScrollItemResult(0));
            request.setEnchantingItem(Player.ID_NONE);
            request.setEnchantingScroll(Player.ID_NONE);
            return;
        }

        if (!EnchantItemEngine.getInstance().existsScroll(scroll)) {
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            activeChar.sendPacket(new ExPutEnchantScrollItemResult(0));
            request.setEnchantingScroll(Player.ID_NONE);
            return;
        }

        request.setTimestamp(System.currentTimeMillis());
        activeChar.sendPacket(new ExPutEnchantScrollItemResult(_scrollObjectId));
    }
}
