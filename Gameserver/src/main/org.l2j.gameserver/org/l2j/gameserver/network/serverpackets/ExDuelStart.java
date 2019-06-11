package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class ExDuelStart extends IClientOutgoingPacket {
    public static final ExDuelStart PLAYER_DUEL = new ExDuelStart(false);
    public static final ExDuelStart PARTY_DUEL = new ExDuelStart(true);

    private final int _partyDuel;

    public ExDuelStart(boolean isPartyDuel) {
        _partyDuel = isPartyDuel ? 1 : 0;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_DUEL_START);

        writeInt(_partyDuel);
    }

}
