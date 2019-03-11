package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
@StaticPacket
public class ExShowVariationCancelWindow extends IClientOutgoingPacket {
    public static final ExShowVariationCancelWindow STATIC_PACKET = new ExShowVariationCancelWindow();

    private ExShowVariationCancelWindow() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_VARIATION_CANCEL_WINDOW.writeId(packet);
    }
}
