/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.handlers.voicedcommandhandlers;

import org.l2j.gameserver.handler.IVoicedCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.settings.CustomFeatures;

/**
 * This class trades Gold Bars for Adena and vice versa.
 * @author Ahmed
 */
public class Banking implements IVoicedCommandHandler {

	private static final String[] _voicedCommands = {
		"bank",
		"withdraw",
		"deposit"
	};

	@Override
	public boolean useVoicedCommand(String command, Player player, String params) {
		return switch (command) {
			case "bank" -> bank(player);
			case "deposit" -> deposit(player);
			case "withdraw" -> withdraw(player);
			default -> false;
		};
	}

	private boolean bank(Player player) {
		player.sendMessage(".deposit (" + CustomFeatures.bankingAdenaCount() + " Adena = " + CustomFeatures.bankingGoldBarCount() + " Goldbar) / .withdraw (" + CustomFeatures.bankingGoldBarCount() + " Goldbar = " + CustomFeatures.bankingAdenaCount() + " Adena)");
		return true;
	}

	private boolean withdraw(Player player) {
		if (!player.destroyItemByItemId("Adena", 3470, CustomFeatures.bankingGoldBarCount(), player, false)) {
			player.sendMessage("You do not have any Goldbars to turn into Adena.");
			return false;
		}
		player.getInventory().addAdena("Adena", CustomFeatures.bankingAdenaCount(), player, null);
		player.sendMessage("Thank you, you now have " + CustomFeatures.bankingAdenaCount() + " Adena, and " + CustomFeatures.bankingGoldBarCount() + " less Goldbar(s).");
		return true;
	}

	private boolean deposit(Player player) {
		if (!player.reduceAdena("Goldbar", CustomFeatures.bankingAdenaCount(), player, false)) {
			player.sendMessage("You do not have enough Adena to convert to Goldbar(s), you need " + CustomFeatures.bankingAdenaCount() + " Adena.");
			return false;
		}
		player.getInventory().addItem("Goldbar", 3470, CustomFeatures.bankingGoldBarCount(), player, null);
		player.sendMessage("Thank you, you now have " + CustomFeatures.bankingGoldBarCount() + " Goldbar(s), and " + CustomFeatures.bankingAdenaCount() + " less adena.");
		return true;
	}

	@Override
	public String[] getVoicedCommandList() {
		return _voicedCommands;
	}
}