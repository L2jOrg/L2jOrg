package org.l2j.gameserver.handler.admincommands.impl;

import org.l2j.gameserver.handler.admincommands.IAdminCommandHandler;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.Inventory;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.s2c.InventoryUpdatePacket;
import org.l2j.gameserver.network.l2.components.HtmlMessage;

public class AdminEnchant implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_seteh, // 6
		admin_setec, // 10
		admin_seteg, // 9
		admin_setel, // 11
		admin_seteb, // 12
		admin_setew, // 7
		admin_setes, // 8
		admin_setle, // 1
		admin_setre, // 2
		admin_setlf, // 4
		admin_setrf, // 5
		admin_seten, // 3
		admin_setba,
		admin_setha,
		admin_setdha,
		admin_setlbr,
		admin_setrbr,
		admin_setbelt,
		admin_enchant
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditChar)
			return false;

		int armorType = -1;

		switch(command)
		{
			case admin_enchant:
				showMainPage(activeChar);
				return true;
			case admin_seteh:
				armorType = Inventory.PAPERDOLL_HEAD;
				break;
			case admin_setec:
				armorType = Inventory.PAPERDOLL_CHEST;
				break;
			case admin_seteg:
				armorType = Inventory.PAPERDOLL_GLOVES;
				break;
			case admin_seteb:
				armorType = Inventory.PAPERDOLL_FEET;
				break;
			case admin_setel:
				armorType = Inventory.PAPERDOLL_LEGS;
				break;
			case admin_setew:
				armorType = Inventory.PAPERDOLL_RHAND;
				break;
			case admin_setes:
				armorType = Inventory.PAPERDOLL_LHAND;
				break;
			case admin_setle:
				armorType = Inventory.PAPERDOLL_LEAR;
				break;
			case admin_setre:
				armorType = Inventory.PAPERDOLL_REAR;
				break;
			case admin_setlf:
				armorType = Inventory.PAPERDOLL_LFINGER;
				break;
			case admin_setrf:
				armorType = Inventory.PAPERDOLL_RFINGER;
				break;
			case admin_seten:
				armorType = Inventory.PAPERDOLL_NECK;
				break;
			case admin_setba:
				armorType = Inventory.PAPERDOLL_BACK;
				break;
			case admin_setha:
				armorType = Inventory.PAPERDOLL_HAIR;
				break;
			case admin_setdha:
				armorType = Inventory.PAPERDOLL_DHAIR;
				break;
			case admin_setlbr:
				armorType = Inventory.PAPERDOLL_LBRACELET;
				break;
			case admin_setrbr:
				armorType = Inventory.PAPERDOLL_RBRACELET;
				break;
			case admin_setbelt:
				armorType = Inventory.PAPERDOLL_BELT;
				break;
		}

		if(armorType == -1 || wordList.length < 2)
		{
			showMainPage(activeChar);
			return true;
		}

		try
		{
			int ench = Integer.parseInt(wordList[1]);
			if(ench < 0 || ench > 65535)
				activeChar.sendMessage("You must set the enchant level to be between 0-65535.");
			else
				setEnchant(activeChar, ench, armorType);
		}
		catch(StringIndexOutOfBoundsException e)
		{
			activeChar.sendMessage("Please specify a new enchant value.");
		}
		catch(NumberFormatException e)
		{
			activeChar.sendMessage("Please specify a valid new enchant value.");
		}

		// show the enchant menu after an action
		showMainPage(activeChar);
		return true;
	}

	private void setEnchant(Player activeChar, int ench, int armorType)
	{
		// get the target
		GameObject target = activeChar.getTarget();
		if(target == null)
			target = activeChar;
		if(!target.isPlayer())
		{
			activeChar.sendMessage("Wrong target type.");
			return;
		}

		Player player = (Player) target;

		// now we need to find the equipped weapon of the targeted character...
		int curEnchant = 0; // display purposes only

		// only attempt to enchant if there is a weapon equipped
		ItemInstance itemInstance = player.getInventory().getPaperdollItem(armorType);

		if(itemInstance != null)
		{
			curEnchant = itemInstance.getEnchantLevel();

			// set enchant value
			boolean equipped = false;
			if ((equipped = itemInstance.isEquipped()))
			{
				player.getInventory().isRefresh = true;
				player.getInventory().unEquipItem(itemInstance);
			}

			itemInstance.setEnchantLevel(ench);

			if (equipped)
			{
				player.getInventory().equipItem(itemInstance);
				player.getInventory().isRefresh = false;
			}

			// send packets
			player.sendPacket(new InventoryUpdatePacket().addModifiedItem(player, itemInstance));
			player.broadcastCharInfo();

			// informations
			activeChar.sendMessage("Changed enchantment of " + player.getName() + "'s " + itemInstance.getName() + " from " + curEnchant + " to " + ench + ".");
			player.sendMessage("Admin has changed the enchantment of your " + itemInstance.getName() + " from " + curEnchant + " to " + ench + ".");
		}
	}

	public void showMainPage(Player activeChar)
	{
		// get the target
		GameObject target = activeChar.getTarget();
		if(target == null)
			target = activeChar;
		Player player = activeChar;
		if(target.isPlayer())
			player = (Player) target;

		HtmlMessage adminReply = new HtmlMessage(5);

		StringBuilder replyMSG = new StringBuilder("<html><body>");
		replyMSG.append("<center><table width=720><tr><td width=40>");
		replyMSG.append("<button value=\"Main\" action=\"bypass -h admin_admin\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("</td><td width=50>&nbsp;</td><td width=50>&nbsp;</td><td width=50>&nbsp;</td><td width=50>&nbsp;</td></tr></table></center><br>");
		replyMSG.append("<center>Enchant equip for player: " + player.getName() + "</center><br>");
		replyMSG.append("<center><table width=270><tr><td>");
		replyMSG.append("<button value=\"Helmet\" action=\"bypass -h admin_seteh $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Mask\" action=\"bypass -h admin_setha $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Hair\" action=\"bypass -h admin_setdha $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Cloak\" action=\"bypass -h admin_setba $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Necklace\" action=\"bypass -h admin_seten $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table>");
		replyMSG.append("</center><center><table width=270><tr><td>");
		replyMSG.append("<button value=\"Weapon\" action=\"bypass -h admin_setew $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Chest\" action=\"bypass -h admin_setec $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Shield\" action=\"bypass -h admin_setes $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Earring\" action=\"bypass -h admin_setre $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Earring\" action=\"bypass -h admin_setle $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table>");
		replyMSG.append("</center><center><table width=270><tr><td>");
		replyMSG.append("<button value=\"Gloves\" action=\"bypass -h admin_seteg $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Leggings\" action=\"bypass -h admin_setel $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Boots\" action=\"bypass -h admin_seteb $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Ring\" action=\"bypass -h admin_setrf $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Ring\" action=\"bypass -h admin_setlf $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table>");
		replyMSG.append("</center><center><table width=270><tr><td width=50></td><td width=50></td><td width=50>");
		replyMSG.append("<button value=\"R-Bracelet\" action=\"bypass -h admin_setrbr $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td width=50>&nbsp;</td><td>");
		replyMSG.append("<button value=\"L-Bracelet\" action=\"bypass -h admin_setlbr $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Belt\" action=\"bypass -h admin_setbelt $menu_command\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table>");
		replyMSG.append("</center><br>");
		replyMSG.append("<center>[Enchant 0-127]</center>");
		replyMSG.append("<center><edit var=\"menu_command\" width=100 height=15></center><br>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}