package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.stats.Env;

/**
 * @author VISTALL
 * @date 18:37/22.05.2011
 */
public class ConditionPlayerSummonSiegeGolem extends Condition
{
	public ConditionPlayerSummonSiegeGolem()
	{
		//
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Player player = env.character.getPlayer();
		if(player == null)
			return false;
		Zone zone = player.getZone(Zone.ZoneType.RESIDENCE);
		if(zone != null)
			return false;
		zone = player.getZone(Zone.ZoneType.SIEGE);
		if(zone == null)
			return false;
		SiegeEvent<?, ?> event = player.getEvent(SiegeEvent.class);
		if(event == null)
			return false;
		if(event instanceof CastleSiegeEvent)
		{
			if(zone.getParams().getInteger("residence") != event.getId())
				return false;
			if(event.getSiegeClan(CastleSiegeEvent.ATTACKERS, player.getClan()) == null)
				return false;
		}
		else if(event.getSiegeClan(CastleSiegeEvent.DEFENDERS, player.getClan()) == null)
			return false;

		return true;
	}
}
