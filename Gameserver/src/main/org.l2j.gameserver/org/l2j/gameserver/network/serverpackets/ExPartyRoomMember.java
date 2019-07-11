package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.instancemanager.MapRegionManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.matching.PartyMatchingRoom;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Map;
import java.util.Map.Entry;
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_PARTY_ROOM_MEMBER);

        writeInt(_type.ordinal());
        writeInt(_room.getMembersCount());
        for (Player member : _room.getMembers()) {
            writeInt(member.getObjectId());
            writeString(member.getName());
            writeInt(member.getActiveClass());
            writeInt(member.getLevel());
            writeInt(MapRegionManager.getInstance().getBBs(member.getLocation()));
            writeInt(_room.getMemberType(member).ordinal());
            final Map<Integer, Long> _instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(member);
            writeInt(_instanceTimes.size());
            for (Entry<Integer, Long> entry : _instanceTimes.entrySet()) {
                final long instanceTime = TimeUnit.MILLISECONDS.toSeconds(entry.getValue() - System.currentTimeMillis());
                writeInt(entry.getKey());
                writeInt((int) instanceTime);
            }
        }
    }

}