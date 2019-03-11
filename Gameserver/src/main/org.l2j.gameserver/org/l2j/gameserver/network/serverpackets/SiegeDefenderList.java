package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2SiegeClan;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * Populates the Siege Defender List in the SiegeInfo Window<BR>
 * <BR>
 * c = 0xcb<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = unknow (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Defending Clans?<BR>
 * d = Number of Defending Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = Type -> Owner = 0x01 || Waiting = 0x02 || Accepted = 0x03<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 *
 * @author KenM
 */
public final class SiegeDefenderList extends IClientOutgoingPacket {
    private final Castle _castle;

    public SiegeDefenderList(Castle castle) {
        _castle = castle;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.CASTLE_SIEGE_DEFENDER_LIST.writeId(packet);

        packet.putInt(_castle.getResidenceId());
        packet.putInt(0x00); // Unknown
        packet.putInt(0x01); // Unknown
        packet.putInt(0x00); // Unknown

        final int size = _castle.getSiege().getDefenderWaitingClans().size() + _castle.getSiege().getDefenderClans().size() + (_castle.getOwner() != null ? 1 : 0);

        packet.putInt(size);
        packet.putInt(size);

        // Add owners
        final L2Clan ownerClan = _castle.getOwner();
        if (ownerClan != null) {
            packet.putInt(ownerClan.getId());
            writeString(ownerClan.getName(), packet);
            writeString(ownerClan.getLeaderName(), packet);
            packet.putInt(ownerClan.getCrestId());
            packet.putInt(0x00); // signed time (seconds) (not storated by L2J)
            packet.putInt(SiegeClanType.OWNER.ordinal());
            packet.putInt(ownerClan.getAllyId());
            writeString(ownerClan.getAllyName(), packet);
            writeString("", packet); // AllyLeaderName
            packet.putInt(ownerClan.getAllyCrestId());
        }

        // List of confirmed defenders
        for (L2SiegeClan siegeClan : _castle.getSiege().getDefenderClans()) {
            final L2Clan defendingClan = ClanTable.getInstance().getClan(siegeClan.getClanId());
            if ((defendingClan == null) || (defendingClan == _castle.getOwner())) {
                continue;
            }

            packet.putInt(defendingClan.getId());
            writeString(defendingClan.getName(), packet);
            writeString(defendingClan.getLeaderName(), packet);
            packet.putInt(defendingClan.getCrestId());
            packet.putInt(0x00); // signed time (seconds) (not storated by L2J)
            packet.putInt(SiegeClanType.DEFENDER.ordinal());
            packet.putInt(defendingClan.getAllyId());
            writeString(defendingClan.getAllyName(), packet);
            writeString("", packet); // AllyLeaderName
            packet.putInt(defendingClan.getAllyCrestId());
        }

        // List of not confirmed defenders
        for (L2SiegeClan siegeClan : _castle.getSiege().getDefenderWaitingClans()) {
            final L2Clan defendingClan = ClanTable.getInstance().getClan(siegeClan.getClanId());
            if (defendingClan == null) {
                continue;
            }

            packet.putInt(defendingClan.getId());
            writeString(defendingClan.getName(), packet);
            writeString(defendingClan.getLeaderName(), packet);
            packet.putInt(defendingClan.getCrestId());
            packet.putInt(0x00); // signed time (seconds) (not storated by L2J)
            packet.putInt(SiegeClanType.DEFENDER_PENDING.ordinal());
            packet.putInt(defendingClan.getAllyId());
            writeString(defendingClan.getAllyName(), packet);
            writeString("", packet); // AllyLeaderName
            packet.putInt(defendingClan.getAllyCrestId());
        }
    }
}
