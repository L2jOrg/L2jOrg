package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ExSendManorList;

/**
 * @author l3x
 */
public class RequestManorList extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        client.sendPacket(ExSendManorList.STATIC_PACKET);
    }
}