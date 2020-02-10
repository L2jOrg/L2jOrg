package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.SystemMessageId;
import org.w3c.dom.Node;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class OpEnergyMaxSkillCondition implements SkillCondition {

	public final int charges;

	private OpEnergyMaxSkillCondition(int charges) {
		this.charges = charges;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if (caster.getActingPlayer().getCharges() >= charges) {
			caster.sendPacket(SystemMessageId.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
			return false;
		}
		return true;
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			return new OpEnergyMaxSkillCondition(parseInt(xmlNode.getAttributes(), "charges"));
		}

		@Override
		public String conditionName() {
			return "energy-max";
		}
	}
}
