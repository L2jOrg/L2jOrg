package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExMercenaryCastleWarCastleSiegeInfo extends ServerPacket {
    private final int _castleId;

    public ExMercenaryCastleWarCastleSiegeInfo(int castleId)
    {
        _castleId = castleId;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)
    {
        writeId(ServerExPacketId.EX_MERCENARY_CASTLEWAR_CASTLE_SIEGE_INFO, buffer );

        Castle castle = CastleManager.getInstance().getCastleById(_castleId);

        buffer.writeInt(_castleId);
        if (castle == null)
        {
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
            buffer.writeString("-");
            buffer.writeString("-");
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
        }
        else
        {
            buffer.writeInt(0x00); // seconds ?
            buffer.writeInt(0x00); // Is that crest?

            buffer.writeString(castle.getOwner() != null ? castle.getOwner().getName() : "-");
            buffer.writeString(castle.getOwner() != null ? castle.getOwner().getLeaderName() : "-");

            buffer.writeInt(0x00); // Is that crest?
            buffer.writeInt(castle.getSiege().getAttackerClans().size());
            buffer.writeInt(castle.getSiege().getDefenderClans().size());
        }
    }
}
