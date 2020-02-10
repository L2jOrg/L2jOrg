package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.w3c.dom.Node;

import static java.util.Objects.nonNull;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class OpPledgeSkillCondition implements SkillCondition {
	public final int level;

	private OpPledgeSkillCondition(int level) {
		this.level = level;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		final Clan clan = caster.getClan();
		return nonNull(clan) && clan.getLevel() >= level;
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			return new OpPledgeSkillCondition(parseInt(xmlNode.getAttributes(), "level"));
		}

		@Override
		public String conditionName() {
			return "clan";
		}
	}
}
