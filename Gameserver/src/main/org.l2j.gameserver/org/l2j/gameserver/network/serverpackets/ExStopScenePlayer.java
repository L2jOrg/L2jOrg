package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.Movie;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author St3eT
 */
public class ExStopScenePlayer extends ServerPacket {
    private final Movie _movie;

    public ExStopScenePlayer(Movie movie) {
        _movie = movie;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_STOP_SCENE_PLAYER);

        writeInt(_movie.getClientId());
    }

}