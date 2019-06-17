package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author devScarlet, mrTJO
 */
@StaticPacket
public class ExPlayScene extends ServerPacket {
    public static final ExPlayScene STATIC_PACKET = new ExPlayScene();

    private ExPlayScene() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_PLAY_SCENE);
    }

}
