package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author mochitto
 */
public class ExNevitAdventPointInfoPacket extends IClientOutgoingPacket {
    private final int _points;

    public ExNevitAdventPointInfoPacket(int points) {
        _points = points;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BR_AGATHION_ENERGY_INFO.writeId(packet);

        packet.putInt(_points); // 72 = 1%, max 7200 = 100%
    }

    @Override
    protected int size(L2GameClient client) {
        return 9;
    }
}
