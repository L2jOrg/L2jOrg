package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class GMViewWarehouseWithdrawListPacket extends L2GameServerPacket
{
	private final ItemInstance[] _items;
	private String _charName;
	private long _charAdena;

	public GMViewWarehouseWithdrawListPacket(Player cha)
	{
		_charName = cha.getName();
		_charAdena = cha.getWarehouse().getAdena();
		_items = cha.getWarehouse().getItems();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_charName, buffer);
		buffer.putLong(_charAdena);
		buffer.putShort((short) _items.length);
		for(ItemInstance temp : _items)
		{
			writeItemInfo(buffer, temp);
			buffer.putInt(temp.getObjectId());
		}
	}
}