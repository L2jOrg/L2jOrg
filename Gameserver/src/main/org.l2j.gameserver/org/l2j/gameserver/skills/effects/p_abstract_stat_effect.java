package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.stats.StatModifierType;
import org.l2j.gameserver.stats.Stats;
import org.l2j.gameserver.stats.funcs.Func;
import org.l2j.gameserver.stats.funcs.FuncTemplate;
import org.l2j.gameserver.templates.skill.EffectTemplate;

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