package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author GodKratos
 */
public class ExVitalityPointInfo extends IClientOutgoingPacket {
    private final int _vitalityPoints;

    public ExVitalityPointInfo(int vitPoints) {
        _vitalityPoints = vitPoints;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_VITALITY_POINT_INFO.writeId(packet);

        packet.putInt(_vitalityPoints);
    }

    @Override
    protected int size(L2GameClient client) {
        return 9;
    }
}
