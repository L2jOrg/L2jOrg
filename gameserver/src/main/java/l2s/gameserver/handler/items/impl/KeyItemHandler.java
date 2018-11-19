package l2s.gameserver.handler.items.impl;

import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class KeyItemHandler extends DefaultItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = playable.getPlayer();
		GameObject target = player.getTarget();
		if(target == null || !target.isDoor())
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}
		DoorInstance door = (DoorInstance) target;
		if(door.isOpen())
		{
			player.sendPacket(SystemMsg.IT_IS_NOT_LOCKED);
			return false;
		}
		if(door.getKey() <= 0 || item.getItemId() != door.getKey() || door.isLockOpen()) // ключ не подходит к двери
		{
			player.sendPacket(SystemMsg.THIS_DOOR_CANNOT_BE_UNLOCKED);
			return false;
		}
		if(player.getDistance(door) > 300)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_BECAUSE_YOU_ARE_TOO_FAR);
			return false;
		}
		if(!player.getInventory().destroyItem(item, 1L))
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return false;
		}
		player.sendPacket(SystemMessagePacket.removeItems(item.getItemId(), 1));
		door.openMe(player, true);
		return true;
	}
}