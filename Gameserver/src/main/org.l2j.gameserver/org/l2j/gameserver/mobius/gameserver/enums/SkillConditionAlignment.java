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
package org.l2j.gameserver.mobius.gameserver.enums;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Sdw
 */
public enum SkillConditionAlignment
{
	LAWFUL
	{
		@Override
		public boolean test(L2PcInstance player)
		{
			return player.getReputation() >= 0;
		}
	},
	CHAOTIC
	{
		@Override
		public boolean test(L2PcInstance player)
		{
			return player.getReputation() < 0;
		}
	};
	
	public abstract boolean test(L2PcInstance player);
}
