package org.l2j.gameserver.listener.zone.impl;

import org.l2j.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Zone;
import org.l2j.gameserver.model.instances.SummonInstance;

public class SiegeZoneListener implements OnZoneEnterLeaveListener
{
	public static final OnZoneEnterLeaveListener STATIC = new SiegeZoneListener();

	@Override
	public void onZoneEnter(Zone zone, Creature actor)
	{
	}

	@Override
	public void onZoneLeave(Zone zone, Creature cha)
	{
		if(!cha.isInSiegeZone() && cha.isSummon())
		{
			SummonInstance summon = (SummonInstance) cha;
			if(summon.isSiegeSummon())
				summon.unSummon(false);
		}
	}
}