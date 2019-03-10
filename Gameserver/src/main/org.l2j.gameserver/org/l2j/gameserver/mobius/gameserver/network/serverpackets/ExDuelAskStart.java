package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class ExDuelAskStart extends IClientOutgoingPacket {
    private final String _requestorName;
    private final int _partyDuel;

    public ExDuelAskStart(String requestor, int partyDuel) {
        _requestorName = requestor;
        _partyDuel = partyDuel;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_DUEL_ASK_START.writeId(packet);

        writeString(_requestorName, packet);
        packet.putInt(_partyDuel);
    }
}
