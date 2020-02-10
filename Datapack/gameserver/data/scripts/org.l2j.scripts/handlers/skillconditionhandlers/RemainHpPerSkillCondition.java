package handlers.skillconditionhandlers;

import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.enums.SkillConditionPercentType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author UnAfraid
 */
public class RemainHpPerSkillCondition implements SkillCondition {

	public final int _amount;
	public final SkillConditionPercentType _percentType;
	public final SkillConditionAffectType _affectType;
	
	public RemainHpPerSkillCondition(StatsSet params) {
		_amount = params.getInt("amount");
		_percentType = params.getEnum("percentType", SkillConditionPercentType.class);
		_affectType = params.getEnum("affectType", SkillConditionAffectType.class);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return switch (_affectType) {
			case CASTER -> _percentType.test(caster.getCurrentHpPercent(), _amount);
			case TARGET -> isCreature(target) && _percentType.test(((Creature) target).getCurrentHpPercent(), _amount);
			default -> false;
		};
	}
}
