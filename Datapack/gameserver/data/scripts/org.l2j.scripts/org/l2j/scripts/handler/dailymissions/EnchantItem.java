package org.l2j.scripts.handler.dailymissions;

import org.l2j.commons.time.cron.SchedulingPattern;
import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.listener.actor.player.OnEnchantItemListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;

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
