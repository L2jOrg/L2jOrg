package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class ExPartyPetWindowDelete extends IClientOutgoingPacket {
    private final L2Summon _summon;

    public ExPartyPetWindowDelete(L2Summon summon) {
        _summon = summon;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PARTY_PET_WINDOW_DELETE.writeId(packet);

        packet.putInt(_summon.getObjectId());
        packet.put((byte) _summon.getSummonType());
        packet.putInt(_summon.getOwner().getObjectId());
    }

    @Override
    protected int size(L2GameClient client) {
        return 14;
    }
}
