package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.instances.player.Mount;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.stats.Env;

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
