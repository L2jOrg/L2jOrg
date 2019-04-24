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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BR_GAME_POINT.writeId(packet);

        var player = client.getActiveChar();
        packet.putInt(player.getObjectId());
        packet.putLong(client.getCoin());
        packet.putInt(0x00);
    }
}
