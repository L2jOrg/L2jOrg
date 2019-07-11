package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 */
public class ExDuelStart extends ServerPacket {
    public static final ExDuelStart PLAYER_DUEL = new ExDuelStart(false);
    public static final ExDuelStart PARTY_DUEL = new ExDuelStart(true);

    private final int _partyDuel;

    public ExDuelStart(boolean isPartyDuel) {
        _partyDuel = isPartyDuel ? 1 : 0;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_DUEL_START);

        writeInt(_partyDuel);
    }

}
