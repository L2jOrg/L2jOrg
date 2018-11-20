package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.geodata.GeoEngine;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.entity.events.impl.SiegeEvent;
import org.l2j.gameserver.model.instances.SummonInstance;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class EffectMoveToEffector extends Effect
{
	public EffectMoveToEffector(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isFearImmune()) // TODO: Пересмотреть.
			return false;

		// Нельзя наложить на осадных саммонов
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
		getEffected().moveToLocation(GeoEngine.moveCheck(getEffected().getX(), getEffected().getY(), getEffected().getZ(), getEffector().getX(), getEffector().getY(), getEffected().getGeoIndex()), 40, true);
		return true;
	}
}