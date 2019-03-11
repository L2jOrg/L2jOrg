package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Clan.RankPrivs;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class PledgePowerGradeList extends IClientOutgoingPacket {
    private final RankPrivs[] _privs;

    public PledgePowerGradeList(RankPrivs[] privs) {
        _privs = privs;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLEDGE_POWER_GRADE_LIST.writeId(packet);

        packet.putInt(_privs.length);
        for (RankPrivs temp : _privs) {
            packet.putInt(temp.getRank());
            packet.putInt(temp.getParty());
        }
    }
}
