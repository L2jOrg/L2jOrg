package org.l2j.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;

import org.l2j.commons.string.StringArrayUtils;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.network.l2.s2c.MagicSkillUse;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class i_call_random_skill extends i_abstract_effect
{
	private final List<SkillEntry> _skills = new ArrayList<SkillEntry>();

	public i_call_random_skill(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		int[][] skills = StringArrayUtils.stringToIntArray2X(template.getParam().getString("skills"), ";", "-");
		for(int[] skill : skills)
		{
			SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(skill[0], skill.length >= 2 ? skill[1] : 1);
			if(skillEntry != null)
				_skills.add(skillEntry);
		}
	}

	@Override
	public void instantUse()
	{
		if(_skills.isEmpty())
			return;

		Skill skill = Rnd.get(_skills).getTemplate();
		if(skill == null)
			return;

		if(skill.getReuseDelay() > 0 && getEffector().isSkillDisabled(skill))
			return;

		if(skill.checkCondition(getEffector(), getEffected(), true, true, true, false, true))
		{
			List<Creature> targets = skill.getTargets(getEffector(), getEffected(), false);

			if((!skill.isNotBroadcastable()) && (!getEffector().isCastingNow()))
			{
				for(Creature cha : targets)
					getEffector().broadcastPacket(new MagicSkillUse(getEffector(), cha, skill.getDisplayId(), skill.getDisplayLevel(), 0, 0));
			}
			getEffector().callSkill(skill, targets, false, true);
			getEffector().disableSkill(skill, skill.getReuseDelay());
		}
	}
}