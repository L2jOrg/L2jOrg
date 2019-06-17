package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author GodKratos
 */
public class ExVitalityPointInfo extends ServerPacket {
    private final int _vitalityPoints;

    public ExVitalityPointInfo(int vitPoints) {
        _vitalityPoints = vitPoints;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_VITALITY_POINT_INFO);

        writeInt(_vitalityPoints);
    }

}
