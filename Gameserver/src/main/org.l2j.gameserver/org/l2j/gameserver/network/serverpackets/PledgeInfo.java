package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.settings.ServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class PledgeInfo extends ServerPacket {
    private final Clan _clan;

    public PledgeInfo(Clan clan) {
        _clan = clan;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PLEDGE_INFO);

        writeInt(getSettings(ServerSettings.class).serverId());
        writeInt(_clan.getId());
        writeString(_clan.getName());
        writeString(_clan.getAllyName());
    }

}
