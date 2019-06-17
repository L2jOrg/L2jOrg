package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author -Wooden-
 * @author UnAfraid, mrTJO
 */
public class PackageToList extends ServerPacket {
    private final Map<Integer, String> _players;

    public PackageToList(Map<Integer, String> chars) {
        _players = chars;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PACKAGE_TO_LIST);

        writeInt(_players.size());
        for (Entry<Integer, String> entry : _players.entrySet()) {
            writeInt(entry.getKey());
            writeString(entry.getValue());
        }
    }

}
