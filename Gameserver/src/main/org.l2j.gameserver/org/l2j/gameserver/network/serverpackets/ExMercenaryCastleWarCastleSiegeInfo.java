package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Objects;

import static java.util.Objects.nonNull;

public class ExMercenaryCastleWarCastleSiegeInfo extends ServerPacket {

    private final Castle castle;

    public ExMercenaryCastleWarCastleSiegeInfo(int castleId) {
        castle = Objects.requireNonNull(CastleManager.getInstance().getCastleById(castleId));
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)
    {
        writeId(ServerExPacketId.EX_MERCENARY_CASTLEWAR_CASTLE_SIEGE_INFO, buffer );

        buffer.writeInt(castle.getId());

        final var owner = castle.getOwner();
        if(nonNull(owner)) {
            buffer.writeInt(owner.getId());
            buffer.writeInt(owner.getCrestId());
            buffer.writeSizedString(owner.getName());
            buffer.writeSizedString(owner.getLeaderName());
        } else {
            buffer.writeInt(0);
            buffer.writeInt(0);
            buffer.writeSizedString("-");
            buffer.writeSizedString("-");
        }

        buffer.writeInt(castle.getSiege().isInProgress()); // siege state
        buffer.writeInt(castle.getSiege().getAttackerClans().size());
        buffer.writeInt(castle.getSiege().getDefenderClans().size());
    }
}
