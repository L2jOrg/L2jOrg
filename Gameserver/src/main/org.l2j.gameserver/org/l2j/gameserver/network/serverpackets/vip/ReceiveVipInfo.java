/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.serverpackets.vip;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ReceiveVipInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        var player = client.getPlayer();
        var vipData = VipEngine.getInstance();
        var vipTier = player.getVipTier();

        var vipDuration = (int) ChronoUnit.SECONDS.between(Instant.now(), Instant.ofEpochMilli(player.getVipTierExpiration()));

        writeId(ServerExPacketId.EX_VIP_INFO, buffer );
        buffer.writeByte(vipTier); // VIP Current level ( MAX 7 )
        buffer.writeLong(player.getVipPoints()); // VIP Current Points
        buffer.writeInt(vipDuration); // VIP Benefit Duration Seconds
        buffer.writeLong(vipData.getPointsToLevel(vipTier + 1)); // VIP Points to next Level
        buffer.writeLong(vipData.getPointsDepreciatedOnLevel(vipTier)); // VIP Points used on  30 days period
        buffer.writeByte(vipTier); // VIP tier
        buffer.writeLong(vipData.getPointsToLevel(vipTier)); // VIP Current Level Requirement Points
    }

}
