package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.w3c.dom.Node;

import static org.l2j.commons.util.Util.isBetween;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class CheckLevelSkillCondition implements SkillCondition {
	public final int minLevel;
	public final int maxLevel;
	public final SkillConditionAffectType affectType;

	private CheckLevelSkillCondition(SkillConditionAffectType affect, int minLevel, int maxLevel) {
		this.affectType = affect;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return switch (affectType) {
			case CASTER -> isBetween(caster.getLevel(), minLevel, maxLevel);
			case TARGET -> isPlayer(target) && isBetween(target.getActingPlayer().getLevel(), minLevel, maxLevel);
			default -> false;
		};
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			var attr = xmlNode.getAttributes();
			return new CheckLevelSkillCondition(parseEnum(attr, SkillConditionAffectType.class, "affect"), parseInt(attr, "min-level"), parseInt(attr, "max-level"));
		}

		@Override
		public String conditionName() {
			return "level";
		}
	}
}
