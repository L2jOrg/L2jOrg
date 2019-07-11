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

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;

/**
 * Target Me effect implementation.
 * @author -Nemesiss-
 */
public final class TargetMe extends AbstractEffect
{
	public TargetMe(StatsSet params)
	{
	}
	
	@Override
	public void onExit(L2Character effector, L2Character effected, Skill skill)
	{
		if (effected.isPlayable())
		{
			((Playable) effected).setLockedTarget(null);
		}
	}
	
	@Override
	public void onStart(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		if (effected.isPlayable())
		{
			if (effected.getTarget() != effector)
			{
				effected.setTarget(effector);
			}
			
			((Playable) effected).setLockedTarget(effector);
		}
	}
}
