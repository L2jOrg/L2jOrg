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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.matching.PartyMatchingRoom;
import org.l2j.gameserver.network.serverpackets.PartyRoomInfo;

/**
 * author: Gnacik
 */
public class RequestPartyMatchList extends ClientPacket {
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
        final Player activeChar = client.getPlayer();
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
