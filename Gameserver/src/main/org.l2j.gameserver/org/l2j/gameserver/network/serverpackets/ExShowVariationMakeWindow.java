package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * Trigger packet
 *
 * @author KenM
 */
@StaticPacket
public class ExShowVariationMakeWindow extends ServerPacket {
    public static final ExShowVariationMakeWindow STATIC_PACKET = new ExShowVariationMakeWindow();

    private ExShowVariationMakeWindow() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SHOW_VARIATION_MAKE_WINDOW);

    }

}
