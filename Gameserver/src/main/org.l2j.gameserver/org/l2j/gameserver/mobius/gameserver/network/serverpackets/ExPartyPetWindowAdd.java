package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author KenM
 */
public final class ExPartyPetWindowAdd implements IClientOutgoingPacket
{
    private final L2Summon _summon;

    public ExPartyPetWindowAdd(L2Summon summon)
    {
        _summon = summon;
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.EX_PARTY_PET_WINDOW_ADD.writeId(packet);

        packet.writeD(_summon.getObjectId());
        packet.writeD(_summon.getTemplate().getDisplayId() + 1000000);
        packet.writeC(_summon.getSummonType());
        packet.writeD(_summon.getOwner().getObjectId());
        packet.writeD((int) _summon.getCurrentHp());
        packet.writeD(_summon.getMaxHp());
        packet.writeD((int) _summon.getCurrentMp());
        packet.writeD(_summon.getMaxMp());
        return true;
    }
}
