package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.network.l2.s2c.StatusUpdatePacket;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public final class p_violet_boy extends Effect
{
	public p_violet_boy(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isPlayer())
			return false;

		return getTemplate().checkCondition(this);
	}

	@Override
	public void onStart()
	{
		Player player = getEffected().getPlayer();
		if(player != null)
		{
			player.getFlags().getVioletBoy().start(this);
			player.sendStatusUpdate(true, true, StatusUpdatePacket.PVP_FLAG);
			player.broadcastRelation();
		}
	}

	@Override
	public void onExit()
	{
		Player player = getEffected().getPlayer();
		if(player != null)
		{
			player.getFlags().getVioletBoy().stop(this);
			player.startPvPFlag(null);
			player.setLastPvPAttack(System.currentTimeMillis() - Config.PVP_TIME + 20000);
		}
	}
}