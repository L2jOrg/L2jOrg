package org.l2j.gameserver.skills.effects;

import java.util.List;

import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.network.l2.s2c.MagicSkillUse;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class i_call_skill extends i_abstract_effect
{
	private final SkillEntry _skillEntry;
	private final int _maxIncreaseLevel;

	public i_call_skill(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		int[] skill = template.getParam().getIntegerArray("skill", "-");
		_skillEntry = SkillHolder.getInstance().getSkillEntry(skill[0], skill.length >= 2 ? skill[1] : 1);
		_maxIncreaseLevel = template.getParam().getInteger("max_increase_level", 1);
	}

	@Override
	public void instantUse()
	{
		if(_skillEntry == null)
			return;

		Skill skill = _skillEntry.getTemplate();
		if(skill == null)
			return;

		Creature aimTarget = skill.getAimingTarget(getEffector(), getEffected());
		if(aimTarget != null && _maxIncreaseLevel > 0)
		{
			Skill hasSkill = null;
			for(Abnormal effect : aimTarget.getAbnormalList())
			{
				if(effect.getSkill().getId() == skill.getId())
				{
					hasSkill = effect.getSkill();
					break;
				}
			}

			loop: if(hasSkill == null)
			{
				for(Servitor servitor : aimTarget.getServitors())
				{
					for(Abnormal effect : servitor.getAbnormalList())
					{
						if(effect.getSkill().getId() == skill.getId())
						{
							hasSkill = effect.getSkill();
							break loop;
						}
					}
				}
			}

			if(hasSkill != null)
			{
				Skill newSkill = SkillHolder.getInstance().getSkill(skill.getId(), Math.min(_maxIncreaseLevel, hasSkill.getLevel() + 1));
				if(newSkill != null)
					skill = newSkill;
				else
					skill = hasSkill;
			}
		}

		if(skill.getReuseDelay() > 0 && getEffector().isSkillDisabled(skill))
			return;

		if(skill.checkCondition(getEffector(), aimTarget, true, true, true, false, true))
		{
			List<Creature> targets = skill.getTargets(getEffector(), aimTarget, false);

			if(!skill.isNotBroadcastable() && !getEffector().isCastingNow())
			{
				for(Creature cha : targets)
					getEffector().broadcastPacket(new MagicSkillUse(getEffector(), cha, skill.getDisplayId(), skill.getDisplayLevel(), 0, 0));
			}
			getEffector().callSkill(skill, targets, false, true);
			getEffector().disableSkill(skill, skill.getReuseDelay());
		}
	}
}