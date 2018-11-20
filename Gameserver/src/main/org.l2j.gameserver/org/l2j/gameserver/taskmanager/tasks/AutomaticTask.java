package org.l2j.gameserver.taskmanager.tasks;

import org.l2j.commons.threading.RunnableImpl;
import org.l2j.gameserver.ThreadPoolManager;

/**
 * @author VISTALL
 * @date 20:00/24.06.2011
 */
public abstract class AutomaticTask extends RunnableImpl
{
	public AutomaticTask()
	{
		init(true);
	}

	public abstract void doTask() throws Exception;

	public abstract long reCalcTime(boolean start);

	public void init(boolean start)
	{
		ThreadPoolManager.getInstance().schedule(this, reCalcTime(start) - System.currentTimeMillis());
	}

	@Override
	public void runImpl() throws Exception
	{
		try
		{
			doTask();
		}
		finally
		{
			init(false);
		}
	}
}