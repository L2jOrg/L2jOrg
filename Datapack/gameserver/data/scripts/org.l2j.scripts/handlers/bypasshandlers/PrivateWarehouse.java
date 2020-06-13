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
package handlers.bypasshandlers;

import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.WareHouseWithdrawalList;
import org.l2j.gameserver.network.serverpackets.item.WarehouseDepositList;

import static org.l2j.gameserver.util.GameUtils.isNpc;

public class PrivateWarehouse implements IBypassHandler {

	private static final String[] COMMANDS = {
		"withdrawp",
		"depositp"
	};
	
	@Override
	public boolean useBypass(String command, Player player, Creature target) {
		if (!isNpc(target)) {
			return false;
		}
		
		if (player.hasItemRequest()) {
			return false;
		}

		if (command.toLowerCase().startsWith(COMMANDS[0])) { // WithdrawP
			showWithdrawWindow(player);
			return true;
		}
		else if (command.toLowerCase().startsWith(COMMANDS[1])) { // DepositP
			player.setActiveWarehouse(player.getWarehouse());
			player.setInventoryBlockingStatus(true);
			WarehouseDepositList.openOfPlayer(player);
			return true;
		}
		return false;
	}
	
	private static void showWithdrawWindow(Player player) {
		player.setActiveWarehouse(player.getWarehouse());
		
		if (player.getActiveWarehouse().getSize() == 0) {
			player.sendPacket(SystemMessageId.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
			return;
		}
		
		player.sendPacket(new WareHouseWithdrawalList(1, player, WareHouseWithdrawalList.PRIVATE));
		player.sendPacket(new WareHouseWithdrawalList(2, player, WareHouseWithdrawalList.PRIVATE));
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
