package l2s.gameserver.listener.zone.impl;

import l2s.gameserver.Config;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.utils.Location;

public class EpicZoneListener implements OnZoneEnterLeaveListener
{
	public static final OnZoneEnterLeaveListener STATIC = new EpicZoneListener();

	@Override
	public void onZoneEnter(Zone zone, Creature cha)
	{
		if(cha.isPlayable() && !cha.getPlayer().isGM())
		{
			if(cha.getLevel() > zone.getParams().getInteger("levelLimit", Integer.MAX_VALUE))
			{
				if(cha.isPlayer())
					cha.getPlayer().sendMessage(new CustomMessage("scripts.zones.epic.banishMsg"));
				cha.teleToLocation(Location.parseLoc(zone.getParams().getString("tele")));
			}
			else if(!Config.ALT_USE_TRANSFORM_IN_EPIC_ZONE)
			{
				if(cha.isPlayer())
				{
					Player player = cha.getPlayer();
					if(player.isTransformed())
						player.setTransform(null);
				}
			}
		}
	}

	@Override
	public void onZoneLeave(Zone zone, Creature cha)
	{}
}