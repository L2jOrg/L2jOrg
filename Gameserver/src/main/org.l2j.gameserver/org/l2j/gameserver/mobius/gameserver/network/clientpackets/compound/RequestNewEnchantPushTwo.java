package org.l2j.gameserver.mobius.gameserver.network.clientpackets.compound;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.CombinationItemsData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.mobius.gameserver.model.items.combination.CombinationItem;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.compound.ExEnchantTwoFail;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.compound.ExEnchantTwoOK;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestNewEnchantPushTwo extends IClientIncomingPacket
{
    private int _objectId;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _objectId = packet.getInt();
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
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        }
        else if (activeChar.isProcessingTransaction() || activeChar.isProcessingRequest())
        {
            client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        }

        final CompoundRequest request = activeChar.getRequest(CompoundRequest.class);
        if ((request == null) || request.isProcessing())
        {
            client.sendPacket(ExEnchantTwoFail.STATIC_PACKET);
            return;
        }

        // Make sure player owns this item.
        request.setItemTwo(_objectId);
        final L2ItemInstance itemOne = request.getItemOne();
        final L2ItemInstance itemTwo = request.getItemTwo();
        if ((itemOne == null) || (itemTwo == null))
        {
            client.sendPacket(ExEnchantTwoFail.STATIC_PACKET);
            return;
        }

        // Lets prevent using same item twice
        if (itemOne.getObjectId() == itemTwo.getObjectId())
        {
            client.sendPacket(ExEnchantTwoFail.STATIC_PACKET);
            return;
        }

        final CombinationItem combinationItem = CombinationItemsData.getInstance().getItemsBySlots(itemOne.getId(), itemTwo.getId());

        // Not implemented or not able to merge!
        if (combinationItem == null)
        {
            client.sendPacket(ExEnchantTwoFail.STATIC_PACKET);
            return;
        }

        client.sendPacket(ExEnchantTwoOK.STATIC_PACKET);
    }
}
