package l2s.gameserver.network.l2.s2c;

public class MagicSkillCanceled extends L2GameServerPacket
{

	private int _objectId;

	public MagicSkillCanceled(int objectId)
	{
		_objectId = objectId;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objectId);
	}
}