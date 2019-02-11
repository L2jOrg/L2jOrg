package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExChangeNPCState extends L2GameServerPacket
{
	private int _objId;
	private int _state;

	public ExChangeNPCState(int objId, int state)
	{
		_objId = objId;
		_state = state;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objId);
		buffer.putInt(_state);
	}
}
