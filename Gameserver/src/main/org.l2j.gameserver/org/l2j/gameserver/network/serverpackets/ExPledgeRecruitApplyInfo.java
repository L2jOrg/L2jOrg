package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.ClanEntryStatus;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExPledgeRecruitApplyInfo extends ServerPacket {
    private final ClanEntryStatus _status;

    public ExPledgeRecruitApplyInfo(ClanEntryStatus status) {
        _status = status;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_PLEDGE_RECRUIT_APPLY_INFO);

        writeInt(_status.ordinal());
    }

}
