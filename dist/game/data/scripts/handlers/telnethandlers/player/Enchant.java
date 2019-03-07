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
package handlers.telnethandlers.player;

import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.itemcontainer.Inventory;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmobius.gameserver.network.telnet.ITelnetCommand;
import com.l2jmobius.gameserver.util.GMAudit;
import com.l2jmobius.gameserver.util.Util;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class Enchant implements ITelnetCommand
{
	@Override
	public String getCommand()
	{
		return "enchant";
	}
	
	@Override
	public String getUsage()
	{
		return "Enchant <player name> <item id> [item amount] [item enchant]";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		if ((args.length < 3) || args[0].isEmpty() || !Util.isDigit(args[1]) || !Util.isDigit(args[2]))
		{
			return null;
		}
		final L2PcInstance player = L2World.getInstance().getPlayer(args[0]);
		if (player != null)
		{
			int itemType = Integer.parseInt(args[1]);
			int enchant = Integer.parseInt(args[2]);
			enchant = Math.min(enchant, 127);
			enchant = Math.max(enchant, 0);
			
			switch (itemType)
			{
				case 1:
				{
					itemType = Inventory.PAPERDOLL_HEAD;
					break;
				}
				case 2:
				{
					itemType = Inventory.PAPERDOLL_CHEST;
					break;
				}
				case 3:
				{
					itemType = Inventory.PAPERDOLL_GLOVES;
					break;
				}
				case 4:
				{
					itemType = Inventory.PAPERDOLL_FEET;
					break;
				}
				case 5:
				{
					itemType = Inventory.PAPERDOLL_LEGS;
					break;
				}
				case 6:
				{
					itemType = Inventory.PAPERDOLL_RHAND;
					break;
				}
				case 7:
				{
					itemType = Inventory.PAPERDOLL_LHAND;
					break;
				}
				case 8:
				{
					itemType = Inventory.PAPERDOLL_LEAR;
					break;
				}
				case 9:
				{
					itemType = Inventory.PAPERDOLL_REAR;
					break;
				}
				case 10:
				{
					itemType = Inventory.PAPERDOLL_LFINGER;
					break;
				}
				case 11:
				{
					itemType = Inventory.PAPERDOLL_RFINGER;
					break;
				}
				case 12:
				{
					itemType = Inventory.PAPERDOLL_NECK;
					break;
				}
				case 13:
				{
					itemType = Inventory.PAPERDOLL_UNDER;
					break;
				}
				case 14:
				{
					itemType = Inventory.PAPERDOLL_CLOAK;
					break;
				}
				case 15:
				{
					itemType = Inventory.PAPERDOLL_BELT;
					break;
				}
				default:
				{
					itemType = 0;
					break;
				}
			}
			final boolean success = setEnchant(player, enchant, itemType);
			return success ? "Item has been successfully enchanted." : "Failed to enchant player's item!";
		}
		return "Couldn't find player with such name.";
	}
	
	private boolean setEnchant(L2PcInstance activeChar, int ench, int armorType)
	{
		// now we need to find the equipped weapon of the targeted character...
		int curEnchant = 0; // display purposes only
		L2ItemInstance itemInstance = null;
		
		// only attempt to enchant if there is a weapon equipped
		L2ItemInstance parmorInstance = activeChar.getInventory().getPaperdollItem(armorType);
		if ((parmorInstance != null) && (parmorInstance.getLocationSlot() == armorType))
		{
			itemInstance = parmorInstance;
		}
		else
		{
			// for bows/crossbows and double handed weapons
			parmorInstance = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
			if ((parmorInstance != null) && (parmorInstance.getLocationSlot() == Inventory.PAPERDOLL_RHAND))
			{
				itemInstance = parmorInstance;
			}
		}
		
		if (itemInstance != null)
		{
			curEnchant = itemInstance.getEnchantLevel();
			
			// set enchant value
			activeChar.getInventory().unEquipItemInSlot(armorType);
			itemInstance.setEnchantLevel(ench);
			activeChar.getInventory().equipItem(itemInstance);
			
			// send packets
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(itemInstance);
			activeChar.sendPacket(iu);
			activeChar.broadcastUserInfo();
			
			// informations
			activeChar.sendMessage("Changed enchantment of " + activeChar.getName() + "'s " + itemInstance.getItem().getName() + " from " + curEnchant + " to " + ench + ".");
			activeChar.sendMessage("Admin has changed the enchantment of your " + itemInstance.getItem().getName() + " from " + curEnchant + " to " + ench + ".");
			
			// log
			GMAudit.auditGMAction("TelnetAdmin", "enchant", activeChar.getName(), itemInstance.getItem().getName() + "(" + itemInstance.getObjectId() + ") from " + curEnchant + " to " + ench);
			return true;
		}
		return false;
	}
}
