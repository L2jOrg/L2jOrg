package org.l2j.gameserver.listener.inventory;

import org.l2j.commons.listener.Listener;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.items.ItemInstance;

public interface OnEquipListener extends Listener<Playable>
{
	public void onEquip(int slot, ItemInstance item, Playable actor);

	public void onUnequip(int slot, ItemInstance item, Playable actor);
}