package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.Clan;
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
public class TargetMyPledgeSkillCondition implements SkillCondition {
	private TargetMyPledgeSkillCondition() {
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		if (!isPlayer(target))
		{
			return false;
		}
		final Clan clan = caster.getClan();
		return (clan != null) && (clan == target.getActingPlayer().getClan());
	}

	public static final class  Factory extends SkillConditionFactory {
		private static final TargetMyPledgeSkillCondition INSTANCE  = new TargetMyPledgeSkillCondition();

		@Override
		public SkillCondition create(Node xmlNode) {
			return INSTANCE;
		}

		@Override
		public String conditionName() {
			return "TargetMyPledge";
		}
	}
}
