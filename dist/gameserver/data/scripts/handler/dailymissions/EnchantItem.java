package handler.dailymissions;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.listener.CharListener;
import l2s.gameserver.listener.actor.player.OnEnchantItemListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;

/**
 * @author Bonux
**/
public class EnchantItem extends ProgressDailyMissionHandler
{
	private static final SchedulingPattern REUSE_PATTERN = new SchedulingPattern("30 6 * * 1");

	private class HandlerListeners implements OnEnchantItemListener
	{
		@Override
		public void onEnchantItem(Player player, ItemInstance item, boolean success)
		{
			progressMission(player, 1, true);
		}
	}

	private final HandlerListeners _handlerListeners = new HandlerListeners();

	@Override
	public CharListener getListener()
	{
		return _handlerListeners;
	}

	@Override
	public SchedulingPattern getReusePattern()
	{
		return REUSE_PATTERN;
	}
}
