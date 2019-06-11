package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.SNOOP);

        writeInt(_convoId);
        writeString(_name);
        writeInt(0x00); // ??
        writeInt(_type.getClientId());
        writeString(_speaker);
        writeString(_msg);
    }

}