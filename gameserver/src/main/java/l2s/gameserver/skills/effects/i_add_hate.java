package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

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