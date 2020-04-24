package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author devScarlet, mrTJO
 */
@StaticPacket
public class ExPlayScene extends ServerPacket {
    public static final ExPlayScene STATIC_PACKET = new ExPlayScene();

    private ExPlayScene() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PLAY_SCENE);
    }

}
