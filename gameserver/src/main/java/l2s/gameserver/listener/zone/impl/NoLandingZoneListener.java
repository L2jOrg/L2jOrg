package l2s.gameserver.listener.zone.impl;

import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.base.MountType;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.network.l2.components.SystemMsg;

public class NoLandingZoneListener implements OnZoneEnterLeaveListener
{
	public static final OnZoneEnterLeaveListener STATIC = new NoLandingZoneListener();

	@Override
	public void onZoneEnter(Zone zone, Creature actor)
	{
		Player player = actor.getPlayer();
		if(player != null)
			if(player.isFlying() && player.getMountType() == MountType.WYVERN)
			{
				Residence residence = ResidenceHolder.getInstance().getResidence(zone.getParams().getInteger("residence", 0));
				if(residence != null && player.getClan() != null && residence.getOwner() == player.getClan())
				{
					//
				}
				else
				{
					player.stopMove();
					player.sendPacket(SystemMsg.THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN);
					player.setMount(null);
				}
			}
	}

	@Override
	public void onZoneLeave(Zone zone, Creature cha)
	{}
}