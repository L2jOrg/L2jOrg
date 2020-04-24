package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.world.WorldTimeController;

@StaticPacket
public class ClientSetTime extends ServerPacket {
    public static final ClientSetTime STATIC_PACKET = new ClientSetTime();

    private ClientSetTime() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CLIENT_SETTIME);

        writeInt(WorldTimeController.getInstance().getGameTime()); // time in client minutes
        writeInt(6); // constant to match the server time( this determines the speed of the client clock)
    }

}