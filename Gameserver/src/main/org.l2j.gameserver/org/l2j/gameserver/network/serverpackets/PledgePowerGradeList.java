package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Clan.RankPrivs;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class PledgePowerGradeList extends ServerPacket {
    private final RankPrivs[] _privs;

    public PledgePowerGradeList(RankPrivs[] privs) {
        _privs = privs;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PLEDGE_POWER_GRADE_LIST);

        writeInt(_privs.length);
        for (RankPrivs temp : _privs) {
            writeInt(temp.getRank());
            writeInt(temp.getParty());
        }
    }

}
