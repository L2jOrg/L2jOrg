/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Objects;

/**
 * @author chris_00
 */
public class ExMultiPartyCommandChannelInfo extends ServerPacket {
    private final CommandChannel _channel;

    public ExMultiPartyCommandChannelInfo(CommandChannel channel) {
        Objects.requireNonNull(channel);
        _channel = channel;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_MULTI_PARTY_COMMAND_CHANNEL_INFO, buffer );

        buffer.writeString(_channel.getLeader().getName());
        buffer.writeInt(0x00); // Channel loot 0 or 1
        buffer.writeInt(_channel.getMemberCount());

        buffer.writeInt(_channel.getPartys().size());
        for (Party p : _channel.getPartys()) {
            buffer.writeString(p.getLeader().getName());
            buffer.writeInt(p.getLeaderObjectId());
            buffer.writeInt(p.getMemberCount());
        }
    }

}
