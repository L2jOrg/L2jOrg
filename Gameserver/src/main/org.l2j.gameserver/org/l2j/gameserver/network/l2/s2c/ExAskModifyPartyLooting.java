package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExAskModifyPartyLooting extends L2GameServerPacket
{
	private String _requestor;
	private int _mode;

	public ExAskModifyPartyLooting(String name, int mode)
	{
		_requestor = name;
		_mode = mode;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_requestor, buffer);
		buffer.putInt(_mode);
	}
}
