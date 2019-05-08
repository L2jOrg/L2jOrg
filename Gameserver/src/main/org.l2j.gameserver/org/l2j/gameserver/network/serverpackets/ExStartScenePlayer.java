package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.Movie;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author JIV
 */
public class ExStartScenePlayer extends IClientOutgoingPacket {
    private final Movie _movie;

    public ExStartScenePlayer(Movie movie) {
        _movie = movie;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_START_SCENE_PLAYER.writeId(packet);

        packet.putInt(_movie.getClientId());
    }

    @Override
    protected int size(L2GameClient client) {
        return 9;
    }
}
