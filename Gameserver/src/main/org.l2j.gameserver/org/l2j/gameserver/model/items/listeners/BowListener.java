package org.l2j.gameserver.model.items.listeners;

import org.l2j.gameserver.listener.inventory.OnEquipListener;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.Inventory;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.templates.item.WeaponTemplate.WeaponType;

public final class BowListener implements OnEquipListener
{
	private static final BowListener _instance = new BowListener();

	public static BowListener getInstance()
	{
		return _instance;
	}

	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		//
	}

	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable() || slot != Inventory.PAPERDOLL_RHAND)
			return;

		Player player = (Player) actor;

		if(item.getItemType() == WeaponType.BOW)
		{
			ItemInstance arrow = player.getInventory().findArrowForBow(item.getTemplate());
			if(arrow != null)
				player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, arrow);
		}
		if(item.getItemType() == WeaponType.CROSSBOW || item.getItemType() == WeaponType.TWOHANDCROSSBOW)
		{
			ItemInstance bolt = player.getInventory().findArrowForCrossbow(item.getTemplate());
			if(bolt != null)
				player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, bolt);
		}
	}
}