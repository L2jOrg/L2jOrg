package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class SoulSavedSkillCondition implements SkillCondition {

	public final int amount;

	private SoulSavedSkillCondition(int amount) {
		this.amount = amount;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return isPlayer(caster) && caster.getActingPlayer().getChargedSouls() >= amount;
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			return new SoulSavedSkillCondition(parseInt(xmlNode.getAttributes(), "amount"));
		}

		@Override
		public String conditionName() {
			return "soul";
		}
	}
}
