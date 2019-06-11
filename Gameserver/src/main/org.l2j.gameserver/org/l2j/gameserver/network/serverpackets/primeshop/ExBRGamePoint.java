package org.l2j.gameserver.network.serverpackets.primeshop;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Gnacik, UnAfraid
 */
public class ExBRGamePoint extends IClientOutgoingPacket {

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_BR_GAME_POINT);

        var player = client.getActiveChar();
        writeInt(player.getObjectId());
        writeLong(client.getCoin());
        writeInt(0x00);
    }

}
