package org.l2j.gameserver.listener.zone.impl;

import org.l2j.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Zone;
import org.l2j.gameserver.utils.Location;

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