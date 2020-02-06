package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class SkillCritical extends AbstractEffect {
	public final BaseStats stat;
	
	public SkillCritical(StatsSet params)
	{
		stat = params.getEnum("stat", BaseStats.class, BaseStats.STR);
	}
	
	@Override
	public void pump(Creature effected, Skill skill)
	{
		effected.getStats().mergeAdd(Stat.SKILL_CRITICAL, stat.ordinal());
	}
}
