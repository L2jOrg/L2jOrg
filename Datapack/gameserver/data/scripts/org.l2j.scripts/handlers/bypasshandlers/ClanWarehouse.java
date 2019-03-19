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
package handlers.bypasshandlers;

import java.util.logging.Level;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2WarehouseInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.WareHouseDepositList;
import org.l2j.gameserver.network.serverpackets.WareHouseWithdrawalList;

public class ClanWarehouse implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"withdrawc",
		"depositc"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!Config.ALLOW_WAREHOUSE)
		{
			return false;
		}
		
		if (!target.isNpc())
		{
			return false;
		}
		
		final L2Npc npc = (L2Npc) target;
		if (!(npc instanceof L2WarehouseInstance) && (npc.getClan() != null))
		{
			return false;
		}
		
		if (activeChar.hasItemRequest())
		{
			return false;
		}
		else if (activeChar.getClan() == null)
		{
			activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE);
			return false;
		}
		else if (activeChar.getClan().getLevel() == 0)
		{
			activeChar.sendPacket(SystemMessageId.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_ABOVE_CAN_USE_A_CLAN_WAREHOUSE);
			return false;
		}
		else
		{
			try
			{
				if (command.toLowerCase().startsWith(COMMANDS[0])) // WithdrawC
				{
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					
					if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_VIEW_WAREHOUSE))
					{
						activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE);
						return true;
					}
					
					activeChar.setActiveWarehouse(activeChar.getClan().getWarehouse());
					
					if (activeChar.getActiveWarehouse().getSize() == 0)
					{
						activeChar.sendPacket(SystemMessageId.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
						return true;
					}
					
					for (L2ItemInstance i : activeChar.getActiveWarehouse().getItems())
					{
						if (i.isTimeLimitedItem() && (i.getRemainingTime() <= 0))
						{
							activeChar.getActiveWarehouse().destroyItem("L2ItemInstance", i, activeChar, null);
						}
					}
					
					activeChar.sendPacket(new WareHouseWithdrawalList(1, activeChar, WareHouseWithdrawalList.CLAN));
					activeChar.sendPacket(new WareHouseWithdrawalList(2, activeChar, WareHouseWithdrawalList.CLAN));
					return true;
				}
				else if (command.toLowerCase().startsWith(COMMANDS[1])) // DepositC
				{
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					activeChar.setActiveWarehouse(activeChar.getClan().getWarehouse());
					activeChar.setInventoryBlockingStatus(true);
					activeChar.sendPacket(new WareHouseDepositList(1, activeChar, WareHouseDepositList.CLAN));
					activeChar.sendPacket(new WareHouseDepositList(2, activeChar, WareHouseDepositList.CLAN));
					return true;
				}
				
				return false;
			}
			catch (Exception e)
			{
				LOGGER.warn("Exception in " + getClass().getSimpleName(), e);
			}
		}
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
