package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.actor.instances.player.Mount;
import org.l2j.gameserver.model.instances.PetInstance;
import org.l2j.gameserver.stats.Env;

public class ConditionTargetPetFeed extends Condition
{
	private final int _itemId;

	public ConditionTargetPetFeed(int itemId)
	{
		_itemId = itemId;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature target = env.character;
		if(target.isPet())
		{
			PetInstance pet = (PetInstance) target;
			return pet.isMyFeed(_itemId);
		}

		if(target.isPlayer() && target.getPlayer().isMounted())
		{
			Mount mount = target.getPlayer().getMount();
			return mount.isMyFeed(_itemId);
		}
		return false;
	}
}
