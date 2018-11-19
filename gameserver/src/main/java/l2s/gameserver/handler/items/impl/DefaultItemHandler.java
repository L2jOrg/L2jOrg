package l2s.gameserver.handler.items.impl;

import l2s.gameserver.handler.items.IItemHandler;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.Log;

/**
 * @author VISTALL
 * @date 21:09/12.07.2011
 */
public class DefaultItemHandler implements IItemHandler
{
	@Override
	public SystemMsg checkCondition(Playable playable, ItemInstance item)
	{
		return null;
	}

	@Override
	public boolean forceUseItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		return false;
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		return false;
	}

	@Override
	public void dropItem(Player player, ItemInstance item, long count, Location loc)
	{
		if(item.isEquipped())
		{
			player.getInventory().unEquipItem(item);
			player.sendUserInfo(true);
		}

		item = player.getInventory().removeItemByObjectId(item.getObjectId(), count);
		if(item == null)
		{
			player.sendActionFailed();
			return;
		}

		Log.LogItem(player, Log.Drop, item);

		item.dropToTheGround(player, loc);
		player.disableDrop(1000);

		player.sendChanges();
	}

	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}

	@Override
	public void onRestoreItem(Playable playable, ItemInstance item)
	{
		//
	}

	@Override
	public void onAddItem(Playable playable, ItemInstance item)
	{
		//
	}

	@Override
	public void onRemoveItem(Playable playable, ItemInstance item)
	{
		//
	}

	@Override
	public boolean isAutoUse()
	{
		return false;
	}

	public static void sendUseMessage(Playable playable, int itemId)
	{
		if(!playable.isPlayer())
			return;

		playable.getPlayer().sendPacket(new SystemMessagePacket(SystemMsg.YOU_USE_S1).addItemName(itemId));
	}

	public static void sendUseMessage(Playable playable, ItemInstance item)
	{
		sendUseMessage(playable, item.getItemId());
	}

	public static boolean reduceItem(Playable playable, ItemInstance item)
	{
		if(playable.getInventory().destroyItem(item, 1))
			return true;

		//if(playable.isPlayer())
			//playable.getPlayer().sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
		return false; // Такого произойти недолжно.
	}

	public static boolean canBeExtracted(Player player, ItemInstance item)
	{
		if(player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - 10)
		{
			//TODO: [Bonux] Проверить, правильное ли сообщение.
			player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL, new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}
		return true;
	}
}