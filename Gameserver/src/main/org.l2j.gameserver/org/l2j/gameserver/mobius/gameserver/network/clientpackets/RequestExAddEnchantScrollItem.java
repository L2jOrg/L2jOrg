package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.EnchantItemData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.mobius.gameserver.model.items.enchant.EnchantScroll;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExPutEnchantScrollItemResult;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestExAddEnchantScrollItem extends IClientIncomingPacket
{
    private int _scrollObjectId;
    private int _enchantObjectId;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _scrollObjectId = packet.getInt();
        _enchantObjectId = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        final EnchantItemRequest request = activeChar.getRequest(EnchantItemRequest.class);
        if ((request == null) || request.isProcessing())
        {
            return;
        }

        request.setEnchantingItem(_enchantObjectId);
        request.setEnchantingScroll(_scrollObjectId);

        final L2ItemInstance item = request.getEnchantingItem();
        final L2ItemInstance scroll = request.getEnchantingScroll();
        if ((item == null) || (scroll == null))
        {
            // message may be custom
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            activeChar.sendPacket(new ExPutEnchantScrollItemResult(0));
            request.setEnchantingItem(L2PcInstance.ID_NONE);
            request.setEnchantingScroll(L2PcInstance.ID_NONE);
            return;
        }

        final EnchantScroll scrollTemplate = EnchantItemData.getInstance().getEnchantScroll(scroll);
        if ((scrollTemplate == null))
        {
            // message may be custom
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            activeChar.sendPacket(new ExPutEnchantScrollItemResult(0));
            request.setEnchantingScroll(L2PcInstance.ID_NONE);
            return;
        }

        request.setTimestamp(System.currentTimeMillis());
        activeChar.sendPacket(new ExPutEnchantScrollItemResult(_scrollObjectId));
    }
}
