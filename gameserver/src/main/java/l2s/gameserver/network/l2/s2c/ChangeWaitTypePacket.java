package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;

/**
 * 0000: 3f 2a 89 00 4c 01 00 00 00 0a 15 00 00 66 fe 00    ?*..L........f..
 * 0010: 00 7c f1 ff ff                                     .|...
 *
 * format   dd ddd
 */
public class ChangeWaitTypePacket extends L2GameServerPacket
{
	private int _objectId;
	private int _moveType;
	private int _x, _y, _z;

	public static final int WT_SITTING = 0;
	public static final int WT_STANDING = 1;
	public static final int WT_START_FAKEDEATH = 2;
	public static final int WT_STOP_FAKEDEATH = 3;

	public ChangeWaitTypePacket(Creature cha, int newMoveType)
	{
		_objectId = cha.getObjectId();
		_moveType = newMoveType;
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objectId);
		writeD(_moveType);
		writeD(_x);
		writeD(_y);
		writeD(_z);
	}
}