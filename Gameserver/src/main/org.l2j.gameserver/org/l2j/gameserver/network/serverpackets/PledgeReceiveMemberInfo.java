package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author -Wooden-
 */
public class PledgeReceiveMemberInfo extends ServerPacket {
    private final ClanMember _member;

    public PledgeReceiveMemberInfo(ClanMember member) {
        _member = member;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PLEDGE_RECEIVE_MEMBER_INFO);

        writeInt(_member.getPledgeType());
        writeString(_member.getName());
        writeString(_member.getTitle()); // title
        writeInt(_member.getPowerGrade()); // power

        // clan or subpledge name
        if (_member.getPledgeType() != 0) {
            writeString((_member.getClan().getSubPledge(_member.getPledgeType())).getName());
        } else {
            writeString(_member.getClan().getName());
        }

        writeString(_member.getApprenticeOrSponsorName()); // name of this member's apprentice/sponsor
    }

}
