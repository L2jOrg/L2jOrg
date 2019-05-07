package org.l2j.gameserver.network.serverpackets.ensoul;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
@StaticPacket
public class ExShowEnsoulExtractionWindow extends IClientOutgoingPacket {
    public static final ExShowEnsoulExtractionWindow STATIC_PACKET = new ExShowEnsoulExtractionWindow();

    private ExShowEnsoulExtractionWindow() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ENSOUL_EXTRACTION_SHOW.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}