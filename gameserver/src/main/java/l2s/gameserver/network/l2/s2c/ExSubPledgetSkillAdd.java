package l2s.gameserver.network.l2.s2c;

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
	protected void writeImpl()
	{
		writeD(_type);
		writeD(_id);
		writeD(_level);
	}
}