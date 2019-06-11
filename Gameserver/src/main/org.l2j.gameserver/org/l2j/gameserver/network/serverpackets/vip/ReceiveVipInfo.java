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
    protected void writeImpl(L2GameClient client) {
        var player = client.getActiveChar();
        var vipData = VipData.getInstance();
        var vipTier = player.getVipTier();

        var vipDuration = (int) ChronoUnit.SECONDS.between(Instant.now(), Instant.ofEpochMilli(client.getVipTierExpiration()));

        writeId(OutgoingPackets.RECEIVE_VIP_INFO);
        writeByte(vipTier); // VIP Current level ( MAX 7 )
        writeLong(client.getVipPoints()); // VIP Current Points
        writeInt(vipDuration); // VIP Benefit Duration Seconds
        writeLong(vipData.getPointsToLevel(vipTier + 1)); // VIP Points to next Level
        writeLong(vipData.getPointsDepreciatedOnLevel(vipTier)); // VIP Points used on  30 days period
        writeByte(vipTier); // VIP tier
        writeLong(vipData.getPointsToLevel(vipTier)); // VIP Current Level Requirement Points
    }

}
