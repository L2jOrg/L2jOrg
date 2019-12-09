package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class PartySmallWindowAdd extends ServerPacket {
    private final Player member;
    private final Party party;

    public PartySmallWindowAdd(Player member, Party party) {
        this.member = member;
        this.party = party;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PARTY_SMALL_WINDOW_ADD);

        writeInt(party.getLeaderObjectId()); // c3
        writeInt(party.getDistributionType().getId()); // c3
        writeInt(member.getObjectId());
        writeString(member.getName());

        writeInt((int) member.getCurrentCp()); // c4
        writeInt(member.getMaxCp()); // c4
        writeInt((int) member.getCurrentHp());
        writeInt(member.getMaxHp());
        writeInt((int) member.getCurrentMp());
        writeInt(member.getMaxMp());
        writeInt(member.getVitalityPoints());
        writeByte(member.getLevel());
        writeShort(member.getClassId().getId());
        writeByte(0x00);
        writeShort(member.getRace().ordinal());
        writeInt(0x00);
    }

}
