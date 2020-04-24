package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author KenM
 */
public class ExDuelReady extends ServerPacket {
    public static final ExDuelReady PLAYER_DUEL = new ExDuelReady(false);
    public static final ExDuelReady PARTY_DUEL = new ExDuelReady(true);

    private final int _partyDuel;

    public ExDuelReady(boolean isPartyDuel) {
        _partyDuel = isPartyDuel ? 1 : 0;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_DUEL_READY);

        writeInt(_partyDuel);
    }

}
