package org.l2j.gameserver.network.clientpackets.compound;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

/**
 * @author UnAfraid
 */
public class RequestNewEnchantClose extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        activeChar.removeRequest(CompoundRequest.class);
    }
}
