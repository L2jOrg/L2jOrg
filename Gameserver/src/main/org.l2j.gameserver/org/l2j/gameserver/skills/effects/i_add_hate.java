package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.instances.MonsterInstance;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class i_add_hate extends i_abstract_effect
{
	private final boolean _affectSummoner;

	public i_add_hate(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_affectSummoner = template.getParam().getBool("affect_summoner", false);
	}

	@Override
	public boolean checkCondition()
	{
		return getEffected().isMonster();
	}

	@Override
	public void instantUse()
	{
		Creature target = getEffector();
		if(_affectSummoner)
		{
			Player owner = target.getPlayer();
			if(owner != null)
				target = owner;
		}
		if(getValue() > 0)
			((MonsterInstance) getEffected()).getAggroList().addDamageHate(target, 0, (int) getValue());
		else if(getValue() < 0)
			((MonsterInstance) getEffected()).getAggroList().reduceHate(target, (int) -getValue());
	}
}