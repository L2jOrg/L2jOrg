package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

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
        writeId(ServerExPacketId.EX_VIEW_PLEDGE_POWER);

        writeInt(_member.getPowerGrade()); // power grade
        writeString(_member.getName());
        writeInt(_member.getClan().getRankPrivs(_member.getPowerGrade()).getBitmask()); // privileges
    }

}
