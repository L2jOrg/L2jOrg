package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.enums.ClanEntryStatus;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExPledgeRecruitApplyInfo extends IClientOutgoingPacket {
    private final ClanEntryStatus _status;

    public ExPledgeRecruitApplyInfo(ClanEntryStatus status) {
        _status = status;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PLEDGE_RECRUIT_APPLY_INFO.writeId(packet);

        packet.putInt(_status.ordinal());
    }
}
