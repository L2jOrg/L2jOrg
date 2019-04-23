package handlers.effecthandlers;

import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stats;

/**
 * @author Mobius
 */
public class HitAtNight extends AbstractStatEffect
{
	public HitAtNight(StatsSet params)
	{
		super(params, Stats.HIT_AT_NIGHT);
	}
	
	@Override
	public void onStart(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		GameTimeController.getInstance().addShadowSenseCharacter(effected);
	}
	
	@Override
	public void onExit(L2Character effector, L2Character effected, Skill skill)
	{
		GameTimeController.getInstance().removeShadowSenseCharacter(effected);
	}
}
