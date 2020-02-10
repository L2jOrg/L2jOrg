package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.Stat;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class OpSoulMaxSkillCondition implements SkillCondition {

	private OpSoulMaxSkillCondition() {
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		final int maxSouls = (int) caster.getStats().getValue(Stat.MAX_SOULS);
		return isPlayable(caster) && (caster.getActingPlayer().getChargedSouls() < maxSouls);
	}

	public static final class Factory extends SkillConditionFactory {
		private static final OpSoulMaxSkillCondition INSTANCE = new OpSoulMaxSkillCondition();

		@Override
		public SkillCondition create(Node xmlNode) {
			return INSTANCE;
		}

		@Override
		public String conditionName() {
			return "OpSoulMax";
		}
	}
}
