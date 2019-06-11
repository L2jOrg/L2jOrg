package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExCastleState extends IClientOutgoingPacket {
    private final int _castleId;
    private final CastleSide _castleSide;

    public ExCastleState(Castle castle) {
        _castleId = castle.getResidenceId();
        _castleSide = castle.getSide();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_CASTLE_STATE);

        writeInt(_castleId);
        writeInt(_castleSide.ordinal());
    }

}
