package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * Close Minigame Waiting List
 *
 * @author mrTJO
 */
public class ExCubeGameCloseUI extends IClientOutgoingPacket {
    public static final ExCubeGameCloseUI STATIC_PACKET = new ExCubeGameCloseUI();

    private ExCubeGameCloseUI() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BLOCK_UP_SET_LIST.writeId(packet);

        packet.putInt(0xffffffff);
    }
}
