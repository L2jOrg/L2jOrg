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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLEDGE_STATUS_CHANGED.writeId(packet);

        packet.putInt(0x00);
        packet.putInt(_clan.getLeaderId());
        packet.putInt(_clan.getId());
        packet.putInt(_clan.getCrestId());
        packet.putInt(_clan.getAllyId());
        packet.putInt(_clan.getAllyCrestId());
        packet.putInt(_clan.getCrestLargeId());
        packet.putInt(0x00); // pledge type ?
    }
}
