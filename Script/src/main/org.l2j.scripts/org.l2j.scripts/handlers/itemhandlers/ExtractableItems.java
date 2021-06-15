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

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.ExtractableProduct;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.item.EtcItem;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Extractable Items handler.
 * @author HorridoJoho, Mobius
 * @author JoeAlisson
 */
public class ExtractableItems implements IItemHandler {

	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse) {
		if(!(item.getTemplate() instanceof EtcItem etcItem)) {
			return false;
		}

		if (!(playable instanceof Player player)) {
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}

		final List<ExtractableProduct> extractables = etcItem.getExtractableItems();

		if (isNull(extractables)) {
			LOGGER.info("No extractable data defined for {}",  etcItem);
			return false;
		}
		
		if (!player.isInventoryUnder80()) {
			player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
			return false;
		}

		if (!player.destroyItem("Extract", item.getObjectId(), 1, player, true)) {
			return false;
		}

		var inventoryUpdate = new InventoryUpdate();
		int extracted = 0;
		for (ExtractableProduct product : extractables) {

			if (etcItem.getMaxExtractable() > 0 && extracted >= etcItem.getMaxExtractable()) {
				break;
			}

			if (Rnd.chance(product.chance())) {
				final int min = (int) (product.min() * Config.RATE_EXTRACTABLE);
				final int max = (int) (product.max() * Config.RATE_EXTRACTABLE);

				int amount = Rnd.get(min, max);
				if (amount == 0) {
					continue;
				}

				int enchant = Rnd.get(product.minEnchant(), product.maxEnchant());
				var extractedItem = player.addItem("Extract", product.id(), amount, enchant,  item, true, false);
				if(nonNull(extractedItem)) {
					extracted++;
					inventoryUpdate.addItem(extractedItem);
				}
			}
		}
		
		if (extracted == 0) {
			player.sendPacket(SystemMessageId.THERE_WAS_NOTHING_FOUND_INSIDE);
		} else {
			player.sendInventoryUpdate(inventoryUpdate);
		}
		return true;
	}
}
