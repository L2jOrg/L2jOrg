package org.l2j.gameserver.network.serverpackets.training;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExTrainingZone_Leaving extends ServerPacket {
    public static ExTrainingZone_Leaving STATIC_PACKET = new ExTrainingZone_Leaving();

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_TRAINING_ZONE_LEAVING);
    }

}
