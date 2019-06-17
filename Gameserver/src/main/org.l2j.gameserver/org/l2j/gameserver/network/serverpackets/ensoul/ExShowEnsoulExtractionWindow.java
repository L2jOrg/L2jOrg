package org.l2j.gameserver.network.serverpackets.ensoul;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
@StaticPacket
public class ExShowEnsoulExtractionWindow extends ServerPacket {
    public static final ExShowEnsoulExtractionWindow STATIC_PACKET = new ExShowEnsoulExtractionWindow();

    private ExShowEnsoulExtractionWindow() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_ENSOUL_EXTRACTION_SHOW);
    }

}