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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.PARTY_SMALL_WINDOW_UPDATE);

        writeInt(_member.getObjectId());
        writeShort((short) _flags);
        if (containsMask(PartySmallWindowUpdateType.CURRENT_CP)) {
            writeInt((int) _member.getCurrentCp()); // c4
        }
        if (containsMask(PartySmallWindowUpdateType.MAX_CP)) {
            writeInt(_member.getMaxCp()); // c4
        }
        if (containsMask(PartySmallWindowUpdateType.CURRENT_HP)) {
            writeInt((int) _member.getCurrentHp());
        }
        if (containsMask(PartySmallWindowUpdateType.MAX_HP)) {
            writeInt(_member.getMaxHp());
        }
        if (containsMask(PartySmallWindowUpdateType.CURRENT_MP)) {
            writeInt((int) _member.getCurrentMp());
        }
        if (containsMask(PartySmallWindowUpdateType.MAX_MP)) {
            writeInt(_member.getMaxMp());
        }
        if (containsMask(PartySmallWindowUpdateType.LEVEL)) {
            writeByte((byte) _member.getLevel());
        }
        if (containsMask(PartySmallWindowUpdateType.CLASS_ID)) {
            writeShort((short) _member.getClassId().getId());
        }
        if (containsMask(PartySmallWindowUpdateType.PARTY_SUBSTITUTE)) {
            writeByte((byte) 0x00);
        }
        if (containsMask(PartySmallWindowUpdateType.VITALITY_POINTS)) {
            writeInt(_member.getVitalityPoints());
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
