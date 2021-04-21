package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExMercenaryCastleWarCastleSiegeDefender  extends ServerPacket {
    private final Castle _castle;

    public ExMercenaryCastleWarCastleSiegeDefender(Castle castle) {
        _castle = castle;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.CASTLE_SIEGE_DEFENDER_LIST, buffer );

        buffer.writeInt(_castle.getId());
        buffer.writeInt(0x00); // Unknown
        buffer.writeInt(0x01); // Unknown
        buffer.writeInt(0x00); // Unknown

        final int size = _castle.getSiege().getDefendersWaiting().size() + _castle.getSiege().getDefenderClans().size() + (_castle.getOwner() != null ? 1 : 0);

        buffer.writeInt(size);
        buffer.writeInt(size);

        // Add owners
        final Clan ownerClan = _castle.getOwner();
        if (ownerClan != null) {
            buffer.writeInt(ownerClan.getId());
            buffer.writeString(ownerClan.getName());
            buffer.writeString(ownerClan.getLeaderName());
            buffer.writeInt(ownerClan.getCrestId());
            buffer.writeInt(0x00); // signed time (seconds) (not storated by L2J)
            buffer.writeInt(SiegeClanType.OWNER.ordinal());
            buffer.writeInt(ownerClan.getAllyId());
            buffer.writeString(ownerClan.getAllyName());
            buffer.writeString(""); // AllyLeaderName
            buffer.writeInt(ownerClan.getAllyCrestId());
        }

        // List of confirmed defenders
        for (var siegeClan : _castle.getSiege().getDefenderClans().values()) {
            final Clan defendingClan = ClanEngine.getInstance().getClan(siegeClan.getClanId());
            if ((defendingClan == null) || (defendingClan == _castle.getOwner())) {
                continue;
            }

            buffer.writeInt(defendingClan.getId());
            buffer.writeString(defendingClan.getName());
            buffer.writeString(defendingClan.getLeaderName());
            buffer.writeInt(defendingClan.getCrestId());
            buffer.writeInt(0x00); // signed time (seconds) (not storated by L2J)
            buffer.writeInt(SiegeClanType.DEFENDER.ordinal());
            buffer.writeInt(defendingClan.getAllyId());
            buffer.writeString(defendingClan.getAllyName());
            buffer.writeString(""); // AllyLeaderName
            buffer.writeInt(defendingClan.getAllyCrestId());
        }

        // List of not confirmed defenders
        for (var siegeClan : _castle.getSiege().getDefendersWaiting().values()) {
            final Clan defendingClan = ClanEngine.getInstance().getClan(siegeClan.getClanId());
            if (defendingClan == null) {
                continue;
            }

            buffer.writeInt(defendingClan.getId());
            buffer.writeString(defendingClan.getName());
            buffer.writeString(defendingClan.getLeaderName());
            buffer.writeInt(defendingClan.getCrestId());
            buffer.writeInt(0x00); // signed time (seconds) (not storated by L2J)
            buffer.writeInt(SiegeClanType.DEFENDER_PENDING.ordinal());
            buffer.writeInt(defendingClan.getAllyId());
            buffer.writeString(defendingClan.getAllyName());
            buffer.writeString(""); // AllyLeaderName
            buffer.writeInt(defendingClan.getAllyCrestId());
        }
    }
}
