package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipInfo;

import java.nio.ByteBuffer;

public class ExRequestVipInfo extends IClientIncomingPacket {

    @Override
    protected void readImpl(ByteBuffer packet) throws Exception {
        // just trigger packet
    }

    @Override
    protected void runImpl() throws Exception {
        client.sendPacket(new ReceiveVipInfo());
    }
}
