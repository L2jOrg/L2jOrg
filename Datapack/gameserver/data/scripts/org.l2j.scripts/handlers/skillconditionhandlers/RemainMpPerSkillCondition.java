package handlers.skillconditionhandlers;

import org.l2j.gameserver.enums.SkillConditionPercentType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

/**
 * @author UnAfraid
 */
public class RemainMpPerSkillCondition implements ISkillCondition {

	public final int _amount;
	public final SkillConditionPercentType _percentType;
	
	public RemainMpPerSkillCondition(StatsSet params) {
		_amount = params.getInt("amount");
		_percentType = params.getEnum("percentType", SkillConditionPercentType.class);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		return _percentType.test(caster.getCurrentMpPercent(), _amount);
	}
}
