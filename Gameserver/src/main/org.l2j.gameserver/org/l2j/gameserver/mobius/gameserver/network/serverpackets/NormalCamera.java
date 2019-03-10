package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

@StaticPacket
public class NormalCamera extends IClientOutgoingPacket {
    public static final NormalCamera STATIC_PACKET = new NormalCamera();

    private NormalCamera() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.NORMAL_CAMERA.writeId(packet);
    }
}
