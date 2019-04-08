package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.data.xml.impl.VipData;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.lang.Math.max;

public class ReceiveVipInfo extends IClientOutgoingPacket {

    @Override
    protected void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.RECEIVE_VIP_INFO.writeId(packet);
        var player = client.getActiveChar();
        var vipData = VipData.getInstance();
        var vipTier = vipData.getVipLevel(player);

        var vipDuration = (int) ChronoUnit.SECONDS.between(Instant.now(), Instant.ofEpochMilli(player.getVipExpiration()));

        packet.put(vipTier); // VIP Current level ( MAX 7 )
        packet.putLong(player.getVipPoints()); // VIP Current Points
        packet.putInt(vipDuration); // VIP Benefit Duration Seconds
        packet.putLong(vipData.getPointsToLevel(vipTier + 1)); // VIP Points to next Level
        packet.putLong(vipData.getPointsDepreciatedOnLevel(vipTier)); // VIP Points used on  30 days period
        packet.put((byte) max(0, vipTier -1)); // VIP Previous Level
        packet.putLong(vipData.getPointsToLevel(vipTier)); // VIP Current Level Requirement Points
    }
}
