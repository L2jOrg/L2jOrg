/*
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
package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ChooseInventoryItem;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class EnchantScrolls implements IItemHandler {

	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse) {
		if (!isPlayer(playable)) {
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final Player player = playable.getActingPlayer();
		if (player.isCastingNow()) {
			return false;
		}
		
		if (player.hasItemRequest()) {
			player.sendPacket(SystemMessageId.ANOTHER_ENCHANTMENT_IS_IN_PROGRESS_PLEASE_COMPLETE_THE_PREVIOUS_TASK_THEN_TRY_AGAIN);
			return false;
		}
		
		player.addRequest(new EnchantItemRequest(player, item.getObjectId()));
		player.sendPacket(new ChooseInventoryItem(item.getId()));
		return true;
	}
}
