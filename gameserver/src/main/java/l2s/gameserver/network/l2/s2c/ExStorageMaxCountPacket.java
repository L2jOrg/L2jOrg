package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;

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
	protected final void writeImpl()
	{
		writeD(_inventory);
		writeD(_warehouse);
		writeD(_clan);
		writeD(_privateSell);
		writeD(_privateBuy);
		writeD(_recipeDwarven);
		writeD(_recipeCommon);
		writeD(_inventoryExtraSlots); // belt inventory slots increase count
		writeD(_questItemsLimit); //  quests list  by off 100 maximum
		writeD(40); // ??? 40 slots
		writeD(40); // ??? 40 slots
	}
}