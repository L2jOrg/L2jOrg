package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 */
public class ExDuelEnd extends ServerPacket {
    public static final ExDuelEnd PLAYER_DUEL = new ExDuelEnd(false);
    public static final ExDuelEnd PARTY_DUEL = new ExDuelEnd(true);

    private final int _partyDuel;

    public ExDuelEnd(boolean isPartyDuel) {
        _partyDuel = isPartyDuel ? 1 : 0;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_DUEL_END);

        writeInt(_partyDuel);
    }

}
