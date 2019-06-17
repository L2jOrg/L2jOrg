package org.l2j.gameserver.network.serverpackets.training;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExTrainingZone_Leaving extends ServerPacket {
    public static ExTrainingZone_Leaving STATIC_PACKET = new ExTrainingZone_Leaving();

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_TRAINING_ZONE_LEAVING);
    }

}
