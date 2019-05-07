package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author devScarlet, mrTJO
 */
@StaticPacket
public class ExPlayScene extends IClientOutgoingPacket {
    public static final ExPlayScene STATIC_PACKET = new ExPlayScene();

    private ExPlayScene() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PLAY_SCENE.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}
