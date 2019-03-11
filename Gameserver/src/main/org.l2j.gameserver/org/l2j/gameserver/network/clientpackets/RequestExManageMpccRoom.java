package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExMPCCRoomInfo;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestExManageMpccRoom extends IClientIncomingPacket {
    private int _roomId;
    private int _maxMembers;
    private int _minLevel;
    private int _maxLevel;
    private String _title;

    @Override
    public void readImpl(ByteBuffer packet) {
        _roomId = packet.getInt();
        _maxMembers = packet.getInt();
        _minLevel = packet.getInt();
        _maxLevel = packet.getInt();
        packet.getInt(); // Party Distrubtion Type
        _title = readString(packet);
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final MatchingRoom room = activeChar.getMatchingRoom();
        if ((room == null) || (room.getId() != _roomId) || (room.getRoomType() != MatchingRoomType.COMMAND_CHANNEL) || (room.getLeader() != activeChar)) {
            return;
        }

        room.setTitle(_title);
        room.setMaxMembers(_maxMembers);
        room.setMinLvl(_minLevel);
        room.setMaxLvl(_maxLevel);

        room.getMembers().forEach(p -> p.sendPacket(new ExMPCCRoomInfo((CommandChannelMatchingRoom) room)));

        activeChar.sendPacket(SystemMessageId.THE_COMMAND_CHANNEL_MATCHING_ROOM_INFORMATION_WAS_EDITED);
    }
}
