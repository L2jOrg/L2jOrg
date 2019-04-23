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
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;

/**
 * Crystal Grade Modify effect implementation.
 * @author Zoey76
 */
public final class CrystalGradeModify extends AbstractEffect
{
	private final int _grade;
	
	public CrystalGradeModify(StatsSet params)
	{
		_grade = params.getInt("grade", 0);
	}
	
	@Override
	public boolean canStart(L2Character effector, L2Character effected, Skill skill)
	{
		return effected.isPlayer();
	}
	
	@Override
	public void onStart(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		effected.getActingPlayer().setExpertisePenaltyBonus(_grade);
	}
	
	@Override
	public void onExit(L2Character effector, L2Character effected, Skill skill)
	{
		final L2PcInstance player = effected.getActingPlayer();
		if (player != null)
		{
			player.setExpertisePenaltyBonus(0);
		}
	}
}