package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author KenM
 */
public class ExDuelAskStart extends ServerPacket {
    private final String _requestorName;
    private final int _partyDuel;

    public ExDuelAskStart(String requestor, int partyDuel) {
        _requestorName = requestor;
        _partyDuel = partyDuel;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_DUEL_ASK_START);

        writeString(_requestorName);
        writeInt(_partyDuel);
    }

}
