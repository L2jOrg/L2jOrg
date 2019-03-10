package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan.SubPledge;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExPledgeRecruitInfo extends IClientOutgoingPacket {
    private final L2Clan _clan;

    public ExPledgeRecruitInfo(int clanId) {
        _clan = ClanTable.getInstance().getClan(clanId);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PLEDGE_RECRUIT_INFO.writeId(packet);

        final SubPledge[] subPledges = _clan.getAllSubPledges();
        writeString(_clan.getName(), packet);
        writeString(_clan.getLeaderName(), packet);
        packet.putInt(_clan.getLevel());
        packet.putInt(_clan.getMembersCount());
        packet.putInt(subPledges.length);
        for (SubPledge subPledge : subPledges) {
            packet.putInt(subPledge.getId());
            writeString(subPledge.getName(), packet);
        }
    }
}
