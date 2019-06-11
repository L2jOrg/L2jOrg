package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class ExPartyPetWindowUpdate extends IClientOutgoingPacket {
    private final L2Summon _summon;

    public ExPartyPetWindowUpdate(L2Summon summon) {
        _summon = summon;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_PARTY_PET_WINDOW_UPDATE);

        writeInt(_summon.getObjectId());
        writeInt(_summon.getTemplate().getDisplayId() + 1000000);
        writeByte((byte) _summon.getSummonType());
        writeInt(_summon.getOwner().getObjectId());
        writeInt((int) _summon.getCurrentHp());
        writeInt(_summon.getMaxHp());
        writeInt((int) _summon.getCurrentMp());
        writeInt(_summon.getMaxMp());
    }

}
