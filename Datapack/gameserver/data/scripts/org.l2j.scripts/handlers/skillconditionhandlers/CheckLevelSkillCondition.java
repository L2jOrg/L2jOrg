package handlers.skillconditionhandlers;

import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.commons.util.Util.isBetween;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class CheckLevelSkillCondition implements ISkillCondition {
	public final int minLevel;
	public final int maxLevel;
	public final SkillConditionAffectType affectType;
	
	public CheckLevelSkillCondition(StatsSet params) {
		minLevel = params.getInt("minLevel");
		maxLevel = params.getInt("maxLevel");
		affectType = params.getEnum("affectType", SkillConditionAffectType.class);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return switch (affectType) {
			case CASTER -> isBetween(caster.getLevel(), minLevel, maxLevel);
			case TARGET -> isPlayer(target) && isBetween(target.getActingPlayer().getLevel(), minLevel, maxLevel);
			default -> false;
		};
	}
}
