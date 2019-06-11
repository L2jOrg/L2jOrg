package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class ExDuelEnd extends IClientOutgoingPacket {
    public static final ExDuelEnd PLAYER_DUEL = new ExDuelEnd(false);
    public static final ExDuelEnd PARTY_DUEL = new ExDuelEnd(true);

    private final int _partyDuel;

    public ExDuelEnd(boolean isPartyDuel) {
        _partyDuel = isPartyDuel ? 1 : 0;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_DUEL_END);

        writeInt(_partyDuel);
    }

}
