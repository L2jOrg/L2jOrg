package org.l2j.gameserver.taskmanager.tasks;

import org.l2j.gameserver.model.entity.olympiad.OlympiadDatabase;

/**
 * @author VISTALL
 * @date 20:11/24.06.2011
 */
public class OlympiadSaveTask extends AutomaticTask
{
	public OlympiadSaveTask()
	{
		super();
	}

	@Override
	public void doTask() throws Exception
	{
		//long t = System.currentTimeMillis();

		//logger.info("OlympiadSaveTask: data save started.");
		OlympiadDatabase.save();
		//logger.info("OlympiadSaveTask: data save ended in time: " + (System.currentTimeMillis() - t) + " ms.");
	}

	@Override
	public long reCalcTime(boolean start)
	{
		return System.currentTimeMillis() + 600000L;
	}
}