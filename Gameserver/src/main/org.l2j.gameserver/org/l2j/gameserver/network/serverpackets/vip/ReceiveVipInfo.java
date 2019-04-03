package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

public class ReceiveVipInfo extends IClientOutgoingPacket {

    @Override
    protected void writeImpl(L2GameClient client, ByteBuffer packet) throws Exception {
        OutgoingPackets.RECEIVE_VIP_INFO.writeId(packet);
        var player = client.getActiveChar();
        packet.put((byte) 2); // VIP Current level ( MAX 7 )
        packet.putLong(400); // VIP Current Points
        packet.putInt(18000); // VIP Benefit Duration Seconds
        packet.putLong(1000); // VIP Points to next Level
        packet.putLong(200); // VIP Points used on  30 days period
        packet.put((byte) 1); // VIP Previous Level
        packet.putLong(300); // VIP Current Level Min Point
    }
}
