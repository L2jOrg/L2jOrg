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
        var player = client.getActiveChar();
        var vipData = VipData.getInstance();
        var vipTier = player.getVipTier();

        var vipDuration = (int) ChronoUnit.SECONDS.between(Instant.now(), Instant.ofEpochMilli(client.getVipTierExpiration()));

        OutgoingPackets.RECEIVE_VIP_INFO.writeId(packet);
        packet.put(vipTier); // VIP Current level ( MAX 7 )
        packet.putLong(client.getVipPoints()); // VIP Current Points
        packet.putInt(vipDuration); // VIP Benefit Duration Seconds
        packet.putLong(vipData.getPointsToLevel(vipTier + 1)); // VIP Points to next Level
        packet.putLong(vipData.getPointsDepreciatedOnLevel(vipTier)); // VIP Points used on  30 days period
        packet.put(vipTier); // VIP tier
        packet.putLong(vipData.getPointsToLevel(vipTier)); // VIP Current Level Requirement Points
    }

    @Override
    protected int size(L2GameClient client) {
        return 44;
    }
}
