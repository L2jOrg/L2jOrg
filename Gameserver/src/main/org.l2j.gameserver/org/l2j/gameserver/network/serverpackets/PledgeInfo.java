package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.settings.ServerSettings;

import java.nio.ByteBuffer;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class PledgeInfo extends IClientOutgoingPacket {
    private final L2Clan _clan;

    public PledgeInfo(L2Clan clan) {
        _clan = clan;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLEDGE_INFO.writeId(packet);

        packet.putInt(getSettings(ServerSettings.class).serverId());
        packet.putInt(_clan.getId());
        writeString(_clan.getName(), packet);
        writeString(_clan.getAllyName(), packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 13 + (_clan.getName().length() + _clan.getAllyName().length()) * 2;
    }
}
