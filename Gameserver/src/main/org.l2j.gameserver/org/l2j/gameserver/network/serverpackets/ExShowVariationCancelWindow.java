package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author KenM
 */
@StaticPacket
public class ExShowVariationCancelWindow extends ServerPacket {
    public static final ExShowVariationCancelWindow STATIC_PACKET = new ExShowVariationCancelWindow();

    private ExShowVariationCancelWindow() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_VARIATION_CANCEL_WINDOW);
    }

}
