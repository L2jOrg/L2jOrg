package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.settings.ServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class PledgeShowInfoUpdate extends ServerPacket {
    private final Clan _clan;

    public PledgeShowInfoUpdate(Clan clan) {
        _clan = clan;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PLEDGE_SHOW_INFO_UPDATE);

        // sending empty data so client will ask all the info in response ;)
        writeInt(_clan.getId());
        writeInt(getSettings(ServerSettings.class).serverId());
        writeInt(_clan.getCrestId());
        writeInt(_clan.getLevel()); // clan level
        writeInt(_clan.getCastleId());
        writeInt(0x00); // castle state ?
        writeInt(_clan.getHideoutId());
        writeInt(_clan.getFortId());
        writeInt(_clan.getRank());
        writeInt(_clan.getReputationScore()); // clan reputation score
        writeInt(0x00); // ?
        writeInt(0x00); // ?
        writeInt(_clan.getAllyId());
        writeString(_clan.getAllyName()); // c5
        writeInt(_clan.getAllyCrestId()); // c5
        writeInt(_clan.isAtWar() ? 1 : 0); // c5
        writeInt(0x00); // TODO: Find me!
        writeInt(0x00); // TODO: Find me!
    }

}
