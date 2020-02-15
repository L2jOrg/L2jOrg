package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Chest;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class OpUnlockSkillCondition implements SkillCondition {

	private OpUnlockSkillCondition() {
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		return (target != null) && (isDoor(target) || (target instanceof Chest));
	}

	public static final class Factory extends SkillConditionFactory {
		private static final OpUnlockSkillCondition INSTANCE = new OpUnlockSkillCondition();

		@Override
		public SkillCondition create(Node xmlNode) {
			return INSTANCE;
		}

		@Override
		public String conditionName() {
			return "OpUnlock";
		}
	}
}
