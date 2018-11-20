package org.l2j.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.entity.events.impl.SiegeEvent;
import org.l2j.gameserver.model.instances.SummonInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectDiscord extends Effect
{
	public EffectDiscord(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		boolean multitargets = getSkill().isAoE();

		if(!getEffected().isMonster())
		{
			if(!multitargets)
				getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		if(getEffected().isFearImmune() || getEffected().isRaid())
		{
			if(!multitargets)
				getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		// Discord нельзя наложить на осадных саммонов
		Player player = getEffected().getPlayer();
		if(player != null)
		{
			SiegeEvent<?, ?> siegeEvent = player.getEvent(SiegeEvent.class);
			if(getEffected().isSummon() && siegeEvent != null && siegeEvent.containsSiegeSummon((SummonInstance) getEffected()))
			{
				if(!multitargets)
					getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
		}

		if(getEffected().isInPeaceZone())
		{
			if(!multitargets)
				getEffector().sendPacket(SystemMsg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
			return false;
		}

		int skilldiff = getEffected().getLevel() - getSkill().getMagicLevel();
		int lvldiff = getEffected().getLevel() - getEffector().getLevel();
		if(skilldiff > 10 || skilldiff > 5 && Rnd.chance(30) || Rnd.chance(Math.abs(lvldiff) * 2))
		{
			if(!multitargets)
				getEffector().sendPacket(new SystemMessagePacket(SystemMsg.S1_HAS_FAILED).addSkillName(getSkill()));
			return false;
		}

		return super.checkCondition();
	}

	@Override
	public boolean isHidden()
	{
		return true;
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getConfused().start(this);
		onActionTime();
	}

	@Override
	public void onExit()
	{
		if(getEffected().getFlags().getConfused().stop(this))
		{
			getEffected().abortAttack(true, true);
			getEffected().abortCast(true, true);
			getEffected().stopMove();
			getEffected().getAI().setAttackTarget(null);
			getEffected().setWalking();
			getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		}
	}

	@Override
	public boolean onActionTime()
	{
		List<Creature> targetList = new ArrayList<Creature>();

		for(Creature character : getEffected().getAroundCharacters(900, 200))
			if(character.isNpc() && character != getEffected())
				targetList.add(character);

		// if there is no target, exit function
		if(targetList.isEmpty())
			return true;

		// Choosing randomly a new target
		Creature target = targetList.get(Rnd.get(targetList.size()));

		// Attacking the target
		getEffected().setRunning();
		getEffected().getAI().Attack(target, true, false);

		return false;
	}
}