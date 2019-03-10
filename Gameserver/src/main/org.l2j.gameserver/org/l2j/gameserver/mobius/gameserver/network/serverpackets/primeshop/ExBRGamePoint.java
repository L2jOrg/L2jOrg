package org.l2j.gameserver.mobius.gameserver.network.serverpackets.primeshop;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Gnacik, UnAfraid
 */
public class ExBRGamePoint extends IClientOutgoingPacket {
    private final int _charId;
    private final int _charPoints;

    public ExBRGamePoint(L2PcInstance player) {
        _charId = player.getObjectId();
        _charPoints = player.getPrimePoints();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BR_GAME_POINT.writeId(packet);

        packet.putInt(_charId);
        packet.putLong(_charPoints);
        packet.putInt(0x00);
    }
}
