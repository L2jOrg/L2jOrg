package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public final class ExPartyPetWindowAdd extends IClientOutgoingPacket {
    private final L2Summon _summon;

    public ExPartyPetWindowAdd(L2Summon summon) {
        _summon = summon;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PARTY_PET_WINDOW_ADD.writeId(packet);

        packet.putInt(_summon.getObjectId());
        packet.putInt(_summon.getTemplate().getDisplayId() + 1000000);
        packet.put((byte) _summon.getSummonType());
        packet.putInt(_summon.getOwner().getObjectId());
        packet.putInt((int) _summon.getCurrentHp());
        packet.putInt(_summon.getMaxHp());
        packet.putInt((int) _summon.getCurrentMp());
        packet.putInt(_summon.getMaxMp());
    }
}
