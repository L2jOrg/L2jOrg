package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author joeAlisson
 */
@StaticPacket
public class ExShowTeleportUi extends ServerPacket {

    public static final ExShowTeleportUi OPEN = new ExShowTeleportUi();

    private ExShowTeleportUi() {
        // static
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_TELEPORT_UI);
    }
}
