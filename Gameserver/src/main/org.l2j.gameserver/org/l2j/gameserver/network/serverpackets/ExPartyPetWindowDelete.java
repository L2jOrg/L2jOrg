package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 */
public class ExPartyPetWindowDelete extends ServerPacket {
    private final Summon _summon;

    public ExPartyPetWindowDelete(Summon summon) {
        _summon = summon;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_PARTY_PET_WINDOW_DELETE);

        writeInt(_summon.getObjectId());
        writeByte((byte) _summon.getSummonType());
        writeInt(_summon.getOwner().getObjectId());
    }

}
