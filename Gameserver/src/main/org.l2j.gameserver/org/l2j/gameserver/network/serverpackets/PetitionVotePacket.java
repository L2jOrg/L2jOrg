package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Plim
 */
@StaticPacket
public class PetitionVotePacket extends ServerPacket {
    public static final PetitionVotePacket STATIC_PACKET = new PetitionVotePacket();

    private PetitionVotePacket() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PETITION_VOTE);
    }

}
