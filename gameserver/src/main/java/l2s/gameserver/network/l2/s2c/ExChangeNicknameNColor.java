package l2s.gameserver.network.l2.s2c;

public class ExChangeNicknameNColor extends L2GameServerPacket
{
	private int _itemObjId;

	public ExChangeNicknameNColor(int itemObjId)
	{
		_itemObjId = itemObjId;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_itemObjId);
	}
}