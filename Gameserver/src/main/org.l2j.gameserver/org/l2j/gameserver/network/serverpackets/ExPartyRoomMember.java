package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.instancemanager.MapRegionManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.matching.PartyMatchingRoom;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * @author Gnacik
 */
public class ExPartyRoomMember extends IClientOutgoingPacket {
    private final PartyMatchingRoom _room;
    private final MatchingMemberType _type;

    public ExPartyRoomMember(L2PcInstance player, PartyMatchingRoom room) {
        _room = room;
        _type = room.getMemberType(player);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PARTY_ROOM_MEMBER.writeId(packet);

        packet.putInt(_type.ordinal());
        packet.putInt(_room.getMembersCount());
        for (L2PcInstance member : _room.getMembers()) {
            packet.putInt(member.getObjectId());
            writeString(member.getName(), packet);
            packet.putInt(member.getActiveClass());
            packet.putInt(member.getLevel());
            packet.putInt(MapRegionManager.getInstance().getBBs(member.getLocation()));
            packet.putInt(_room.getMemberType(member).ordinal());
            final Map<Integer, Long> _instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(member);
            packet.putInt(_instanceTimes.size());
            for (Entry<Integer, Long> entry : _instanceTimes.entrySet()) {
                final long instanceTime = TimeUnit.MILLISECONDS.toSeconds(entry.getValue() - System.currentTimeMillis());
                packet.putInt(entry.getKey());
                packet.putInt((int) instanceTime);
            }
        }
    }
}