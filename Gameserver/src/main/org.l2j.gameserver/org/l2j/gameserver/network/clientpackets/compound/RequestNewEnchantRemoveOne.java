package org.l2j.gameserver.network.clientpackets.compound;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneRemoveFail;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneRemoveOK;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestNewEnchantRemoveOne extends IClientIncomingPacket {
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

        final CompoundRequest request = activeChar.getRequest(CompoundRequest.class);
        if ((request == null) || request.isProcessing()) {
            client.sendPacket(ExEnchantOneRemoveFail.STATIC_PACKET);
            return;
        }

        final L2ItemInstance item = request.getItemOne();
        if ((item == null) || (item.getObjectId() != _objectId)) {
            client.sendPacket(ExEnchantOneRemoveFail.STATIC_PACKET);
            return;
        }
        request.setItemOne(0);

        client.sendPacket(ExEnchantOneRemoveOK.STATIC_PACKET);
    }
}
