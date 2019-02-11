package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExRotation extends L2GameServerPacket
{
	private int _charObjId, _degree;

	public ExRotation(int charId, int degree)
	{
		_charObjId = charId;
		_degree = degree;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_charObjId);
		buffer.putInt(_degree);
	}
}
