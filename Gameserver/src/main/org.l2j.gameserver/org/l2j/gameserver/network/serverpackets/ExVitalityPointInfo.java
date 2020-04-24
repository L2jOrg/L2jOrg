package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author GodKratos
 */
public class ExVitalityPointInfo extends ServerPacket {
    private final int _vitalityPoints;

    public ExVitalityPointInfo(int vitPoints) {
        _vitalityPoints = vitPoints;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_VITALITY_POINT_INFO);

        writeInt(_vitalityPoints);
    }

}
