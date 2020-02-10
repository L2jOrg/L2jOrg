package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class OpNotCursedSkillCondition implements SkillCondition {

	private OpNotCursedSkillCondition() {
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		return isPlayer(caster) && !caster.getActingPlayer().isCursedWeaponEquipped();
	}

	public static final class  Factory extends SkillConditionFactory {
		private static final OpNotCursedSkillCondition INSTANCE = new OpNotCursedSkillCondition();

		@Override
		public SkillCondition create(Node xmlNode) {
			return INSTANCE;
		}

		@Override
		public String conditionName() {
			return "OpNotCursed";
		}
	}
}
