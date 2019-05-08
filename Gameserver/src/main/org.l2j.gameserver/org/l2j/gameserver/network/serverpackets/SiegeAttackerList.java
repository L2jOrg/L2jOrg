package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2SiegeClan;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * Populates the Siege Attacker List in the SiegeInfo Window<BR>
 * <BR>
 * c = ca<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = unknow (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Attackers Clans?<BR>
 * d = Number of Attackers Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 *
 * @author KenM
 */
public final class SiegeAttackerList extends IClientOutgoingPacket {
    private final Castle _castle;

    public SiegeAttackerList(Castle castle) {
        _castle = castle;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.CASTLE_SIEGE_ATTACKER_LIST.writeId(packet);

        packet.putInt(_castle.getResidenceId());
        packet.putInt(0x00); // 0
        packet.putInt(0x01); // 1
        packet.putInt(0x00); // 0
        final int size = _castle.getSiege().getAttackerClans().size();
        if (size > 0) {
            L2Clan clan;

            packet.putInt(size);
            packet.putInt(size);
            for (L2SiegeClan siegeclan : _castle.getSiege().getAttackerClans()) {
                clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
                if (clan == null) {
                    continue;
                }

                packet.putInt(clan.getId());
                writeString(clan.getName(), packet);
                writeString(clan.getLeaderName(), packet);
                packet.putInt(clan.getCrestId());
                packet.putInt(0x00); // signed time (seconds) (not storated by L2J)
                packet.putInt(clan.getAllyId());
                writeString(clan.getAllyName(), packet);
                writeString("", packet); // AllyLeaderName
                packet.putInt(clan.getAllyCrestId());
            }
        } else {
            packet.putInt(0x00);
            packet.putInt(0x00);
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 30 + _castle.getSiege().getAttackerClans().size() * 148;
    }
}
