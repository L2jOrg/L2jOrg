package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.skills.SkillTrait;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class p_attack_trait extends Effect
{
	private final SkillTrait _type;
	private final double _power;

	public p_attack_trait(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		String traitName = getTemplate().getParam().getString("type").toUpperCase();
		if(traitName.startsWith("TRAIT_"))
			traitName = traitName.substring(6).trim();
		_type = SkillTrait.valueOf(traitName);
		_power = (getTemplate().getParam().getInteger("power") + 100.) / 100.;
	}

	@Override
	public void onStart()
	{
		if(_power == 1)
			return;
	}

	@Override
	public void onExit()
	{
		if(_power == 1)
			return;
	}
}