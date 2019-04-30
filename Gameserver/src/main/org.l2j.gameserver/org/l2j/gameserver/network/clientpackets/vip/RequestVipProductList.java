package org.l2j.gameserver.network.clientpackets.vip;

import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipProductList;

import java.nio.ByteBuffer;

public class RequestVipProductList extends IClientIncomingPacket {

    @Override
    protected void readImpl(ByteBuffer packet) throws Exception {
        // trigger packet
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ReceiveVipProductList());
    }
}
