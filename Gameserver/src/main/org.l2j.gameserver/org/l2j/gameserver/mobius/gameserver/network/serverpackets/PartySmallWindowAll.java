package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class PartySmallWindowAll extends IClientOutgoingPacket {
    private final L2Party _party;
    private final L2PcInstance _exclude;

    public PartySmallWindowAll(L2PcInstance exclude, L2Party party) {
        _exclude = exclude;
        _party = party;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PARTY_SMALL_WINDOW_ALL.writeId(packet);

        packet.putInt(_party.getLeaderObjectId());
        packet.put((byte) _party.getDistributionType().getId());
        packet.put((byte) (_party.getMemberCount() - 1));

        for (L2PcInstance member : _party.getMembers()) {
            if ((member != null) && (member != _exclude)) {
                packet.putInt(member.getObjectId());
                writeString(member.getName(), packet);

                packet.putInt((int) member.getCurrentCp()); // c4
                packet.putInt(member.getMaxCp()); // c4

                packet.putInt((int) member.getCurrentHp());
                packet.putInt(member.getMaxHp());
                packet.putInt((int) member.getCurrentMp());
                packet.putInt(member.getMaxMp());
                packet.putInt(member.getVitalityPoints());
                packet.put((byte) member.getLevel());
                packet.putShort((short) member.getClassId().getId());
                packet.put((byte) 0x01); // Unk
                packet.putShort((short) member.getRace().ordinal());
                final L2Summon pet = member.getPet();
                packet.putInt(member.getServitors().size() + (pet != null ? 1 : 0)); // Summon size, one only atm
                if (pet != null) {
                    packet.putInt(pet.getObjectId());
                    packet.putInt(pet.getId() + 1000000);
                    packet.put((byte) pet.getSummonType());
                    writeString(pet.getName(), packet);
                    packet.putInt((int) pet.getCurrentHp());
                    packet.putInt(pet.getMaxHp());
                    packet.putInt((int) pet.getCurrentMp());
                    packet.putInt(pet.getMaxMp());
                    packet.put((byte) pet.getLevel());
                }
                member.getServitors().values().forEach(s ->
                {
                    packet.putInt(s.getObjectId());
                    packet.putInt(s.getId() + 1000000);
                    packet.put((byte) s.getSummonType());
                    writeString(s.getName(), packet);
                    packet.putInt((int) s.getCurrentHp());
                    packet.putInt(s.getMaxHp());
                    packet.putInt((int) s.getCurrentMp());
                    packet.putInt(s.getMaxMp());
                    packet.put((byte) s.getLevel());
                });
            }
        }
    }
}
