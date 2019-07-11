package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class CSShowComBoard extends ServerPacket {
    private final byte[] _html;

    public CSShowComBoard(byte[] html) {
        _html = html;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SHOW_BOARD);

        writeByte((byte) 0x01); // c4 1 to show community 00 to hide
        writeBytes(_html);
    }

}
