/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.l2jmobius.gameserver.ai.CtrlEvent;
import com.l2jmobius.gameserver.ai.CtrlIntention;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Summon;
import com.l2jmobius.gameserver.model.effects.AbstractEffect;
import com.l2jmobius.gameserver.model.effects.EffectFlag;
import com.l2jmobius.gameserver.model.effects.L2EffectType;
import com.l2jmobius.gameserver.model.skills.Skill;

/**
 * Block Actions effect implementation.
 * @author mkizub
 */
public final class BlockActions extends AbstractEffect
{
	private final Set<Integer> _allowedSkills;
	
	public BlockActions(StatsSet params)
	{
		final String[] allowedSkills = params.getString("allowedSkills", "").split(";");
		_allowedSkills = Arrays.stream(allowedSkills).filter(s -> !s.isEmpty()).map(Integer::parseInt).collect(Collectors.toSet());
	}
	
	@Override
	public long getEffectFlags()
	{
		return _allowedSkills.isEmpty() ? EffectFlag.BLOCK_ACTIONS.getMask() : EffectFlag.CONDITIONAL_BLOCK_ACTIONS.getMask();
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.BLOCK_ACTIONS;
	}
	
	@Override
	public void onStart(L2Character effector, L2Character effected, Skill skill)
	{
		_allowedSkills.stream().forEach(effected::addBlockActionsAllowedSkill);
		effected.startParalyze();
	}
	
	@Override
	public void onExit(L2Character effector, L2Character effected, Skill skill)
	{
		_allowedSkills.stream().forEach(effected::removeBlockActionsAllowedSkill);
		if (effected.isPlayable())
		{
			if (effected.isSummon())
			{
				if ((effector != null) && !effector.isDead())
				{
					((L2Summon) effected).doAttack(effector);
				}
				else
				{
					effected.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, effected.getActingPlayer());
				}
			}
			else
			{
				effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			}
		}
		else
		{
			effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
}
