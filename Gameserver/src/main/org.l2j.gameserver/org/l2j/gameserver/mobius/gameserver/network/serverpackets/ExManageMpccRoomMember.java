package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.enums.ExManagePartyRoomMemberType;
import org.l2j.gameserver.mobius.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.MapRegionManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Gnacik
 */
public class ExManageMpccRoomMember extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private final MatchingMemberType _memberType;
    private final ExManagePartyRoomMemberType _type;

    public ExManageMpccRoomMember(L2PcInstance player, CommandChannelMatchingRoom room, ExManagePartyRoomMemberType mode) {
        _activeChar = player;
        _memberType = room.getMemberType(player);
        _type = mode;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MANAGE_PARTY_ROOM_MEMBER.writeId(packet);

        packet.putInt(_type.ordinal());
        packet.putInt(_activeChar.getObjectId());
        writeString(_activeChar.getName(), packet);
        packet.putInt(_activeChar.getClassId().getId());
        packet.putInt(_activeChar.getLevel());
        packet.putInt(MapRegionManager.getInstance().getBBs(_activeChar.getLocation()));
        packet.putInt(_memberType.ordinal());
    }
}
