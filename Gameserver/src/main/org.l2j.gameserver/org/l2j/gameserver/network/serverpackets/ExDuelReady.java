package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class ExDuelReady extends IClientOutgoingPacket {
    public static final ExDuelReady PLAYER_DUEL = new ExDuelReady(false);
    public static final ExDuelReady PARTY_DUEL = new ExDuelReady(true);

    private final int _partyDuel;

    public ExDuelReady(boolean isPartyDuel) {
        _partyDuel = isPartyDuel ? 1 : 0;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_DUEL_READY.writeId(packet);

        packet.putInt(_partyDuel);
    }

    @Override
    protected int size(L2GameClient client) {
        return 9;
    }
}
