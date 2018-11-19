package l2s.gameserver.skills.effects;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.instances.SummonInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.PositionUtils;

public final class EffectFear extends Effect
{
	public static final double FEAR_RANGE = 900;

	public EffectFear(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isFearImmune())
			return false;

		// Fear нельзя наложить на осадных саммонов
		Player player = getEffected().getPlayer();
		if(player != null)
		{
			SiegeEvent<?, ?> siegeEvent = player.getEvent(SiegeEvent.class);
			if(getEffected().isSummon() && siegeEvent != null && siegeEvent.containsSiegeSummon((SummonInstance) getEffected()))
				return false;
		}

		if(getEffected().isInPeaceZone())
			return false;

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		if(getEffected().getFlags().getAfraid().start(this))
		{
			getEffected().abortAttack(true, true);
			getEffected().abortCast(true, true);
			getEffected().stopMove();
		}

		onActionTime();
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getAfraid().stop(this);
		getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}

	@Override
	public boolean onActionTime()
	{
		final double angle = Math.toRadians(PositionUtils.calculateAngleFrom(getEffector(), getEffected()));
		final int oldX = getEffected().getX();
		final int oldY = getEffected().getY();
		final int x = oldX + (int) (FEAR_RANGE * Math.cos(angle));
		final int y = oldY + (int) (FEAR_RANGE * Math.sin(angle));
		getEffected().setRunning();
		getEffected().moveToLocation(GeoEngine.moveCheck(oldX, oldY, getEffected().getZ(), x, y, getEffected().getGeoIndex()), 0, false);
		return true;
	}

	@Override
	public int getInterval()
	{
		return 3;
	}
}