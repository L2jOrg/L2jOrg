package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.Movie;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JIV
 */
public class ExStartScenePlayer extends ServerPacket {
    private final Movie _movie;

    public ExStartScenePlayer(Movie movie) {
        _movie = movie;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_START_SCENE_PLAYER);

        writeInt(_movie.getClientId());
    }

}
