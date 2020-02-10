package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

/**
 * @author Mobius
 */
public class CanUseSwoopCannonSkillCondition implements SkillCondition {

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		return caster.isInsideZone(ZoneType.SIEGE);
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			return new CanUseSwoopCannonSkillCondition();
		}

		@Override
		public String conditionName() {
			return "CanUseSwoopCannon";
		}
	}
}
