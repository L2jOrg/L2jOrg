package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * Author: VISTALL
 */
public class ExSubPledgetSkillAdd extends L2GameServerPacket
{
	private int _type, _id, _level;

	public ExSubPledgetSkillAdd(int type, int id, int level)
	{
		_type = type;
		_id = id;
		_level = level;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_type);
		buffer.putInt(_id);
		buffer.putInt(_level);
	}
}