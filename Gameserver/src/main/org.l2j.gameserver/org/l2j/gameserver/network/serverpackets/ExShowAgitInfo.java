package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

/**
 * @author KenM
 */
public class ExShowAgitInfo extends ServerPacket {
    public static final ExShowAgitInfo STATIC_PACKET = new ExShowAgitInfo();

    private ExShowAgitInfo() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SHOW_AGIT_INFO);

        final Collection<ClanHall> clanHalls = ClanHallManager.getInstance().getClanHalls();
        writeInt(clanHalls.size());
        clanHalls.forEach(clanHall ->
        {
            writeInt(clanHall.getId());
            writeString(clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getName()); // owner clan name
            writeString(clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getLeaderName()); // leader name
            writeInt(clanHall.getType().getClientVal()); // Clan hall type
        });
    }

}
