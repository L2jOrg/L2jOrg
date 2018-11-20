package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.data.xml.holder.SkillAcquireHolder;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.base.AcquireType;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.stats.Env;

/**
 * @author Bonux
 */
public class ConditionPlayerCanLearnSkill extends Condition
{
	private static final AcquireType[] ACQUITE_TYPES_TO_CHECK = { AcquireType.NORMAL, AcquireType.FISHING, AcquireType.GENERAL, AcquireType.HERO };

	private final int _id;
	private final int _level;

	public ConditionPlayerCanLearnSkill(int id, int level)
	{
		_id = id;
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Skill skill = SkillHolder.getInstance().getSkill(_id, _level);
		if(skill == null)
			return false;

		if(!env.character.isPlayer())
			return false;

		Player player = env.character.getPlayer();

		int skillLvl = skill.getLevel();
		int haveSkillLvl = 0;

		SkillEntry knownSkillEntry = player.getKnownSkill(skill.getId());
		if(knownSkillEntry != null)
		{
			haveSkillLvl = knownSkillEntry.getTemplate().getLevel();
			if(haveSkillLvl >= skillLvl)
				return false;
		}

		if(skillLvl > (haveSkillLvl + 1))
			return false;

		for(AcquireType at : ACQUITE_TYPES_TO_CHECK)
		{
			if(!SkillAcquireHolder.getInstance().isSkillPossible(player, skill, at))
				continue;

			SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, skill.getId(), skill.getLevel(), at);
			if(skillLearn == null)
				continue;

			if(SkillAcquireHolder.getInstance().checkLearnCondition(player, skillLearn, player.getLevel()))
				return true;
		}

		return false;
	}
}