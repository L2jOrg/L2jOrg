package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.PartySmallWindowUpdateType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class PartySmallWindowUpdate extends AbstractMaskPacket<PartySmallWindowUpdateType> {
    private final L2PcInstance _member;
    private int _flags = 0;

    public PartySmallWindowUpdate(L2PcInstance member, boolean addAllFlags) {
        _member = member;
        if (addAllFlags) {
            for (PartySmallWindowUpdateType type : PartySmallWindowUpdateType.values()) {
                addComponentType(type);
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PARTY_SMALL_WINDOW_UPDATE.writeId(packet);

        packet.putInt(_member.getObjectId());
        packet.putShort((short) _flags);
        if (containsMask(PartySmallWindowUpdateType.CURRENT_CP)) {
            packet.putInt((int) _member.getCurrentCp()); // c4
        }
        if (containsMask(PartySmallWindowUpdateType.MAX_CP)) {
            packet.putInt(_member.getMaxCp()); // c4
        }
        if (containsMask(PartySmallWindowUpdateType.CURRENT_HP)) {
            packet.putInt((int) _member.getCurrentHp());
        }
        if (containsMask(PartySmallWindowUpdateType.MAX_HP)) {
            packet.putInt(_member.getMaxHp());
        }
        if (containsMask(PartySmallWindowUpdateType.CURRENT_MP)) {
            packet.putInt((int) _member.getCurrentMp());
        }
        if (containsMask(PartySmallWindowUpdateType.MAX_MP)) {
            packet.putInt(_member.getMaxMp());
        }
        if (containsMask(PartySmallWindowUpdateType.LEVEL)) {
            packet.put((byte) _member.getLevel());
        }
        if (containsMask(PartySmallWindowUpdateType.CLASS_ID)) {
            packet.putShort((short) _member.getClassId().getId());
        }
        if (containsMask(PartySmallWindowUpdateType.PARTY_SUBSTITUTE)) {
            packet.put((byte) 0x00);
        }
        if (containsMask(PartySmallWindowUpdateType.VITALITY_POINTS)) {
            packet.putInt(_member.getVitalityPoints());
        }
    }

    @Override
    protected void addMask(int mask) {
        _flags |= mask;
    }

    @Override
    public boolean containsMask(PartySmallWindowUpdateType component) {
        return containsMask(_flags, component);
    }

    @Override
    protected byte[] getMasks() {
        return new byte[0];
    }
}
