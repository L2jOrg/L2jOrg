package org.l2j.gameserver.model.items.listeners;

import org.l2j.gameserver.listener.inventory.OnEquipListener;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.Inventory;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.templates.item.WeaponTemplate.WeaponType;

/**
 * @author Bonux
 **/
public final class RodListener implements OnEquipListener
{
	private static final RodListener _instance = new RodListener();

	public static RodListener getInstance()
	{
		return _instance;
	}

	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		if(!actor.isPlayer())
			return;

		if(!item.isEquipable() || slot != Inventory.PAPERDOLL_RHAND)
			return;

		if(item.getItemType() != WeaponType.ROD)
			return;

		Player player = actor.getPlayer();
		if(player.isFishing())
			player.getFishing().stop();
	}

	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		//
	}
}