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

import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.util.BuilderUtil;

import java.util.StringTokenizer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.enums.InventorySlot.*;

/**
 * @author JoeAlisson
 */
public class AdminEnchant implements IAdminCommandHandler {
	
	private static final String[] ADMIN_COMMANDS = {
		"admin_seteh", // 6
		"admin_setec", // 10
		"admin_seteg", // 9
		"admin_setel", // 11
		"admin_seteb", // 12
		"admin_setew", // 7
		"admin_setes", // 8
		"admin_setle", // 1
		"admin_setre", // 2
		"admin_setlf", // 4
		"admin_setrf", // 5
		"admin_seten", // 3
		"admin_setun", // 0
		"admin_setba", // 13
		"admin_setbe",
		"admin_enchant"
	};

	@Override
	public boolean useAdminCommand(String command, Player player) {
		var tokens = new StringTokenizer(command);
		if(tokens.hasMoreTokens()) {
			InventorySlot itemSlot = switch (tokens.nextToken()) {
				case "admin_seteh" -> HEAD;
				case "admin_setec" -> CHEST;
				case "admin_seteg" -> GLOVES;
				case "admin_seteb" -> FEET;
				case "admin_setel" -> LEGS;
				case "admin_setew" -> RIGHT_HAND;
				case "admin_setes" -> LEFT_HAND;
				case "admin_setle" -> LEFT_EAR;
				case "admin_setre" -> RIGHT_EAR;
				case "admin_setlf" -> LEFT_FINGER;
				case "admin_setrf" -> RIGHT_FINGER;
				case "admin_seten" -> NECK;
				case "admin_setun" -> PENDANT;
				case "admin_setba" -> CLOAK;
				case "admin_setbe" -> BELT;
				default -> null;
			};

			if (nonNull(itemSlot) && tokens.hasMoreTokens()) {
				var enchant = Util.parseNextInt(tokens, -1);
				if (enchant < 0 || enchant > Short.MAX_VALUE) {
					BuilderUtil.sendSysMessage(player, "You must set the enchant level to be between 0 and 32767.");
				} else {
					setEnchant(player, enchant, itemSlot);
				}
			} else {
				BuilderUtil.sendSysMessage(player, "Incorrect Command " + command);
			}
		}
		showMainPage(player);
		return true;
	}
	
	private void setEnchant(Player player, int ench, InventorySlot itemSlot) {
		// get the target
		final Player target = nonNull(player.getTarget()) ? player.getTarget().getActingPlayer() : player;
		
		if (isNull(target)) {
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		// now we need to find the equipped weapon of the targeted character...
		Item itemInstance = null;
		
		// only attempt to enchant if there is a weapon equipped
		final Item item = target.getInventory().getPaperdollItem(itemSlot);
		if (nonNull(item ) && (item.getLocationSlot() == itemSlot.getId())) {
			itemInstance = item;
		}
		
		if (nonNull(itemInstance)) {
			final int curEnchant = itemInstance.getEnchantLevel();
			
			target.getInventory().unEquipItemInSlot(itemSlot);
			itemInstance.setEnchantLevel(ench);
			target.getInventory().equipItem(itemInstance);
			
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(itemInstance);
			target.sendInventoryUpdate(iu);
			target.broadcastUserInfo();
			
			BuilderUtil.sendSysMessage(player, String.format("Changed enchantment of %s's %s from %d  to %d.", target.getName(), itemInstance.getName(), curEnchant, ench));
			target.sendMessage(String.format("Admin has changed the enchantment of your %s from %d to %d.", itemInstance.getName(),curEnchant, ench));
		}
	}
	
	private void showMainPage(Player activeChar) {
		AdminHtml.showAdminHtml(activeChar, "enchant.htm");
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}
