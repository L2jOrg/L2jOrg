package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zabbix
 */
public class PartyMemberPosition extends IClientOutgoingPacket {
    private final Map<Integer, Location> locations = new HashMap<>();

    public PartyMemberPosition(L2Party party) {
        reuse(party);
    }

    public void reuse(L2Party party) {
        locations.clear();
        for (L2PcInstance member : party.getMembers()) {
            if (member == null) {
                continue;
            }
            locations.put(member.getObjectId(), member.getLocation());
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PARTY_MEMBER_POSITION.writeId(packet);

        packet.putInt(locations.size());
        for (Map.Entry<Integer, Location> entry : locations.entrySet()) {
            final Location loc = entry.getValue();
            packet.putInt(entry.getKey());
            packet.putInt(loc.getX());
            packet.putInt(loc.getY());
            packet.putInt(loc.getZ());
        }
    }
}
