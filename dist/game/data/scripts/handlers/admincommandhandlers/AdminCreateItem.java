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
package handlers.admincommandhandlers;

import java.util.StringTokenizer;

import com.l2jmobius.gameserver.datatables.ItemTable;
import com.l2jmobius.gameserver.handler.IAdminCommandHandler;
import com.l2jmobius.gameserver.handler.IItemHandler;
import com.l2jmobius.gameserver.handler.ItemHandler;
import com.l2jmobius.gameserver.model.L2Object;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.items.L2Item;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.network.serverpackets.ExAdenaInvenCount;
import com.l2jmobius.gameserver.network.serverpackets.GMViewItemList;
import com.l2jmobius.gameserver.util.BuilderUtil;

/**
 * This class handles following admin commands: - itemcreate = show menu - create_item <id> [num] = creates num items with respective id, if num is not specified, assumes 1.
 * @version $Revision: 1.2.2.2.2.3 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminCreateItem implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_itemcreate",
		"admin_create_item",
		"admin_create_coin",
		"admin_give_item_target",
		"admin_give_item_to_all",
		"admin_delete_item",
		"admin_use_item"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_itemcreate"))
		{
			AdminHtml.showAdminHtml(activeChar, "itemcreation.htm");
		}
		else if (command.startsWith("admin_create_item"))
		{
			try
			{
				final String val = command.substring(17);
				final StringTokenizer st = new StringTokenizer(val);
				if (st.countTokens() == 2)
				{
					final String id = st.nextToken();
					final int idval = Integer.parseInt(id);
					final String num = st.nextToken();
					final long numval = Long.parseLong(num);
					createItem(activeChar, activeChar, idval, numval);
				}
				else if (st.countTokens() == 1)
				{
					final String id = st.nextToken();
					final int idval = Integer.parseInt(id);
					createItem(activeChar, activeChar, idval, 1);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //create_item <itemId> [amount]");
			}
			catch (NumberFormatException nfe)
			{
				BuilderUtil.sendSysMessage(activeChar, "Specify a valid number.");
			}
			AdminHtml.showAdminHtml(activeChar, "itemcreation.htm");
		}
		else if (command.startsWith("admin_create_coin"))
		{
			try
			{
				final String val = command.substring(17);
				final StringTokenizer st = new StringTokenizer(val);
				if (st.countTokens() == 2)
				{
					final String name = st.nextToken();
					final int idval = getCoinId(name);
					if (idval > 0)
					{
						final String num = st.nextToken();
						final long numval = Long.parseLong(num);
						createItem(activeChar, activeChar, idval, numval);
					}
				}
				else if (st.countTokens() == 1)
				{
					final String name = st.nextToken();
					final int idval = getCoinId(name);
					createItem(activeChar, activeChar, idval, 1);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //create_coin <name> [amount]");
			}
			catch (NumberFormatException nfe)
			{
				BuilderUtil.sendSysMessage(activeChar, "Specify a valid number.");
			}
			AdminHtml.showAdminHtml(activeChar, "itemcreation.htm");
		}
		else if (command.startsWith("admin_give_item_target"))
		{
			try
			{
				final L2Object target = activeChar.getTarget();
				if ((target == null) || !target.isPlayer())
				{
					BuilderUtil.sendSysMessage(activeChar, "Invalid target.");
					return false;
				}
				
				final String val = command.substring(22);
				final StringTokenizer st = new StringTokenizer(val);
				if (st.countTokens() == 2)
				{
					final String id = st.nextToken();
					final int idval = Integer.parseInt(id);
					final String num = st.nextToken();
					final long numval = Long.parseLong(num);
					createItem(activeChar, (L2PcInstance) target, idval, numval);
				}
				else if (st.countTokens() == 1)
				{
					final String id = st.nextToken();
					final int idval = Integer.parseInt(id);
					createItem(activeChar, (L2PcInstance) target, idval, 1);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //give_item_target <itemId> [amount]");
			}
			catch (NumberFormatException nfe)
			{
				BuilderUtil.sendSysMessage(activeChar, "Specify a valid number.");
			}
			AdminHtml.showAdminHtml(activeChar, "itemcreation.htm");
		}
		else if (command.startsWith("admin_give_item_to_all"))
		{
			final String val = command.substring(22);
			final StringTokenizer st = new StringTokenizer(val);
			int idval = 0;
			long numval = 0;
			if (st.countTokens() == 2)
			{
				final String id = st.nextToken();
				idval = Integer.parseInt(id);
				final String num = st.nextToken();
				numval = Long.parseLong(num);
			}
			else if (st.countTokens() == 1)
			{
				final String id = st.nextToken();
				idval = Integer.parseInt(id);
				numval = 1;
			}
			int counter = 0;
			final L2Item template = ItemTable.getInstance().getTemplate(idval);
			if (template == null)
			{
				BuilderUtil.sendSysMessage(activeChar, "This item doesn't exist.");
				return false;
			}
			if ((numval > 10) && !template.isStackable())
			{
				BuilderUtil.sendSysMessage(activeChar, "This item does not stack - Creation aborted.");
				return false;
			}
			for (L2PcInstance onlinePlayer : L2World.getInstance().getPlayers())
			{
				if ((activeChar != onlinePlayer) && onlinePlayer.isOnline() && ((onlinePlayer.getClient() != null) && !onlinePlayer.getClient().isDetached()))
				{
					onlinePlayer.getInventory().addItem("Admin", idval, numval, onlinePlayer, activeChar);
					onlinePlayer.sendMessage("Admin spawned " + numval + " " + template.getName() + " in your inventory.");
					counter++;
				}
			}
			activeChar.sendMessage(counter + " players rewarded with " + template.getName());
		}
		else if (command.startsWith("admin_delete_item"))
		{
			final String val = command.substring(18);
			final StringTokenizer st = new StringTokenizer(val);
			int idval = 0;
			long numval = 0;
			if (st.countTokens() == 2)
			{
				final String id = st.nextToken();
				idval = Integer.parseInt(id);
				final String num = st.nextToken();
				numval = Long.parseLong(num);
			}
			else if (st.countTokens() == 1)
			{
				final String id = st.nextToken();
				idval = Integer.parseInt(id);
				numval = 1;
			}
			final L2ItemInstance item = (L2ItemInstance) L2World.getInstance().findObject(idval);
			final int ownerId = item.getOwnerId();
			if (ownerId > 0)
			{
				final L2PcInstance player = L2World.getInstance().getPlayer(ownerId);
				if (player == null)
				{
					BuilderUtil.sendSysMessage(activeChar, "Player is not online.");
					return false;
				}
				
				if (numval == 0)
				{
					numval = item.getCount();
				}
				
				player.getInventory().destroyItem("AdminDelete", idval, numval, activeChar, null);
				activeChar.sendPacket(new GMViewItemList(1, player));
				BuilderUtil.sendSysMessage(activeChar, "Item deleted.");
			}
			else
			{
				BuilderUtil.sendSysMessage(activeChar, "Item doesn't have owner.");
				return false;
			}
		}
		else if (command.startsWith("admin_use_item"))
		{
			final String val = command.substring(15);
			final int idval = Integer.parseInt(val);
			final L2ItemInstance item = (L2ItemInstance) L2World.getInstance().findObject(idval);
			final int ownerId = item.getOwnerId();
			if (ownerId > 0)
			{
				final L2PcInstance player = L2World.getInstance().getPlayer(ownerId);
				if (player == null)
				{
					BuilderUtil.sendSysMessage(activeChar, "Player is not online.");
					return false;
				}
				
				// equip
				if (item.isEquipable())
				{
					player.useEquippableItem(item, false);
				}
				else
				{
					final IItemHandler ih = ItemHandler.getInstance().getHandler(item.getEtcItem());
					if (ih != null)
					{
						ih.useItem(player, item, false);
					}
				}
				activeChar.sendPacket(new GMViewItemList(1, player));
			}
			else
			{
				BuilderUtil.sendSysMessage(activeChar, "Item doesn't have owner.");
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
	
	private void createItem(L2PcInstance activeChar, L2PcInstance target, int id, long num)
	{
		final L2Item template = ItemTable.getInstance().getTemplate(id);
		if (template == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "This item doesn't exist.");
			return;
		}
		if ((num > 10) && !template.isStackable())
		{
			BuilderUtil.sendSysMessage(activeChar, "This item does not stack - Creation aborted.");
			return;
		}
		
		target.getInventory().addItem("Admin", id, num, activeChar, null);
		
		if (activeChar != target)
		{
			target.sendMessage("Admin spawned " + num + " " + template.getName() + " in your inventory.");
		}
		BuilderUtil.sendSysMessage(activeChar, "You have spawned " + num + " " + template.getName() + "(" + id + ") in " + target.getName() + " inventory.");
		target.sendPacket(new ExAdenaInvenCount(target));
	}
	
	private int getCoinId(String name)
	{
		int id;
		if (name.equalsIgnoreCase("adena"))
		{
			id = 57;
		}
		else if (name.equalsIgnoreCase("ancientadena"))
		{
			id = 5575;
		}
		else if (name.equalsIgnoreCase("festivaladena"))
		{
			id = 6673;
		}
		else if (name.equalsIgnoreCase("blueeva"))
		{
			id = 4355;
		}
		else if (name.equalsIgnoreCase("goldeinhasad"))
		{
			id = 4356;
		}
		else if (name.equalsIgnoreCase("silvershilen"))
		{
			id = 4357;
		}
		else if (name.equalsIgnoreCase("bloodypaagrio"))
		{
			id = 4358;
		}
		else if (name.equalsIgnoreCase("fantasyislecoin"))
		{
			id = 13067;
		}
		else
		{
			id = 0;
		}
		
		return id;
	}
}
