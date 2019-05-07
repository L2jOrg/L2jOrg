package org.l2j.gameserver.network.serverpackets.ensoul;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExShowEnsoulWindow extends IClientOutgoingPacket {
    public static final ExShowEnsoulWindow STATIC_PACKET = new ExShowEnsoulWindow();

    private ExShowEnsoulWindow() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_ENSOUL_WINDOW.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}
