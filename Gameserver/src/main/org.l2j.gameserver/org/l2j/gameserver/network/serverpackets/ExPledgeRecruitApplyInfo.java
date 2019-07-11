package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.ClanEntryStatus;
import org.l2j.gameserver.network.GameClient;
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_PLEDGE_RECRUIT_APPLY_INFO);

        writeInt(_status.ordinal());
    }

}
