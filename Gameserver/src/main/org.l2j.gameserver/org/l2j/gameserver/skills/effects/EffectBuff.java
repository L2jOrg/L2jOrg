package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public final class EffectBuff extends Effect
{
	public EffectBuff(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}
}