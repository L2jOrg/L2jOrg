package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2ClanMember;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class GMViewPledgeInfo extends IClientOutgoingPacket {
    private final L2Clan _clan;
    private final L2PcInstance _activeChar;

    public GMViewPledgeInfo(L2Clan clan, L2PcInstance activeChar) {
        _clan = clan;
        _activeChar = activeChar;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.GM_VIEW_PLEDGE_INFO.writeId(packet);

        packet.putInt(0x00);
        writeString(_activeChar.getName(), packet);
        packet.putInt(_clan.getId());
        packet.putInt(0x00);
        writeString(_clan.getName(), packet);
        writeString(_clan.getLeaderName(), packet);

        packet.putInt(_clan.getCrestId()); // -> no, it's no longer used (nuocnam) fix by game
        packet.putInt(_clan.getLevel());
        packet.putInt(_clan.getCastleId());
        packet.putInt(_clan.getHideoutId());
        packet.putInt(_clan.getFortId());
        packet.putInt(_clan.getRank());
        packet.putInt(_clan.getReputationScore());
        packet.putInt(0x00);
        packet.putInt(0x00);
        packet.putInt(0x00);
        packet.putInt(_clan.getAllyId()); // c2
        writeString(_clan.getAllyName(), packet); // c2
        packet.putInt(_clan.getAllyCrestId()); // c2
        packet.putInt(_clan.isAtWar() ? 1 : 0); // c3
        packet.putInt(0x00); // T3 Unknown

        packet.putInt(_clan.getMembers().size());
        for (L2ClanMember member : _clan.getMembers()) {
            if (member != null) {
                writeString(member.getName(), packet);
                packet.putInt(member.getLevel());
                packet.putInt(member.getClassId());
                packet.putInt(member.getSex() ? 1 : 0);
                packet.putInt(member.getRaceOrdinal());
                packet.putInt(member.isOnline() ? member.getObjectId() : 0);
                packet.putInt(member.getSponsor() != 0 ? 1 : 0);
            }
        }
    }
}
