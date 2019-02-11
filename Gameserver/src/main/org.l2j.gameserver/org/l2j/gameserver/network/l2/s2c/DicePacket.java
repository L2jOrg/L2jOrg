package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class DicePacket extends L2GameServerPacket
{
	private int _playerId;
	private int _itemId;
	private int _number;
	private int _x;
	private int _y;
	private int _z;

	/**
	 * 0xd4 DicePacket         dddddd
	 * @param _characters
	 */
	public DicePacket(int playerId, int itemId, int number, int x, int y, int z)
	{
		_playerId = playerId;
		_itemId = itemId;
		_number = number;
		_x = x;
		_y = y;
		_z = z;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_playerId); // object id of player
		buffer.putInt(_itemId); //	item id of dice (spade)  4625,4626,4627,4628
		buffer.putInt(_number); // number rolled
		buffer.putInt(_x); // x
		buffer.putInt(_y); // y
		buffer.putInt(_z); // z
	}
}