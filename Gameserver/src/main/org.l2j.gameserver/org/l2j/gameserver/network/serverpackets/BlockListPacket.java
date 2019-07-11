package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

/**
 * @author Sdw
 */
public class BlockListPacket extends ServerPacket {
    private final List<Integer> _playersId;

    public BlockListPacket(List<Integer> playersId) {
        _playersId = playersId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.BLOCK_LIST);

        writeInt(_playersId.size());
        for (int playerId : _playersId) {
            writeString(CharNameTable.getInstance().getNameById(playerId));
            writeString(""); // memo ?
        }
    }

}
