package org.l2j.gameserver.listener.zone.impl;

import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Zone;
import org.l2j.gameserver.model.base.MountType;
import org.l2j.gameserver.model.entity.residence.Residence;
import org.l2j.gameserver.network.l2.components.SystemMsg;

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