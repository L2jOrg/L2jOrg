package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 * @author joeAlisson
 */
public class BlockListPacket extends ServerPacket {
    private final IntSet blockedIds;

    public BlockListPacket(IntSet playersId) {
        blockedIds = playersId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.BLOCK_LIST);

        writeInt(blockedIds.size());
        var playerNameTable = PlayerNameTable.getInstance();
        blockedIds.forEach(id -> {
            writeString(playerNameTable.getNameById(id));
            writeString(""); // memo ?
        });
    }

}
