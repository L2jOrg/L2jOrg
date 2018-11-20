package org.l2j.gameserver.model.entity.olympiad;

import org.l2j.commons.threading.RunnableImpl;

public class ValidationTask extends RunnableImpl
{
	@Override
	public void runImpl() throws Exception
	{
		Olympiad._period = 0;
		Olympiad._currentCycle++;

		OlympiadDatabase.setNewOlympiadStartTime();

		Olympiad.init();
		OlympiadDatabase.save();
	}
}