package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ShowTownMapPacket extends L2GameServerPacket
{
	/**
	 * Format: csdd
	 */

	String _texture;
	int _x;
	int _y;

	public ShowTownMapPacket(String texture, int x, int y)
	{
		_texture = texture;
		_x = x;
		_y = y;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_texture, buffer);
		buffer.putInt(_x);
		buffer.putInt(_y);
	}
}