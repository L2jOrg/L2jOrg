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
import org.l2j.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.matching.PartyMatchingRoom;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.world.MapRegionManager;

import java.util.concurrent.TimeUnit;

/**
 * @author Gnacik
 */
public class ExPartyRoomMember extends ServerPacket {
    private final PartyMatchingRoom _room;
    private final MatchingMemberType _type;

    public ExPartyRoomMember(Player player, PartyMatchingRoom room) {
        _room = room;
        _type = room.getMemberType(player);
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_PARTY_ROOM_MEMBER, buffer );

        buffer.writeInt(_type.ordinal());
        buffer.writeInt(_room.getMembersCount());
        for (Player member : _room.getMembers()) {
            buffer.writeInt(member.getObjectId());
            buffer.writeString(member.getName());
            buffer.writeInt(member.getActiveClass());
            buffer.writeInt(member.getLevel());
            buffer.writeInt(MapRegionManager.getInstance().getBBs(member.getLocation()));
            buffer.writeInt(_room.getMemberType(member).ordinal());
            final var _instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(member);
            buffer.writeInt(_instanceTimes.size());
            for (var entry : _instanceTimes.entrySet()) {
                final long instanceTime = TimeUnit.MILLISECONDS.toSeconds(entry.getValue() - System.currentTimeMillis());
                buffer.writeInt(entry.getKey());
                buffer.writeInt((int) instanceTime);
            }
        }
    }

}