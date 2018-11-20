package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.items.Inventory;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectArmorBreaker extends Effect
{
	private ItemInstance item;

	public EffectArmorBreaker(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isPlayer())
			return false;

		if(getEffected().getPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST) == null)
			return false;

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		item = getEffected().getPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		getEffected().getPlayer().getInventory().unEquipItem(item);
	}

	@Override
	public void onExit()
	{
		getEffected().getPlayer().getInventory().equipItem(item);
	}
}