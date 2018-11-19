package l2s.gameserver.listener.zone.impl;

import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.base.ResidenceFunctionType;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.entity.residence.ResidenceFunction;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.funcs.FuncMul;

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