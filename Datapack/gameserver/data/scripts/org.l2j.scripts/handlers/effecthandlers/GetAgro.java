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

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.L2EffectType;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;

import java.util.Set;

/**
 * Get Agro effect implementation.
 * @author Adry_85, Mobius
 */
public final class GetAgro extends AbstractEffect
{
	public GetAgro(StatsSet params)
	{
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.AGGRESSION;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		if ((effected != null) && effected.isAttackable())
		{
			effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, effector);
			
			// Monsters from the same clan should assist.
			final L2NpcTemplate template = ((L2Attackable) effected).getTemplate();
			final Set<Integer> clans = template.getClans();
			if (clans != null)
			{
				for (L2Attackable nearby : L2World.getInstance().getVisibleObjectsInRange(effected, L2Attackable.class, template.getClanHelpRange()))
				{
					if (!nearby.isMovementDisabled() && nearby.getTemplate().isClan(clans))
					{
						nearby.addDamageHate(effector, 1, 200);
						nearby.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, effector);
					}
				}
			}
		}
	}
}
