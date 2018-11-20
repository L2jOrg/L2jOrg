package org.l2j.gameserver.network.l2.s2c;

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
	protected final void writeImpl()
	{
		writeString(_texture);
		writeInt(_x);
		writeInt(_y);
	}
}