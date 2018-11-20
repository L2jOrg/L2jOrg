package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.stats.Env;

public abstract class ConditionInventory extends Condition
{
	protected final int _slot;

	public ConditionInventory(int slot)
	{
		_slot = slot;
	}

	@Override
	protected abstract boolean testImpl(Env env);
}