package org.l2j.gameserver.taskmanager.tasks;

import org.l2j.gameserver.tables.ClanTable;

public class PledgeHuntingSaveTask extends AutomaticTask
{
	private static final long SAVE_DELAY = 600000L;

	public PledgeHuntingSaveTask()
	{
		super();
	}

	@Override
	public void doTask() throws Exception
	{
		ClanTable.getInstance().saveClanHuntingProgress();
	}

	@Override
	public long reCalcTime(final boolean start)
	{
		return System.currentTimeMillis() + SAVE_DELAY;
	}
}