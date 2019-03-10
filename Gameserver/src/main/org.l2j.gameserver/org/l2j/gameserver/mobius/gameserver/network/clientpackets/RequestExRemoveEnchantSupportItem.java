package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExRemoveEnchantSupportItemResult;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestExRemoveEnchantSupportItem extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

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

        final L2ItemInstance supportItem = request.getSupportItem();
        if ((supportItem == null) || (supportItem.getCount() < 1)) {
            request.setSupportItem(L2PcInstance.ID_NONE);
        }

        request.setTimestamp(System.currentTimeMillis());
        activeChar.sendPacket(ExRemoveEnchantSupportItemResult.STATIC_PACKET);
    }
}
