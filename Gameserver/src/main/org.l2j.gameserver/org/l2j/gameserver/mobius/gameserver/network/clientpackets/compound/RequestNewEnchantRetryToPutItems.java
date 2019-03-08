package org.l2j.gameserver.mobius.gameserver.network.clientpackets.compound;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.CombinationItemsData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.mobius.gameserver.model.items.combination.CombinationItem;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExEnchantRetryToPutItemFail;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExEnchantRetryToPutItemOk;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestNewEnchantRetryToPutItems extends IClientIncomingPacket
{
    private int _firstItemObjectId;
    private int _secondItemObjectId;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _firstItemObjectId = packet.getInt();
        _secondItemObjectId = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }
        else if (activeChar.isInStoreMode())
        {
            client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            return;
        }
        else if (activeChar.isProcessingTransaction() || activeChar.isProcessingRequest())
        {
            client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            return;
        }

        final CompoundRequest request = new CompoundRequest(activeChar);
        if (!activeChar.addRequest(request))
        {
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            return;
        }

        // Make sure player owns first item.
        request.setItemOne(_firstItemObjectId);
        final L2ItemInstance itemOne = request.getItemOne();
        if (itemOne == null)
        {
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }

        // Make sure player owns second item.
        request.setItemTwo(_secondItemObjectId);
        final L2ItemInstance itemTwo = request.getItemTwo();
        if (itemTwo == null)
        {
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }

        final CombinationItem combinationItem = CombinationItemsData.getInstance().getItemsBySlots(itemOne.getId(), itemTwo.getId());

        // Not implemented or not able to merge!
        if (combinationItem == null)
        {
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }
        client.sendPacket(ExEnchantRetryToPutItemOk.STATIC_PACKET);
    }
}
