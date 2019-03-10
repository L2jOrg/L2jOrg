package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.enums.ChatType;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class Snoop extends IClientOutgoingPacket {
    private final int _convoId;
    private final String _name;
    private final ChatType _type;
    private final String _speaker;
    private final String _msg;

    public Snoop(int id, String name, ChatType type, String speaker, String msg) {
        _convoId = id;
        _name = name;
        _type = type;
        _speaker = speaker;
        _msg = msg;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SNOOP.writeId(packet);

        packet.putInt(_convoId);
        writeString(_name, packet);
        packet.putInt(0x00); // ??
        packet.putInt(_type.getClientId());
        writeString(_speaker, packet);
        writeString(_msg, packet);
    }
}