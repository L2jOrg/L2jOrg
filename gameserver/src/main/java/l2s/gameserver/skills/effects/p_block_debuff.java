package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class p_block_debuff extends Effect
{
	private final int _maxDebuffsDisabled;
	private int _disabledDebuffs = 0;

	public p_block_debuff(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_maxDebuffsDisabled = getTemplate().getParam().getInteger("max_disabled_debuffs", -1);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getDebuffImmunity().start(this);
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getDebuffImmunity().stop(this);
	}

	@Override
	public boolean checkDebuffImmunity()
	{
		if(_maxDebuffsDisabled > 0)
		{
			_disabledDebuffs++;

			if(getEffected().isPlayer() && getEffected().getPlayer().isGM())
				getEffected().sendMessage("DebuffImmunity: disabled_debuffs: " + _disabledDebuffs + " max_disabled_debuffs: " + _maxDebuffsDisabled);
			if(_disabledDebuffs >= _maxDebuffsDisabled)
			{
				getEffected().getAbnormalList().stop(getSkill());
				if(getEffected().isPlayer() && getEffected().getPlayer().isGM())
					getEffected().sendMessage("DebuffImmunity: All disabled. Abnormal canceled.");
			}
		}
		return true;
	}
}