package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class PledgeInfo extends IClientOutgoingPacket {
    private final L2Clan _clan;

    public PledgeInfo(L2Clan clan) {
        _clan = clan;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLEDGE_INFO.writeId(packet);

        packet.putInt(Config.SERVER_ID);
        packet.putInt(_clan.getId());
        writeString(_clan.getName(), packet);
        writeString(_clan.getAllyName(), packet);
    }
}
