package l2s.gameserver.ai;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;

public class Guard extends Fighter
{
	public Guard(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean canAttackCharacter(Creature target)
	{
		NpcInstance actor = getActor();
		if(target.isPlayable())
		{
			if(!target.isPK() || (actor.getParameter("evilGuard", false) && target.getPvpFlag() > 0))
				return false;
			return true;
		}
		return false;
	}

	@Override
	public boolean checkTarget(Creature target, int range)
	{
		// Обходим HATE.
		return super.checkTarget(target, range) && canAttackCharacter(target);
	}

	@Override
	protected boolean maybeMoveToHome(boolean force)
	{
		return returnHome(true);
	}

	@Override
	protected void onEvtArrived()
	{
		super.onEvtArrived();

		NpcInstance actor = getActor();
		if(actor.isInRangeZ(actor.getSpawnedLoc(), 50))
			actor.setHeading(actor.getSpawnedLoc().h, true);
	}
}