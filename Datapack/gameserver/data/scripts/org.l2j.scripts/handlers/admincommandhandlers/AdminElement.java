/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.util.BuilderUtil;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.enums.InventorySlot.*;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * This class handles following admin commands: - delete = deletes target
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/04/11 10:05:56 $
 */
public class AdminElement implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_setlh",
		"admin_setlc",
		"admin_setll",
		"admin_setlg",
		"admin_setlb",
		"admin_setlw",
		"admin_setls"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar) {
		InventorySlot armorType = null;
		
		if (command.startsWith("admin_setlh"))
		{
			armorType = HEAD;
		}
		else if (command.startsWith("admin_setlc"))
		{
			armorType = CHEST;
		}
		else if (command.startsWith("admin_setlg"))
		{
			armorType = GLOVES;
		}
		else if (command.startsWith("admin_setlb"))
		{
			armorType = FEET;
		}
		else if (command.startsWith("admin_setll"))
		{
			armorType = LEGS;
		}
		else if (command.startsWith("admin_setlw"))
		{
			armorType = RIGHT_HAND;
		}
		else if (command.startsWith("admin_setls"))
		{
			armorType = LEFT_HAND;
		}
		
		if (nonNull(armorType )) {
			try
			{
				final String[] args = command.split(" ");
				
				final AttributeType type = AttributeType.findByName(args[1]);
				final int value = Integer.parseInt(args[2]);
				if ((type == null) || (value < 0) || (value > 450))
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: //setlh/setlc/setlg/setlb/setll/setlw/setls <element> <value>[0-450]");
					return false;
				}
				
				setElement(activeChar, type, value, armorType);
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //setlh/setlc/setlg/setlb/setll/setlw/setls <element>[0-5] <value>[0-450]");
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void setElement(Player activeChar, AttributeType type, int value, InventorySlot armorType)
	{
		// get the target
		WorldObject target = activeChar.getTarget();
		if (target == null)
		{
			target = activeChar;
		}
		Player player = null;
		if (isPlayer(target))
		{
			player = (Player) target;
		}
		else
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		Item itemInstance = null;
		
		// only attempt to enchant if there is a weapon equipped
		final Item parmorInstance = player.getInventory().getPaperdollItem(armorType);
		if ((parmorInstance != null) && (parmorInstance.getLocationSlot() == armorType.getId()))
		{
			itemInstance = parmorInstance;
		}
		
		if (itemInstance != null)
		{
			String old;
			String current;
			final AttributeHolder element = itemInstance.getAttribute(type);
			if (element == null)
			{
				old = "None";
			}
			else
			{
				old = element.toString();
			}
			
			// set enchant value
			player.getInventory().unEquipItemInSlot(armorType);
			if (type == AttributeType.NONE)
			{
				itemInstance.clearAllAttributes();
			}
			else if (value < 1)
			{
				itemInstance.clearAttribute(type);
			}
			else
			{
				itemInstance.setAttribute(new AttributeHolder(type, value), true);
			}
			player.getInventory().equipItem(itemInstance);
			
			if (itemInstance.getAttributes() == null)
			{
				current = "None";
			}
			else
			{
				current = itemInstance.getAttribute(type).toString();
			}
			
			// send packets
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(itemInstance);
			player.sendInventoryUpdate(iu);
			
			// informations
			BuilderUtil.sendSysMessage(activeChar, "Changed elemental power of " + player.getName() + "'s " + itemInstance.getTemplate().getName() + " from " + old + " to " + current + ".");
			if (player != activeChar)
			{
				player.sendMessage(activeChar.getName() + " has changed the elemental power of your " + itemInstance.getTemplate().getName() + " from " + old + " to " + current + ".");
			}
		}
	}
}
