package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class SnoopPacket extends L2GameServerPacket
{
    private int _convoID;
    private String _name;
    private int _type;
    private int _fStringId;
    private String _speaker;
    private String _msg;
    private String[] _params;

    public SnoopPacket(int id, String name, int type, String speaker, String msg, int fStringId, String... params)
    {
        _convoID = id;
        _name = name;
        _type = type;
        _speaker = speaker;
        _fStringId = fStringId;
        _params = params;
    }

    public SnoopPacket(int id, String name, int type, String speaker, String msg)
    {
        _convoID = id;
        _name = name;
        _type = type;
        _speaker = speaker;
        _msg = msg;
    }

    @Override
    protected final void writeImpl(GameClient client, ByteBuffer buffer)
    {
        buffer.putInt(_convoID);
        writeString(_name, buffer);
        buffer.putInt(0x00); // ??
        buffer.putInt(_type);
        writeString(_speaker, buffer);
        //buffer.putInt(_fStringId);
		/*for(String param : _params)
			writeString(param, buffer);*/
        writeString(_msg, buffer);
    }
}