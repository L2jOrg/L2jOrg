package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 */
@StaticPacket
public class ExShowVariationCancelWindow extends ServerPacket {
    public static final ExShowVariationCancelWindow STATIC_PACKET = new ExShowVariationCancelWindow();

    private ExShowVariationCancelWindow() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_SHOW_VARIATION_CANCEL_WINDOW);
    }

}
