package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.matching.PartyMatchingRoom;
import org.l2j.gameserver.network.serverpackets.PartyRoomInfo;

import java.nio.ByteBuffer;

/**
 * author: Gnacik
 */
public class RequestPartyMatchList extends IClientIncomingPacket {
    private int _roomId;
    private int _maxMembers;
    private int _minLevel;
    private int _maxLevel;
    private int _lootType;
    private String _roomTitle;

    @Override
    public void readImpl() {
        _roomId = readInt();
        _maxMembers = readInt();
        _minLevel = readInt();
        _maxLevel = readInt();
        _lootType = readInt();
        _roomTitle = readString();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if ((_roomId <= 0) && (activeChar.getMatchingRoom() == null)) {
            final PartyMatchingRoom room = new PartyMatchingRoom(_roomTitle, _lootType, _minLevel, _maxLevel, _maxMembers, activeChar);
            activeChar.setMatchingRoom(room);
        } else {
            final MatchingRoom room = activeChar.getMatchingRoom();
            if ((room.getId() == _roomId) && (room.getRoomType() == MatchingRoomType.PARTY) && room.isLeader(activeChar)) {
                room.setLootType(_lootType);
                room.setMinLvl(_minLevel);
                room.setMaxLvl(_maxLevel);
                room.setMaxMembers(_maxMembers);
                room.setTitle(_roomTitle);

                final PartyRoomInfo packet = new PartyRoomInfo((PartyMatchingRoom) room);
                room.getMembers().forEach(packet::sendTo);
            }
        }
    }

}
