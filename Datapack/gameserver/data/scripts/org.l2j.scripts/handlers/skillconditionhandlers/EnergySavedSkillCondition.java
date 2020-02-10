package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.w3c.dom.Node;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class EnergySavedSkillCondition implements SkillCondition {

	public final int charges;

	private EnergySavedSkillCondition(int charges) {
		this.charges = charges;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		return caster.getActingPlayer().getCharges() >= charges;
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			return new EnergySavedSkillCondition(parseInt(xmlNode.getAttributes(), "charges"));
		}

		@Override
		public String conditionName() {
			return "energy-saved";
		}
	}
}
