package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.network.serverpackets.EnchantResult;

/**
 * @author KenM
 */
public class RequestExCancelEnchantItem extends ClientPacket {
    @Override
    public void readImpl() {

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
