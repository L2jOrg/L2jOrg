package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import static java.util.Objects.nonNull;

/**
 * @author UnAfraid
 */
public class OpPledgeSkillCondition implements ISkillCondition {
	public final int level;
	
	public OpPledgeSkillCondition(StatsSet params)
	{
		level = params.getInt("level");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		final Clan clan = caster.getClan();
		return nonNull(clan) && clan.getLevel() >= level;
	}
}
