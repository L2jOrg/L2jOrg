package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author UnAfraid
 */
public class ExCastleState extends ServerPacket {
    private final int _castleId;
    private final CastleSide _castleSide;

    public ExCastleState(Castle castle) {
        _castleId = castle.getId();
        _castleSide = castle.getSide();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_CASTLE_STATE);

        writeInt(_castleId);
        writeInt(_castleSide.ordinal());
    }

}
