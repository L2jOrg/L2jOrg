package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author godson
 */
public class ExOlympiadMode extends ServerPacket {
    private final int _mode;

    /**
     * @param mode (0 = return, 3 = spectate)
     */
    public ExOlympiadMode(int mode) {
        _mode = mode;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_OLYMPIAD_MODE);

        writeByte((byte) _mode);
    }

}
