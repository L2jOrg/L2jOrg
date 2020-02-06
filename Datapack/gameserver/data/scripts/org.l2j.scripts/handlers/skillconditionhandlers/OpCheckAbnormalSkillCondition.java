package handlers.skillconditionhandlers;

import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author UnAfraid
 */
public class OpCheckAbnormalSkillCondition implements ISkillCondition {
	public final AbnormalType type;
	public final int level;
	public final boolean hasAbnormal;
	public final SkillConditionAffectType affectType;
	
	public OpCheckAbnormalSkillCondition(StatsSet params) {
		type = params.getEnum("type", AbnormalType.class);
		level = params.getInt("level");
		hasAbnormal = params.getBoolean("hasAbnormal");
		affectType = params.getEnum("affectType", SkillConditionAffectType.class, SkillConditionAffectType.TARGET);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return switch (affectType) {
			case CASTER -> caster.getEffectList().hasAbnormalType(type, info -> (info.getSkill().getAbnormalLvl() >= level)) == hasAbnormal;
			case TARGET -> isCreature(target) && ((Creature) target).getEffectList().hasAbnormalType(type, info -> (info.getSkill().getAbnormalLvl() >= level)) == hasAbnormal;
			default -> false;
		};
	}
}
