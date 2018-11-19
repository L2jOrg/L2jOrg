package l2s.gameserver.templates.fakeplayer.actions;

import java.util.List;

import l2s.gameserver.ai.FakeAI;

public abstract class AbstractAction
{
	private final double _chance;

	public AbstractAction(double chance)
	{
		_chance = chance;
	}

	public double getChance()
	{
		return _chance;
	}

	public boolean performAction(FakeAI ai)
	{
		throw new UnsupportedOperationException();
	}

	public List<AbstractAction> makeActionsList()
	{
		return null;
	}

	public boolean isAbortable()
	{
		return false;
	}

	public boolean checkCondition(FakeAI ai, boolean force)
	{
		return true;
	}
}