package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Plim
 */
@StaticPacket
public class PetitionVotePacket extends IClientOutgoingPacket {
    public static final PetitionVotePacket STATIC_PACKET = new PetitionVotePacket();

    private PetitionVotePacket() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PETITION_VOTE.writeId(packet);
    }
}
