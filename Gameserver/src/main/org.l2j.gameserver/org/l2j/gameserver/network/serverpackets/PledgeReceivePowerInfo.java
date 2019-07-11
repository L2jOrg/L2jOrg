package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author -Wooden-
 */
public class PledgeReceivePowerInfo extends ServerPacket {
    private final ClanMember _member;

    public PledgeReceivePowerInfo(ClanMember member) {
        _member = member;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PLEDGE_RECEIVE_POWER_INFO);

        writeInt(_member.getPowerGrade()); // power grade
        writeString(_member.getName());
        writeInt(_member.getClan().getRankPrivs(_member.getPowerGrade()).getBitmask()); // privileges
    }

}
