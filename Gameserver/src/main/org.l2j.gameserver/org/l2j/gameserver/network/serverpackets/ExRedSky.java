package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author KenM
 */
public class ExRedSky extends ServerPacket {
    private final int _duration;

    public ExRedSky(int duration) {
        _duration = duration;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_REDSKY);

        writeInt(_duration);
    }

}
