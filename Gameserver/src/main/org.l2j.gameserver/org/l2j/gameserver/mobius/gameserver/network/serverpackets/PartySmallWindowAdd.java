package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class PartySmallWindowAdd extends IClientOutgoingPacket {
    private final L2PcInstance _member;
    private final L2Party _party;

    public PartySmallWindowAdd(L2PcInstance member, L2Party party) {
        _member = member;
        _party = party;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PARTY_SMALL_WINDOW_ADD.writeId(packet);

        packet.putInt(_party.getLeaderObjectId()); // c3
        packet.putInt(_party.getDistributionType().getId()); // c3
        packet.putInt(_member.getObjectId());
        writeString(_member.getName(), packet);

        packet.putInt((int) _member.getCurrentCp()); // c4
        packet.putInt(_member.getMaxCp()); // c4
        packet.putInt((int) _member.getCurrentHp());
        packet.putInt(_member.getMaxHp());
        packet.putInt((int) _member.getCurrentMp());
        packet.putInt(_member.getMaxMp());
        packet.putInt(_member.getVitalityPoints());
        packet.put((byte) _member.getLevel());
        packet.putShort((short) _member.getClassId().getId());
        packet.put((byte) 0x00);
        packet.putShort((short) _member.getRace().ordinal());
    }
}
