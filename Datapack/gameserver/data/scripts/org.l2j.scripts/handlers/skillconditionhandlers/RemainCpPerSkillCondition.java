package handlers.skillconditionhandlers;

import org.l2j.gameserver.enums.SkillConditionPercentType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * @author UnAfraid
 */
public class RemainCpPerSkillCondition implements SkillCondition {
	public final int _amount;
	public final SkillConditionPercentType _percentType;
	
	public RemainCpPerSkillCondition(StatsSet params) {
		_amount = params.getInt("amount");
		_percentType = params.getEnum("percentType", SkillConditionPercentType.class);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return _percentType.test(caster.getCurrentCpPercent(), _amount);
	}
}
