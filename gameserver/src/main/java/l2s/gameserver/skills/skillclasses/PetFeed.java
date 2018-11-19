package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.player.Mount;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.templates.StatsSet;

public class PetFeed extends Skill
{
	private int[] _feedPower;

	public PetFeed(StatsSet set)
	{
		super(set);
		_feedPower = set.getIntegerArray("feed_power");
	}

	@Override
	public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		if(!target.isPet() && !(target.isPlayer() && target.getPlayer().isMounted()))
			return false;

		return true;
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(target.isPet())
		{
			PetInstance pet = (PetInstance) target;
			int feedPowerIndx = Math.min(_feedPower.length - 1, pet.getFormId());
			int power = _feedPower[feedPowerIndx];
			pet.setCurrentFed(pet.getCurrentFed() + power, true);
			pet.sendStatusUpdate();
		}
		else if(target.isPlayer() && target.getPlayer().isMounted())
		{
			Mount mount = target.getPlayer().getMount();
			int feedPowerIndx = Math.min(_feedPower.length - 1, mount.getFormId());
			int power = _feedPower[feedPowerIndx];
			mount.setCurrentFeed(mount.getCurrentFeed() + power);
			mount.updateStatus();
		}
	}
}
