package l2s.gameserver.ai;

import l2s.gameserver.model.instances.NpcInstance;

public class Mystic extends DefaultAI
{
	public Mystic(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		return super.thinkActive() || defaultThinkBuff(2);
	}

	@Override
	protected boolean createNewTask()
	{
		return defaultFightTask();
	}

	@Override
	public int getRatePHYS()
	{
		return _damSkills.length == 0 ? 10 : 0;
	}

	@Override
	public int getRateDOT()
	{
		return 10;
	}

	@Override
	public int getRateDEBUFF()
	{
		return 5;
	}

	@Override
	public int getRateDAM()
	{
		return 30;
	}

	@Override
	public int getRateSTUN()
	{
		return 3;
	}

	@Override
	public int getRateBUFF()
	{
		return 3;
	}

	@Override
	public int getRateHEAL()
	{
		return 5;
	}
}