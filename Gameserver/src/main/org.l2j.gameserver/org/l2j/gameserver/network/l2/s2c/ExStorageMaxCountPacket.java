package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExStorageMaxCountPacket extends L2GameServerPacket
{
	private int _inventory;
	private int _warehouse;
	private int _clan;
	private int _privateSell;
	private int _privateBuy;
	private int _recipeDwarven;
	private int _recipeCommon;
	private int _inventoryExtraSlots;
	private int _questItemsLimit;

	public ExStorageMaxCountPacket(Player player)
	{
		_inventory = player.getInventoryLimit();
		_warehouse = player.getWarehouseLimit();
		_clan = Config.WAREHOUSE_SLOTS_CLAN;
		_privateBuy = _privateSell = player.getTradeLimit();
		_recipeDwarven = player.getDwarvenRecipeLimit();
		_recipeCommon = player.getCommonRecipeLimit();
		_inventoryExtraSlots = player.getBeltInventoryIncrease();
		_questItemsLimit = Config.QUEST_INVENTORY_MAXIMUM;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_inventory);
		buffer.putInt(_warehouse);
		buffer.putInt(_clan);
		buffer.putInt(_privateSell);
		buffer.putInt(_privateBuy);
		buffer.putInt(_recipeDwarven);
		buffer.putInt(_recipeCommon);
		buffer.putInt(_inventoryExtraSlots); // belt inventory slots increase count
		buffer.putInt(_questItemsLimit); //  quests list  by off 100 maximum
		buffer.putInt(40); // ??? 40 slots
		buffer.putInt(40); // ??? 40 slots
		buffer.putInt(0x64); // artfact slots (fixed)
	}
}