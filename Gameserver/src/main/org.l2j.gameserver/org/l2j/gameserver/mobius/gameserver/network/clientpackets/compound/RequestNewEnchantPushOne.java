package org.l2j.gameserver.mobius.gameserver.network.clientpackets.compound;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.CombinationItemsData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.mobius.gameserver.model.items.combination.CombinationItem;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.compound.ExEnchantOneOK;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author UnAfraid
 */
public class RequestNewEnchantPushOne extends IClientIncomingPacket {
    private int _objectId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _objectId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        } else if (activeChar.isInStoreMode()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        } else if (activeChar.isProcessingTransaction() || activeChar.isProcessingRequest()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        }

        final CompoundRequest request = new CompoundRequest(activeChar);
        if (!activeChar.addRequest(request)) {
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        }

        // Make sure player owns this item.
        request.setItemOne(_objectId);
        final L2ItemInstance itemOne = request.getItemOne();
        if (itemOne == null) {
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }

        final List<CombinationItem> combinationItems = CombinationItemsData.getInstance().getItemsByFirstSlot(itemOne.getId());

        // Not implemented or not able to merge!
        if (combinationItems.isEmpty()) {
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }

        client.sendPacket(ExEnchantOneOK.STATIC_PACKET);
    }
}
