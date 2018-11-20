package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public abstract class EffectRestore extends Effect
{
	protected final boolean _ignoreBonuses;
	protected final boolean _percent;
	protected final boolean _staticPower;

	public EffectRestore(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_ignoreBonuses = template.getParam().getBool("ignore_bonuses", false);
		_percent = template.getParam().getBool("percent", false);
		_staticPower = template.getParam().getBool("static_power", env.skill.isHandler() || _percent || !template.isInstant());
	}
}