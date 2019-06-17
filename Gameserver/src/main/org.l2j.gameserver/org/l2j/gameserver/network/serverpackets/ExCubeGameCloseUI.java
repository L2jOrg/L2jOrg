package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_BLOCK_UP_SET_LIST);

        writeInt(0xffffffff);
    }

}
