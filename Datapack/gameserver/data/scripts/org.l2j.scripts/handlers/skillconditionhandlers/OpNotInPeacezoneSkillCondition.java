package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.zone.ZoneId;

/**
 * @author Liamxroy
 */
public class OpNotInPeacezoneSkillCondition implements ISkillCondition
{
	public OpNotInPeacezoneSkillCondition(StatsSet params)
	{
	}
	
	@Override
	public boolean canUse(L2Character caster, Skill skill,  L2Object target)
	{
		return !caster.isInsideZone(ZoneId.PEACE);
	}
}
