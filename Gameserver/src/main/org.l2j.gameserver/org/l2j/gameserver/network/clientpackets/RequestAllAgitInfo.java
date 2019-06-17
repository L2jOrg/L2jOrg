package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ExShowAgitInfo;

/**
 * @author KenM
 */
public class RequestAllAgitInfo extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        client.sendPacket(ExShowAgitInfo.STATIC_PACKET);
    }
}
