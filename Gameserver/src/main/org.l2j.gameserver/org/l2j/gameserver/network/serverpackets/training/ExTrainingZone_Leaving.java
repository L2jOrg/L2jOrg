package org.l2j.gameserver.network.serverpackets.training;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExTrainingZone_Leaving extends IClientOutgoingPacket {
    public static ExTrainingZone_Leaving STATIC_PACKET = new ExTrainingZone_Leaving();

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_TRAINING_ZONE_LEAVING.writeId(packet);
    }
}
