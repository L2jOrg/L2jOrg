package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author KenM
 */
public class ExAskJoinPartyRoom extends ServerPacket {
    private final String _charName;
    private final String _roomName;

    public ExAskJoinPartyRoom(Player player) {
        _charName = player.getName();
        _roomName = player.getMatchingRoom().getTitle();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_ASK_JOIN_PARTY_ROOM);

        writeString(_charName);
        writeString(_roomName);
    }

}
