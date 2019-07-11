package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public class NormalCamera extends ServerPacket {
    public static final NormalCamera STATIC_PACKET = new NormalCamera();

    private NormalCamera() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.NORMAL_CAMERA);
    }

}
