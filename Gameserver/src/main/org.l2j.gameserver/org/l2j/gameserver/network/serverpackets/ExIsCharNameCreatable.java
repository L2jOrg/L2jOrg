package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author UnAfraid
 */
public class ExIsCharNameCreatable extends ServerPacket {
    private final int _allowed;

    public ExIsCharNameCreatable(int allowed) {
        _allowed = allowed;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CHECK_CHAR_NAME);

        writeInt(_allowed);
    }

}
