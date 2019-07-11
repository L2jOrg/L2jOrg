package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author mochitto
 */
public class ExNevitAdventEffect extends ServerPacket {
    private final int _timeLeft;

    public ExNevitAdventEffect(int timeLeft) {
        _timeLeft = timeLeft;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SHOW_CHANNELING_EFFECT);

        writeInt(_timeLeft);
    }

}
