package org.l2j.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public final class ExEnchantSucess extends L2GameServerPacket
{
	private final int _itemId;

	public ExEnchantSucess(int itemId)
	{
		_itemId = itemId;
	}

	@Override
	protected void writeImpl()
	{
		writeInt(_itemId);
	}
}