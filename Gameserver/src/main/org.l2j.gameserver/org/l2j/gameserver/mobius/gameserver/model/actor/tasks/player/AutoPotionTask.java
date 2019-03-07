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
package org.l2j.gameserver.mobius.gameserver.model.actor.tasks.player;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.handler.ItemHandler;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;

/**
 * @author Mobius
 */
public class AutoPotionTask implements Runnable
{
	private final L2PcInstance _player;
	
	public AutoPotionTask(L2PcInstance player)
	{
		_player = player;
	}
	
	@Override
	public void run()
	{
		if ((_player == null) || (_player.isOnlineInt() != 1) || _player.isAlikeDead() || (!Config.AUTO_POTIONS_IN_OLYMPIAD && _player.isInOlympiadMode()))
		{
			return;
		}
		
		boolean success = false;
		if (Config.AUTO_HP_ENABLED)
		{
			final boolean restoreHP = ((_player.getStatus().getCurrentHp() / _player.getMaxHp()) * 100) < Config.AUTO_HP_PERCENTAGE;
			for (int itemId : Config.AUTO_HP_ITEM_IDS)
			{
				final L2ItemInstance hpPotion = _player.getInventory().getItemByItemId(itemId);
				if ((hpPotion != null) && (hpPotion.getCount() > 0))
				{
					success = true;
					if (restoreHP)
					{
						ItemHandler.getInstance().getHandler(hpPotion.getEtcItem()).useItem(_player, hpPotion, false);
						_player.sendMessage("Auto potion: Restored HP.");
						break;
					}
				}
			}
		}
		if (Config.AUTO_CP_ENABLED)
		{
			final boolean restoreCP = ((_player.getStatus().getCurrentCp() / _player.getMaxCp()) * 100) < Config.AUTO_CP_PERCENTAGE;
			for (int itemId : Config.AUTO_CP_ITEM_IDS)
			{
				final L2ItemInstance cpPotion = _player.getInventory().getItemByItemId(itemId);
				if ((cpPotion != null) && (cpPotion.getCount() > 0))
				{
					success = true;
					if (restoreCP)
					{
						ItemHandler.getInstance().getHandler(cpPotion.getEtcItem()).useItem(_player, cpPotion, false);
						_player.sendMessage("Auto potion: Restored CP.");
						break;
					}
				}
			}
		}
		if (Config.AUTO_MP_ENABLED)
		{
			final boolean restoreMP = ((_player.getStatus().getCurrentMp() / _player.getMaxMp()) * 100) < Config.AUTO_MP_PERCENTAGE;
			for (int itemId : Config.AUTO_MP_ITEM_IDS)
			{
				final L2ItemInstance mpPotion = _player.getInventory().getItemByItemId(itemId);
				if ((mpPotion != null) && (mpPotion.getCount() > 0))
				{
					success = true;
					if (restoreMP)
					{
						ItemHandler.getInstance().getHandler(mpPotion.getEtcItem()).useItem(_player, mpPotion, false);
						_player.sendMessage("Auto potion: Restored MP.");
						break;
					}
				}
			}
		}
		
		if (!success)
		{
			_player.sendMessage("Auto potion: You are out of potions!");
		}
	}
}
