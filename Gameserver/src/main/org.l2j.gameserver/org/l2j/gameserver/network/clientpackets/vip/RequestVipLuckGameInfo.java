package org.l2j.gameserver.network.clientpackets.vip;

import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipLuckyGameInfo;

import java.nio.ByteBuffer;

public class RequestVipLuckGameInfo  extends IClientIncomingPacket {
    @Override
    protected void readImpl(ByteBuffer packet) throws Exception {
        // trigger packet
    }

    @Override
    protected void runImpl() throws Exception {
        client.sendPacket(new ReceiveVipLuckyGameInfo());
    }
}
