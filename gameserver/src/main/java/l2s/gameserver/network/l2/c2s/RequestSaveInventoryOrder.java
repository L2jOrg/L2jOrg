package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public class RequestSaveInventoryOrder extends L2GameClientPacket
{
	// format: (ch)db, b - array of (dd)
	int[][] _items;

	@Override
	protected void readImpl()
	{
		int size = readD();
		if(size > 125)
			size = 125;
		if(size * 8 > _buf.remaining() || size < 1)
		{
			_items = null;
			return;
		}
		_items = new int[size][2];
		for(int i = 0; i < size; i++)
		{
			_items[i][0] = readD(); // item id
			_items[i][1] = readD(); // slot
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