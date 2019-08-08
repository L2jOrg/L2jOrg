package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.world.zone.ZoneType;

/**
 * @author Liamxroy
 */
public class OpNotInPeacezoneSkillCondition implements ISkillCondition
{
	public OpNotInPeacezoneSkillCondition(StatsSet params)
	{
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		return !caster.isInsideZone(ZoneType.PEACE);
	}
}
