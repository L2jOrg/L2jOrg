package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BLOCK_UPSET_LIST);

        writeInt(0x03);

        writeInt(_seconds);
    }

}
