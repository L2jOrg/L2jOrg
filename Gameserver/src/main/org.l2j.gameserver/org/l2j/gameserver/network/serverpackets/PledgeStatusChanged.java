package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class PledgeStatusChanged extends ServerPacket {
    private final Clan _clan;

    public PledgeStatusChanged(Clan clan) {
        _clan = clan;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PLEDGE_STATUS_CHANGED);

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
