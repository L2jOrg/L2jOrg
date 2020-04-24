package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * Show Confirm Dialog for 10 seconds
 *
 * @author mrTJO
 */
public class ExCubeGameRequestReady extends ServerPacket {
    public static final ExCubeGameRequestReady STATIC_PACKET = new ExCubeGameRequestReady();

    private ExCubeGameRequestReady() {

    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BLOCK_UPSET_LIST);

        writeInt(0x04);
    }

}
