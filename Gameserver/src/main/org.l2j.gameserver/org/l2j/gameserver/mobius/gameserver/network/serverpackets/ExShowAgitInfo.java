package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.ClanHallData;
import org.l2j.gameserver.mobius.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_AGIT_INFO.writeId(packet);

        final Collection<ClanHall> clanHalls = ClanHallData.getInstance().getClanHalls();
        packet.putInt(clanHalls.size());
        clanHalls.forEach(clanHall ->
        {
            packet.putInt(clanHall.getResidenceId());
            writeString(clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getName(), packet); // owner clan name
            writeString(clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getLeaderName(), packet); // leader name
            packet.putInt(clanHall.getType().getClientVal()); // Clan hall type
        });
    }
}
