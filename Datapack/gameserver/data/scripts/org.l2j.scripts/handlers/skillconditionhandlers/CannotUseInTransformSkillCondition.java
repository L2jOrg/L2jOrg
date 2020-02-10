package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.Node;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class CannotUseInTransformSkillCondition implements SkillCondition {

	public final int id;

	private CannotUseInTransformSkillCondition(int id) {
		this.id = id;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return id > 0 ? caster.getTransformationId() != id : !caster.isTransformed();
	}

	public static final class Factory  extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			return new CannotUseInTransformSkillCondition(parseInt(xmlNode.getAttributes(), "id"));
		}

		@Override
		public String conditionName() {
			return "non-transformed";
		}
	}
}