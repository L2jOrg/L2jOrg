package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.ClanHallData;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author KenM
 */
public class ExShowAgitInfo extends IClientOutgoingPacket {
    public static final ExShowAgitInfo STATIC_PACKET = new ExShowAgitInfo();

    private ExShowAgitInfo() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_SHOW_AGIT_INFO);

        final Collection<ClanHall> clanHalls = ClanHallData.getInstance().getClanHalls();
        writeInt(clanHalls.size());
        clanHalls.forEach(clanHall ->
        {
            writeInt(clanHall.getResidenceId());
            writeString(clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getName()); // owner clan name
            writeString(clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getLeaderName()); // leader name
            writeInt(clanHall.getType().getClientVal()); // Clan hall type
        });
    }

}
