package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2ClanMember;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.settings.ServerSettings;

import java.nio.ByteBuffer;
import java.util.Collection;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class PledgeShowMemberListAll extends IClientOutgoingPacket {
    private final L2Clan _clan;
    private final L2Clan.SubPledge _pledge;
    private final String _name;
    private final String _leaderName;
    private final Collection<L2ClanMember> _members;
    private final int _pledgeId;
    private final boolean _isSubPledge;

    private PledgeShowMemberListAll(L2Clan clan, L2Clan.SubPledge pledge, boolean isSubPledge) {
        _clan = clan;
        _pledge = pledge;
        _pledgeId = _pledge == null ? 0x00 : _pledge.getId();
        _leaderName = pledge == null ? clan.getLeaderName() : CharNameTable.getInstance().getNameById(pledge.getLeaderId());
        _name = pledge == null ? clan.getName() : pledge.getName();
        _members = _clan.getMembers();
        _isSubPledge = isSubPledge;
    }

    public static void sendAllTo(L2PcInstance player) {
        final L2Clan clan = player.getClan();
        if (clan != null) {
            for (L2Clan.SubPledge subPledge : clan.getAllSubPledges()) {
                player.sendPacket(new PledgeShowMemberListAll(clan, subPledge, false));
            }
            player.sendPacket(new PledgeShowMemberListAll(clan, null, true));
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLEDGE_SHOW_MEMBER_LIST_ALL.writeId(packet);

        packet.putInt(_isSubPledge ? 0x00 : 0x01);
        packet.putInt(_clan.getId());
        packet.putInt(getSettings(ServerSettings.class).serverId());
        packet.putInt(_pledgeId);
        writeString(_name, packet);
        writeString(_leaderName, packet);

        packet.putInt(_clan.getCrestId()); // crest id .. is used again
        packet.putInt(_clan.getLevel());
        packet.putInt(_clan.getCastleId());
        packet.putInt(0x00);
        packet.putInt(_clan.getHideoutId());
        packet.putInt(_clan.getFortId());
        packet.putInt(_clan.getRank());
        packet.putInt(_clan.getReputationScore());
        packet.putInt(0x00); // 0
        packet.putInt(0x00); // 0
        packet.putInt(_clan.getAllyId());
        writeString(_clan.getAllyName(), packet);
        packet.putInt(_clan.getAllyCrestId());
        packet.putInt(_clan.isAtWar() ? 1 : 0); // new c3
        packet.putInt(0x00); // Territory castle ID
        packet.putInt(_clan.getSubPledgeMembersCount(_pledgeId));

        for (L2ClanMember m : _members) {
            if (m.getPledgeType() != _pledgeId) {
                continue;
            }
            writeString(m.getName(), packet);
            packet.putInt(m.getLevel());
            packet.putInt(m.getClassId());
            final L2PcInstance player = m.getPlayerInstance();
            if (player != null) {
                packet.putInt(player.getAppearance().getSex() ? 1 : 0); // no visible effect
                packet.putInt(player.getRace().ordinal()); // packet.putInt(1);
            } else {
                packet.putInt(0x01); // no visible effect
                packet.putInt(0x01); // packet.putInt(1);
            }
            packet.putInt(m.isOnline() ? m.getObjectId() : 0); // objectId = online 0 = offline
            packet.putInt(m.getSponsor() != 0 ? 1 : 0);
            packet.put((byte) m.getOnlineStatus());
        }
    }
}
