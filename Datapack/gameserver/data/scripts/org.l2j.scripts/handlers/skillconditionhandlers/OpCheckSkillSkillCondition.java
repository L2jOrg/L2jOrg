package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 */
public class OpCheckSkillSkillCondition implements SkillCondition
{
	private final int _skillId;
	private final SkillConditionAffectType _affectType;
	
	public OpCheckSkillSkillCondition(StatsSet params)
	{
		_skillId = params.getInt("skillId");
		_affectType = params.getEnum("affectType", SkillConditionAffectType.class);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		switch (_affectType)
		{
			case CASTER:
			{
				return caster.getSkillLevel(_skillId) > 0;
			}
			case TARGET:
			{
				if ((target != null) && !isPlayer(target))
				{
					return target.getActingPlayer().getSkillLevel(_skillId) > 0;
				}
				break;
			}
		}
		return false;
	}
}
