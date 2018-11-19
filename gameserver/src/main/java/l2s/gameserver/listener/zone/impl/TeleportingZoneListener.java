package l2s.gameserver.listener.zone.impl;

import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.utils.Location;

public class TeleportingZoneListener implements OnZoneEnterLeaveListener
{
	public static final OnZoneEnterLeaveListener STATIC = new TeleportingZoneListener();

	@Override
	public void onZoneEnter(Zone zone, Creature actor)
	{
		if(!actor.isPlayer())
			return;

		Player player = actor.getPlayer();
		if(player == null)
			return;

		Location loc = zone.getTemplate().getTeleportLocation();
		if(loc != null)
			player.teleToLocation(loc);
	}

	@Override
	public void onZoneLeave(Zone zone, Creature actor)
	{}
}