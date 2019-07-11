package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * Close Minigame Waiting List
 *
 * @author mrTJO
 */
public class ExCubeGameCloseUI extends ServerPacket {
    public static final ExCubeGameCloseUI STATIC_PACKET = new ExCubeGameCloseUI();

    private ExCubeGameCloseUI() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_BLOCK_UP_SET_LIST);

        writeInt(0xffffffff);
    }

}
