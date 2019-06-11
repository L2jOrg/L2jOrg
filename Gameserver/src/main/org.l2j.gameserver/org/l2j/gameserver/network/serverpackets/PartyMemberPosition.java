package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.PARTY_MEMBER_POSITION);

        writeInt(locations.size());
        for (Map.Entry<Integer, Location> entry : locations.entrySet()) {
            final Location loc = entry.getValue();
            writeInt(entry.getKey());
            writeInt(loc.getX());
            writeInt(loc.getY());
            writeInt(loc.getZ());
        }
    }

}
