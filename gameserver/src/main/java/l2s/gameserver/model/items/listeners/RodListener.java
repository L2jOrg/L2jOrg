package l2s.gameserver.model.items.listeners;

import l2s.gameserver.listener.inventory.OnEquipListener;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;

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