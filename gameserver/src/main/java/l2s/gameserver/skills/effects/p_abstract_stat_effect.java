package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.StatModifierType;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.stats.funcs.FuncTemplate;
import l2s.gameserver.templates.skill.EffectTemplate;

public abstract class p_abstract_stat_effect extends Effect
{
	private final StatModifierType _modifierType;
	private final Func _func;

	public p_abstract_stat_effect(Abnormal abnormal, Env env, EffectTemplate template, Stats stat)
	{
		super(abnormal, env, template);

		_modifierType = template.getParam().getEnum("type", StatModifierType.class, StatModifierType.DIFF);

		if(_modifierType == StatModifierType.DIFF)
			_func = new FuncTemplate(getTemplate().getCondition(), "Add", stat, 0x40, getValue()).getFunc(this);
		else
			_func = new FuncTemplate(getTemplate().getCondition(), "Mul", stat, 0x30, getValue() / 100 + 1).getFunc(this);
	}

	protected final StatModifierType getModifierType()
	{
		return _modifierType;
	}

	@Override
	public final void onStart()
	{
		getEffected().addStatFunc(_func);
		afterApplyActions();
	}

	@Override
	public final void onExit()
	{
		getEffected().removeStatFunc(_func);
	}

	protected void afterApplyActions()
	{}

	@Override
	public final boolean checkCondition()
	{
		return true;
	}
}