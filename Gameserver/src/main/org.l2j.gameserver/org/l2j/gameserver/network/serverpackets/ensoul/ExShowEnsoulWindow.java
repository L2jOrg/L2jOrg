package org.l2j.gameserver.network.serverpackets.ensoul;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExShowEnsoulWindow extends ServerPacket {
    public static final ExShowEnsoulWindow STATIC_PACKET = new ExShowEnsoulWindow();

    private ExShowEnsoulWindow() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_ENSOUL_WINDOW);
    }

}
