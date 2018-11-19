package l2s.gameserver.model.entity.olympiad;

import l2s.commons.threading.RunnableImpl;

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