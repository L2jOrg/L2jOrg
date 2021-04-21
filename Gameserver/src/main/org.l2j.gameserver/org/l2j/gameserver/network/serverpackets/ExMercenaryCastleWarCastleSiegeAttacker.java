package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExMercenaryCastleWarCastleSiegeAttacker extends ServerPacket {
    private final Castle _castle;

    public ExMercenaryCastleWarCastleSiegeAttacker(Castle castle) {
        _castle = castle;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.CASTLE_SIEGE_ATTACKER_LIST, buffer );

        buffer.writeInt(_castle.getId());
        buffer.writeInt(0x00); // 0
        buffer.writeInt(0x01); // 1
        buffer.writeInt(0x00); // 0
        final int size = _castle.getSiege().getAttackerClans().size();
        if (size > 0) {
            Clan clan;

            buffer.writeInt(size);
            buffer.writeInt(size);
            for (var siegeclan : _castle.getSiege().getAttackerClans().values()) {
                clan = ClanEngine.getInstance().getClan(siegeclan.getClanId());
                if (clan == null) {
                    continue;
                }

                buffer.writeInt(clan.getId());
                buffer.writeString(clan.getName());
                buffer.writeString(clan.getLeaderName());
                buffer.writeInt(clan.getCrestId());
                buffer.writeInt(0x00); // signed time (seconds) (not storated by L2J)
                buffer.writeInt(clan.getAllyId());
                buffer.writeString(clan.getAllyName());
                buffer.writeString(""); // AllyLeaderName
                buffer.writeInt(clan.getAllyCrestId());
            }
        } else {
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
        }
    }
}
