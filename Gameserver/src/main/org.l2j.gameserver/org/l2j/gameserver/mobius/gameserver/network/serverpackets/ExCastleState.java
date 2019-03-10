package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.enums.CastleSide;
import org.l2j.gameserver.mobius.gameserver.model.entity.Castle;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CASTLE_STATE.writeId(packet);

        packet.putInt(_castleId);
        packet.putInt(_castleSide.ordinal());
    }
}
