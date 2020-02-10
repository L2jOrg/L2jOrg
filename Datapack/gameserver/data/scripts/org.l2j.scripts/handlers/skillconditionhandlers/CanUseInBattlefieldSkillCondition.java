package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

public class CanUseInBattlefieldSkillCondition implements SkillCondition {

	private CanUseInBattlefieldSkillCondition() {
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		return (caster != null) && caster.isInsideZone(ZoneType.SIEGE);
	}

	public static final class Factory extends SkillConditionFactory {
		private static final CanUseInBattlefieldSkillCondition INSTANCE = new CanUseInBattlefieldSkillCondition();

		@Override
		public SkillCondition create(Node xmlNode) {
			return INSTANCE;
		}

		@Override
		public String conditionName() {
			return "CanUseInBattlefield";
		}
	}
}
