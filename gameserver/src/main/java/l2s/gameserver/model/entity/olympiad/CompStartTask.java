package l2s.gameserver.model.entity.olympiad;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.Announcements;
import l2s.gameserver.network.l2.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CompStartTask extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(CompStartTask.class);

	@Override
	public void runImpl() throws Exception
	{
		if(Olympiad.isOlympiadEnd())
			return;

		Olympiad._manager = new OlympiadManager();
		Olympiad._inCompPeriod = true;

		new Thread(Olympiad._manager).start();

		Olympiad.startCompEndTask(Olympiad.getMillisToCompEnd());

		Announcements.announceToAll(SystemMsg.SHARPEN_YOUR_SWORDS_TIGHTEN_THE_STITCHING_IN_YOUR_ARMOR_AND_MAKE_HASTE_TO_A_GRAND_OLYMPIAD_MANAGER__BATTLES_IN_THE_GRAND_OLYMPIAD_GAMES_ARE_NOW_TAKING_PLACE);
		_log.info("Olympiad System: Olympiad Game Started");
	}
}