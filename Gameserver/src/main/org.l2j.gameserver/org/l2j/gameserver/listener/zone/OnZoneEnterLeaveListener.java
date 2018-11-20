package org.l2j.gameserver.listener.zone;

import org.l2j.commons.listener.Listener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Zone;

public interface OnZoneEnterLeaveListener extends Listener<Zone>
{
	public void onZoneEnter(Zone zone, Creature actor);

	public void onZoneLeave(Zone zone, Creature actor);
}