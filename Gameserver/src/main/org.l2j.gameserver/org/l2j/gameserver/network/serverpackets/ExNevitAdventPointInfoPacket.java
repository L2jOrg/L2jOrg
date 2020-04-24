package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author mochitto
 */
public class ExNevitAdventPointInfoPacket extends ServerPacket {
    private final int _points;

    public ExNevitAdventPointInfoPacket(int points) {
        _points = points;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BR_AGATHION_ENERGY_INFO);

        writeInt(_points); // 72 = 1%, max 7200 = 100%
    }

}
