package org.l2j.gameserver.network.serverpackets.luckygame;

import org.l2j.gameserver.enums.LuckyGameType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExStartLuckyGame extends IClientOutgoingPacket {
    private final LuckyGameType _type;
    private final int _ticketCount;

    public ExStartLuckyGame(LuckyGameType type, long ticketCount) {
        _type = type;
        _ticketCount = (int) ticketCount;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_START_LUCKY_GAME.writeId(packet);
        packet.putInt(_type.ordinal());
        packet.putInt(_ticketCount);
    }

    @Override
    protected int size(L2GameClient client) {
        return 13;
    }
}
