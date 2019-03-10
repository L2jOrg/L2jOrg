package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author -Wooden-
 * @author UnAfraid, mrTJO
 */
public class PackageToList extends IClientOutgoingPacket {
    private final Map<Integer, String> _players;

    public PackageToList(Map<Integer, String> chars) {
        _players = chars;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PACKAGE_TO_LIST.writeId(packet);

        packet.putInt(_players.size());
        for (Entry<Integer, String> entry : _players.entrySet()) {
            packet.putInt(entry.getKey());
            writeString(entry.getValue(), packet);
        }
    }
}
