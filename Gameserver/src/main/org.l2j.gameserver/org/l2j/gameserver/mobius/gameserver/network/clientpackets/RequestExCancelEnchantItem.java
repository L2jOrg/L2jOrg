package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.EnchantResult;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class RequestExCancelEnchantItem extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        client.sendPacket(new EnchantResult(2, 0, 0));
        activeChar.removeRequest(EnchantItemRequest.class);
    }
}
