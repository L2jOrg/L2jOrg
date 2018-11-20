package org.l2j.gameserver.model.actor.instances.player.tasks;

import org.l2j.commons.lang.reference.HardReference;
import org.l2j.commons.threading.RunnableImpl;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.events.impl.SiegeEvent;

public class EnableUserRelationTask extends RunnableImpl
{
	private HardReference<Player> _playerRef;
	private SiegeEvent<?, ?> _siegeEvent;

	public EnableUserRelationTask(Player player, SiegeEvent<?, ?> siegeEvent)
	{
		_siegeEvent = siegeEvent;
		_playerRef = player.getRef();
	}

	@Override
	public void runImpl() throws Exception
	{
		Player player = _playerRef.get();
		if(player == null)
			return;

		_siegeEvent.removeBlockFame(player);

		player.stopEnableUserRelationTask();
		player.broadcastUserInfo(true);
	}
}