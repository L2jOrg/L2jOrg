package org.l2j.gameserver.network.clientpackets.vip;

import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipInfo;

import java.nio.ByteBuffer;

public class ExRequestVipInfo extends IClientIncomingPacket {

    @Override
    protected void readImpl() throws Exception {
        // just trigger packet
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ReceiveVipInfo());
    }
}
