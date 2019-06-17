package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 */
public class ExPartyPetWindowDelete extends ServerPacket {
    private final L2Summon _summon;

    public ExPartyPetWindowDelete(L2Summon summon) {
        _summon = summon;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_PARTY_PET_WINDOW_DELETE);

        writeInt(_summon.getObjectId());
        writeByte((byte) _summon.getSummonType());
        writeInt(_summon.getOwner().getObjectId());
    }

}
