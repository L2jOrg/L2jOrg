package l2s.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.string.StringArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

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