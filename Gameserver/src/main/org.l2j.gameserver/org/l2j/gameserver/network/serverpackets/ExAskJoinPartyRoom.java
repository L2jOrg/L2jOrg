package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class ExAskJoinPartyRoom extends IClientOutgoingPacket {
    private final String _charName;
    private final String _roomName;

    public ExAskJoinPartyRoom(L2PcInstance player) {
        _charName = player.getName();
        _roomName = player.getMatchingRoom().getTitle();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ASK_JOIN_PARTY_ROOM.writeId(packet);

        writeString(_charName, packet);
        writeString(_roomName, packet);
    }
}
