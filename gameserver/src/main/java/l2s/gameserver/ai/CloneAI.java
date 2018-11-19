package l2s.gameserver.ai;

import l2s.gameserver.Config;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.FakePlayer;
import l2s.gameserver.model.Skill;

public class CloneAI extends PlayableAI
{
	public CloneAI(FakePlayer actor)
	{
		super(actor);
	}

	@Override
	protected void thinkActive()
	{
		FakePlayer actor = getActor();

		clearNextAction();
		changeIntention(CtrlIntention.AI_INTENTION_FOLLOW, actor.getPlayer(), Config.FOLLOW_RANGE);
		thinkFollow();		

		super.thinkActive();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		FakePlayer actor = getActor();
		if(attacker == actor.getPlayer())
			return;
			
		Attack(attacker, false, false);
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	public FakePlayer getActor()
	{
		return (FakePlayer) super.getActor();
	}
}