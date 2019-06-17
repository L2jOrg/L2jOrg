package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 */
public class ExAskJoinPartyRoom extends ServerPacket {
    private final String _charName;
    private final String _roomName;

    public ExAskJoinPartyRoom(L2PcInstance player) {
        _charName = player.getName();
        _roomName = player.getMatchingRoom().getTitle();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_ASK_JOIN_PARTY_ROOM);

        writeString(_charName);
        writeString(_roomName);
    }

}
