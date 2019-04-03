package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

public class ReceiveVipLuckyGameInfo extends IClientOutgoingPacket {

    @Override
    protected void writeImpl(L2GameClient client, ByteBuffer packet) throws Exception {
        OutgoingPackets.RECEIVE_VIP_LUCKY_GAME_INFO.writeId(packet);
        packet.put((byte) 1);
        packet.putInt(2);
        packet.putInt(3);

    }
}
