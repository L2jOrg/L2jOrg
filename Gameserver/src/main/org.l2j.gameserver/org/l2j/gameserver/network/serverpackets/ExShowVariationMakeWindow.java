package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * Trigger packet
 *
 * @author KenM
 */
@StaticPacket
public class ExShowVariationMakeWindow extends IClientOutgoingPacket {
    public static final ExShowVariationMakeWindow STATIC_PACKET = new ExShowVariationMakeWindow();

    private ExShowVariationMakeWindow() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_SHOW_VARIATION_MAKE_WINDOW);

    }

}
