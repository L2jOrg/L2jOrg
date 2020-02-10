package handlers.skillconditionhandlers;

import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.enums.SkillConditionAlignment;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class OpAlignmentSkillCondition implements SkillCondition {

	private final SkillConditionAffectType affectType;
	private final SkillConditionAlignment alignment;
	
	public OpAlignmentSkillCondition(StatsSet params) {
		affectType = params.getEnum("affectType", SkillConditionAffectType.class);
		alignment = params.getEnum("alignment", SkillConditionAlignment.class);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return switch (affectType) {
			case CASTER -> alignment.test(caster.getActingPlayer());
			case TARGET -> isPlayer(target) && alignment.test(target.getActingPlayer());
			default -> false;
		};
	}
}
