package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.PACKAGE_TO_LIST);

        writeInt(_players.size());
        for (Entry<Integer, String> entry : _players.entrySet()) {
            writeInt(entry.getKey());
            writeString(entry.getValue());
        }
    }

}
