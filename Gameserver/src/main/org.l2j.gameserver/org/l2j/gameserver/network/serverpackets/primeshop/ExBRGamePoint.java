package org.l2j.gameserver.network.serverpackets.primeshop;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Gnacik, UnAfraid
 */
public class ExBRGamePoint extends ServerPacket {

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_BR_GAME_POINT);

        var player = client.getPlayer();
        writeInt(player.getObjectId());
        writeLong(client.getCoin());
        writeInt(0x00);
    }

}
