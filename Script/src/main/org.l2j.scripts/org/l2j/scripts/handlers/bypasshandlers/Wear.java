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
package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.network.serverpackets.ShopPreviewList;
import org.l2j.gameserver.settings.GeneralSettings;

import java.util.StringTokenizer;

import static org.l2j.gameserver.util.GameUtils.isNpc;

public class Wear implements IBypassHandler {

	private static final String[] COMMANDS = { "Wear" };
	
	@Override
	public boolean useBypass(String command, Player player, Creature target) {
		if (!GeneralSettings.allowWear() || !isNpc(target)) {
			return false;
		}

		var tokens = new StringTokenizer(command, " ");
		tokens.nextToken();
		if (tokens.countTokens() < 1) {
			return false;
		}
			
		return showWearWindow(player, Util.parseNextInt(tokens, 0));
	}
	
	private boolean showWearWindow(Player player, int val) {
		final ProductList buyList = BuyListData.getInstance().getBuyList(val);
		if (buyList == null) {
			LOGGER.warn("BuyList not found! BuyListId: {}", val);
			return false;
		}
		
		player.setInventoryBlockingStatus(true);
		player.sendPacket(new ShopPreviewList(buyList, player.getAdena()));
		return true;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
