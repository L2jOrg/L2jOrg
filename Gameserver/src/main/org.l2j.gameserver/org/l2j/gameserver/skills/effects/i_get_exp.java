package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class i_get_exp extends i_abstract_effect
{
	private final long _power;
	private final int _percentPower;
	private final int _percentPowerMaxLvl;

	public i_get_exp(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_power = template.getParam().getLong("power");
		_percentPower = template.getParam().getInteger("percent_power", 0);
		_percentPowerMaxLvl = template.getParam().getInteger("percent_power_max_lvl", 0);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isPlayer())
			return false;
		return super.checkCondition();
	}

	@Override
	public void instantUse()
	{
		Player player = getEffected().getPlayer();

		long power = _power;
		if(_percentPowerMaxLvl != 0 && player.getLevel() < _percentPowerMaxLvl)
			power = (long) (player.getExp() / 100. * _percentPower);

		player.addExpAndSp(power, 0);
	}
}