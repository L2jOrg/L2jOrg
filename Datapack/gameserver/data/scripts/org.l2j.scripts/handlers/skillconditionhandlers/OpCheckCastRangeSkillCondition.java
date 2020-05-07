package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.MathUtil;

/**
 * @author Mobius
 */
public class OpCheckCastRangeSkillCondition implements SkillCondition
{
	private final int _distance;
	
	public OpCheckCastRangeSkillCondition(StatsSet params)
	{
		_distance = params.getInt("distance");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		return (target != null) //
			&& (!MathUtil.isInsideRadius3D(caster,  target, _distance)) //
			&& GeoEngine.getInstance().canSeeTarget(caster, target);
	}
}
