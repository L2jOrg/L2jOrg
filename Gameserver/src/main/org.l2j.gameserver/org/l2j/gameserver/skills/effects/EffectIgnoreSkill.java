package org.l2j.gameserver.skills.effects;

import io.github.joealisson.primitive.sets.IntSet;
import io.github.joealisson.primitive.sets.impl.HashIntSet;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class EffectIgnoreSkill extends Effect
{
	private final IntSet _ignoredSkill = new HashIntSet();

	public EffectIgnoreSkill(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		String[] skills = template.getParam().getString("skillId", "").split(";");
		for(String skill : skills)
			_ignoredSkill.add(Integer.parseInt(skill));
	}

	@Override
	public void onStart()
	{
		getEffected().addIgnoreSkillsEffect(this, _ignoredSkill);
	}

	@Override
	public void onExit()
	{
		getEffected().removeIgnoreSkillsEffect(this);
	}
}