package l2s.gameserver.listener.zone.impl;

import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.instances.SummonInstance;

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