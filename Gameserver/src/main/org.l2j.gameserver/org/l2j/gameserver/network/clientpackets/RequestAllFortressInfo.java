package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ExShowFortressInfo;

/**
 * @author KenM
 */
public class RequestAllFortressInfo extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        client.sendPacket(ExShowFortressInfo.STATIC_PACKET);
    }
}
