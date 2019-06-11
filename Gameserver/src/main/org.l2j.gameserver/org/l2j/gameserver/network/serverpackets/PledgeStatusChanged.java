package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class PledgeStatusChanged extends IClientOutgoingPacket {
    private final L2Clan _clan;

    public PledgeStatusChanged(L2Clan clan) {
        _clan = clan;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.PLEDGE_STATUS_CHANGED);

        writeInt(0x00);
        writeInt(_clan.getLeaderId());
        writeInt(_clan.getId());
        writeInt(_clan.getCrestId());
        writeInt(_clan.getAllyId());
        writeInt(_clan.getAllyCrestId());
        writeInt(_clan.getCrestLargeId());
        writeInt(0x00); // pledge type ?
    }

}
