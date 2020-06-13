package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
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
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        client.sendPacket(EnchantResult.error());
        activeChar.removeRequest(EnchantItemRequest.class);
    }
}
