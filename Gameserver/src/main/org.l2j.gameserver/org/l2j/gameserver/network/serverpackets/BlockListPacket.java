package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Sdw
 */
public class BlockListPacket extends IClientOutgoingPacket {
    private final List<Integer> _playersId;

    public BlockListPacket(List<Integer> playersId) {
        _playersId = playersId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.BLOCK_LIST.writeId(packet);

        packet.putInt(_playersId.size());
        for (int playerId : _playersId) {
            writeString(CharNameTable.getInstance().getNameById(playerId), packet);
            writeString("", packet); // memo ?
        }
    }
}
