package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2CommandChannel;
import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * @author chris_00
 */
public class ExMultiPartyCommandChannelInfo extends IClientOutgoingPacket {
    private final L2CommandChannel _channel;

    public ExMultiPartyCommandChannelInfo(L2CommandChannel channel) {
        Objects.requireNonNull(channel);
        _channel = channel;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MULTI_PARTY_COMMAND_CHANNEL_INFO.writeId(packet);

        writeString(_channel.getLeader().getName(), packet);
        packet.putInt(0x00); // Channel loot 0 or 1
        packet.putInt(_channel.getMemberCount());

        packet.putInt(_channel.getPartys().size());
        for (L2Party p : _channel.getPartys()) {
            writeString(p.getLeader().getName(), packet);
            packet.putInt(p.getLeaderObjectId());
            packet.putInt(p.getMemberCount());
        }
    }
}
