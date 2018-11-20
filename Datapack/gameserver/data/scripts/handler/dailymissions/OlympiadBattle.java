package handler.dailymissions;

import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.listener.actor.player.OnOlympiadFinishBattleListener;
import org.l2j.gameserver.model.Player;

/**
 * @author Bonux
**/
public class OlympiadBattle extends ProgressDailyMissionHandler
{
	private class HandlerListeners implements OnOlympiadFinishBattleListener
	{
		@Override
		public void onOlympiadFinishBattle(Player player, boolean winner)
		{
			if(!winner) // TODO: Проверить на оффе.
				return;

			progressMission(player, 1, true);
		}
	}

	private final HandlerListeners _handlerListeners = new HandlerListeners();

	@Override
	public CharListener getListener()
	{
		return _handlerListeners;
	}
}
