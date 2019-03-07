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

import com.l2jmobius.gameserver.enums.StorageType;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.effects.AbstractEffect;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.stats.Stats;
import com.l2jmobius.gameserver.network.serverpackets.ExStorageMaxCount;

/**
 * @author Sdw
 */
public class EnlargeSlot extends AbstractEffect
{
	private final StorageType _type;
	private final double _amount;
	
	public EnlargeSlot(StatsSet params)
	{
		_amount = params.getDouble("amount", 0);
		_type = params.getEnum("type", StorageType.class, StorageType.INVENTORY_NORMAL);
	}
	
	@Override
	public void pump(L2Character effected, Skill skill)
	{
		Stats stat = Stats.INVENTORY_NORMAL;
		
		switch (_type)
		{
			case TRADE_BUY:
			{
				stat = Stats.TRADE_BUY;
				break;
			}
			case TRADE_SELL:
			{
				stat = Stats.TRADE_SELL;
				break;
			}
			case RECIPE_DWARVEN:
			{
				stat = Stats.RECIPE_DWARVEN;
				break;
			}
			case RECIPE_COMMON:
			{
				stat = Stats.RECIPE_COMMON;
				break;
			}
			case STORAGE_PRIVATE:
			{
				stat = Stats.STORAGE_PRIVATE;
				break;
			}
		}
		effected.getStat().mergeAdd(stat, _amount);
		if (effected.isPlayer())
		{
			effected.sendPacket(new ExStorageMaxCount((L2PcInstance) effected));
		}
	}
}
