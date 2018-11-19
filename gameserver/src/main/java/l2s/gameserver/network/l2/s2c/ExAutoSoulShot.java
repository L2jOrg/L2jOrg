package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.base.SoulShotType;

public class ExAutoSoulShot extends L2GameServerPacket
{
	private final int _itemId;
	private final int _slotId;
	private final int _type;

	public ExAutoSoulShot(int itemId, int slotId, SoulShotType type)
	{
		_itemId = itemId;
		_slotId = slotId;
		_type = type.ordinal();

	}

	@Override
	protected final void writeImpl()
	{
		writeD(_itemId);
		writeD(_slotId);
		writeD(_type);
	}
}