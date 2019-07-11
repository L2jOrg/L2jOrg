package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class PartySmallWindowAdd extends ServerPacket {
    private final Player _member;
    private final Party _party;

    public PartySmallWindowAdd(Player member, Party party) {
        _member = member;
        _party = party;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PARTY_SMALL_WINDOW_ADD);

        writeInt(_party.getLeaderObjectId()); // c3
        writeInt(_party.getDistributionType().getId()); // c3
        writeInt(_member.getObjectId());
        writeString(_member.getName());

        writeInt((int) _member.getCurrentCp()); // c4
        writeInt(_member.getMaxCp()); // c4
        writeInt((int) _member.getCurrentHp());
        writeInt(_member.getMaxHp());
        writeInt((int) _member.getCurrentMp());
        writeInt(_member.getMaxMp());
        writeInt(_member.getVitalityPoints());
        writeByte((byte) _member.getLevel());
        writeShort((short) _member.getClassId().getId());
        writeByte((byte) 0x00);
        writeShort((short) _member.getRace().ordinal());
    }

}
