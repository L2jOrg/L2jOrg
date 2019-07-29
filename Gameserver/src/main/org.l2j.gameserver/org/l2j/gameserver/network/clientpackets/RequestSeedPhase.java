package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExShowSeedMapInfo;

/**
 * RequestSeedPhase client packet
 */
public class RequestSeedPhase extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }
        activeChar.sendPacket(ExShowSeedMapInfo.STATIC_PACKET);
    }
}
