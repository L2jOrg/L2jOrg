package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.enums.Movie;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

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
}
