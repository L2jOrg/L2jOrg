package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2ClanMember;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-
 */
public class PledgeReceiveMemberInfo extends IClientOutgoingPacket {
    private final L2ClanMember _member;

    public PledgeReceiveMemberInfo(L2ClanMember member) {
        _member = member;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLEDGE_RECEIVE_MEMBER_INFO.writeId(packet);

        packet.putInt(_member.getPledgeType());
        writeString(_member.getName(), packet);
        writeString(_member.getTitle(), packet); // title
        packet.putInt(_member.getPowerGrade()); // power

        // clan or subpledge name
        if (_member.getPledgeType() != 0) {
            writeString((_member.getClan().getSubPledge(_member.getPledgeType())).getName(), packet);
        } else {
            writeString(_member.getClan().getName(), packet);
        }

        writeString(_member.getApprenticeOrSponsorName(), packet); // name of this member's apprentice/sponsor
    }

    @Override
    protected int size(L2GameClient client) {
        return 40 + (_member.getName().length() + _member.getTitle().length() + _member.getClan().getName().length() + _member.getApprenticeOrSponsorName().length()) * 2;
    }
}
