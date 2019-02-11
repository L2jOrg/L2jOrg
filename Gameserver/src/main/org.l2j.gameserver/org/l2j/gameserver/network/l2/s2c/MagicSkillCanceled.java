package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class MagicSkillCanceled extends L2GameServerPacket
{

	private int _objectId;

	public MagicSkillCanceled(int objectId)
	{
		_objectId = objectId;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);
	}
}