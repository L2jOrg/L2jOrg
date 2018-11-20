package org.l2j.gameserver.listener.zone.impl;

import org.l2j.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Zone;
import org.l2j.gameserver.model.base.ResidenceFunctionType;
import org.l2j.gameserver.model.entity.residence.Residence;
import org.l2j.gameserver.model.entity.residence.ResidenceFunction;
import org.l2j.gameserver.stats.Stats;
import org.l2j.gameserver.stats.funcs.FuncMul;

/**
 * @author VISTALL
 * @date 16:04/03.07.2011
 */
public class ResidenceEnterLeaveListenerImpl implements OnZoneEnterLeaveListener
{
	public static final OnZoneEnterLeaveListener STATIC = new ResidenceEnterLeaveListenerImpl();

	@Override
	public void onZoneEnter(Zone zone, Creature actor)
	{
		if(!actor.isPlayer())
			return;

		Player player = (Player) actor;
		Residence residence = (Residence) zone.getParams().get("residence");
		if(residence == null)
			return;

		if(!residence.isOwner(player.getClanId()))
			return;

		ResidenceFunction function = residence.getActiveFunction(ResidenceFunctionType.RESTORE_HP);
		if(function != null)
		{
			double value = function.getTemplate().getHpRegen();
			if (value > 0)
				player.addStatFunc(new FuncMul(Stats.REGENERATE_HP_RATE, 0x30, residence, value));
		}

		function = residence.getActiveFunction(ResidenceFunctionType.RESTORE_MP);
		if(function != null)
		{
			double value = function.getTemplate().getMpRegen();
			if(value > 0)
				player.addStatFunc(new FuncMul(Stats.REGENERATE_MP_RATE, 0x30, residence, value));
		}
	}

	@Override
	public void onZoneLeave(Zone zone, Creature actor)
	{
		if(!actor.isPlayer())
			return;

		Residence residence = (Residence) zone.getParams().get("residence");
		if(residence == null)
			return;

		actor.removeStatsOwner(residence);
	}
}