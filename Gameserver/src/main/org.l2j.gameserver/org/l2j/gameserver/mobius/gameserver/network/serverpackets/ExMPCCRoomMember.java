package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.MapRegionManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExMPCCRoomMember extends IClientOutgoingPacket {
    private final CommandChannelMatchingRoom _room;
    private final MatchingMemberType _type;

    public ExMPCCRoomMember(L2PcInstance player, CommandChannelMatchingRoom room) {
        _room = room;
        _type = room.getMemberType(player);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MPCC_ROOM_MEMBER.writeId(packet);

        packet.putInt(_type.ordinal());
        packet.putInt(_room.getMembersCount());
        for (L2PcInstance member : _room.getMembers()) {
            packet.putInt(member.getObjectId());
            writeString(member.getName(), packet);
            packet.putInt(member.getLevel());
            packet.putInt(member.getClassId().getId());
            packet.putInt(MapRegionManager.getInstance().getBBs(member.getLocation()));
            packet.putInt(_room.getMemberType(member).ordinal());
        }
    }
}
