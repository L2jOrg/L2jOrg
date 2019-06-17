package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public class ClientSetTime extends ServerPacket {
    public static final ClientSetTime STATIC_PACKET = new ClientSetTime();

    private ClientSetTime() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.CLIENT_SET_TIME);

        writeInt(GameTimeController.getInstance().getGameTime()); // time in client minutes
        writeInt(6); // constant to match the server time( this determines the speed of the client clock)
    }

}