package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zabbix
 */
public class PartyMemberPosition extends ServerPacket {
    private final Map<Integer, Location> locations = new HashMap<>();

    public PartyMemberPosition(L2Party party) {
        reuse(party);
    }

    public void reuse(L2Party party) {
        locations.clear();
        for (Player member : party.getMembers()) {
            if (member == null) {
                continue;
            }
            locations.put(member.getObjectId(), member.getLocation());
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PARTY_MEMBER_POSITION);

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
