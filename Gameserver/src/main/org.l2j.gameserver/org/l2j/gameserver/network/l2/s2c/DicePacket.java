package org.l2j.gameserver.network.l2.s2c;

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
	protected final void writeImpl()
	{
		writeInt(_playerId); // object id of player
		writeInt(_itemId); //	item id of dice (spade)  4625,4626,4627,4628
		writeInt(_number); // number rolled
		writeInt(_x); // x
		writeInt(_y); // y
		writeInt(_z); // z
	}
}