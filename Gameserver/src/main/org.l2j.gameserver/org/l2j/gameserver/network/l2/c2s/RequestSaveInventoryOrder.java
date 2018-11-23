package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

public class RequestSaveInventoryOrder extends L2GameClientPacket
{
	// format: (ch)db, b - array of (dd)
	int[][] _items;

	@Override
	protected void readImpl()
	{
		int size = readInt();
		if(size > 125)
			size = 125;
		if(size * 8 > availableData() || size < 1)
		{
			_items = null;
			return;
		}
		_items = new int[size][2];
		for(int i = 0; i < size; i++)
		{
			_items[i][0] = readInt(); // item id
			_items[i][1] = readInt(); // slot
		}
	}

	@Override
	protected void runImpl()
	{
		if(_items == null)
			return;
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		activeChar.getInventory().sort(_items);
	}
}