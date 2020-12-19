package org.l2j.gameserver.network.clientpackets.bless;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.BlessItemRequest;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestBlessOptionCancel extends ClientPacket {
    @Override
    protected void readImpl() throws Exception {

    }

    @Override
    protected void runImpl() throws Exception {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        activeChar.removeRequest(BlessItemRequest.class);
    }
}
