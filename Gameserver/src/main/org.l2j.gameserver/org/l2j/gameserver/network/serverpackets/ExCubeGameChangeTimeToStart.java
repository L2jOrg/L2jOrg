package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author mrTJO
 */
public class ExCubeGameChangeTimeToStart extends ServerPacket {
    int _seconds;

    /**
     * Update Minigame Waiting List Time to Start
     *
     * @param seconds
     */
    public ExCubeGameChangeTimeToStart(int seconds) {
        _seconds = seconds;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_BLOCK_UP_SET_LIST);

        writeInt(0x03);

        writeInt(_seconds);
    }

}
