package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ExShowCastleInfo;

/**
 * @author KenM
 */
public class RequestAllCastleInfo extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        client.sendPacket(ExShowCastleInfo.STATIC_PACKET);
    }
}
