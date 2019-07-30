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

import org.l2j.gameserver.enums.StorageType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.network.serverpackets.ExStorageMaxCount;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

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
	public void pump(Creature effected, Skill skill)
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
		if (isPlayer(effected))
		{
			effected.sendPacket(new ExStorageMaxCount((Player) effected));
		}
	}
}
