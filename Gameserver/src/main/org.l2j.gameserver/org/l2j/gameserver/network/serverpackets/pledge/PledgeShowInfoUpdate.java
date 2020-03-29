package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.settings.ServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author JoeAlisson
 */
public class PledgeShowInfoUpdate extends PledgeAbstractPacket {

    public PledgeShowInfoUpdate(Clan clan) {
        super(clan);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PLEDGE_SHOW_INFO_UPDATE);

        writeInt(clan.getId());
        writeInt(getSettings(ServerSettings.class).serverId());
        writeClanInfo(0x00);
    }

}
