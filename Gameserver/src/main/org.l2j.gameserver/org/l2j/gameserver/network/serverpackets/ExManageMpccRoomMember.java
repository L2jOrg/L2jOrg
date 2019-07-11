package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.ExManagePartyRoomMemberType;
import org.l2j.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.instancemanager.MapRegionManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Gnacik
 */
public class ExManageMpccRoomMember extends ServerPacket {
    private final Player _activeChar;
    private final MatchingMemberType _memberType;
    private final ExManagePartyRoomMemberType _type;

    public ExManageMpccRoomMember(Player player, CommandChannelMatchingRoom room, ExManagePartyRoomMemberType mode) {
        _activeChar = player;
        _memberType = room.getMemberType(player);
        _type = mode;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_MANAGE_PARTY_ROOM_MEMBER);

        writeInt(_type.ordinal());
        writeInt(_activeChar.getObjectId());
        writeString(_activeChar.getName());
        writeInt(_activeChar.getClassId().getId());
        writeInt(_activeChar.getLevel());
        writeInt(MapRegionManager.getInstance().getBBs(_activeChar.getLocation()));
        writeInt(_memberType.ordinal());
    }

}
