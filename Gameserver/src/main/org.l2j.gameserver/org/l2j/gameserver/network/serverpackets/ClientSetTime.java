package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

@StaticPacket
public class ClientSetTime extends IClientOutgoingPacket {
    public static final ClientSetTime STATIC_PACKET = new ClientSetTime();

    private ClientSetTime() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.CLIENT_SET_TIME.writeId(packet);

        packet.putInt(GameTimeController.getInstance().getGameTime()); // time in client minutes
        packet.putInt(6); // constant to match the server time( this determines the speed of the client clock)
    }

    @Override
    protected int size(L2GameClient client) {
        return 13;
    }
}