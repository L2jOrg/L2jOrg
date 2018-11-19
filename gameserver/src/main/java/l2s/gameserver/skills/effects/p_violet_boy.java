package l2s.gameserver.skills.effects;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.s2c.StatusUpdatePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

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