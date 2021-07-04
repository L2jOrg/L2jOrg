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
package org.l2j.gameserver.network.clientpackets.timedzone;

import io.github.joealisson.primitive.ArrayIntList;
import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.l2j.gameserver.world.zone.type.TimeRestrictZone;

import java.util.Collection;

import static org.l2j.gameserver.network.SystemMessageId.CANNOT_USE_TIMED_HUNTING_ZONES_WHILE_WAITING_FOR_THE_OLYMPIAD;
import static org.l2j.gameserver.network.SystemMessageId.TIMED_HUNTING_ZONES_HAVE_ENTRY_LEVEL_REQUIREMENTS_AVAILABLE_TIMES_AND_ENTRY_FEES_THAT_MUST_BE_MET_IN_ORDER_TO_ENTER;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class ExTimeRestrictFieldUserEnter extends ClientPacket {
	private int zoneId;

	@Override
	public void readImpl()
	{
		zoneId = readInt();
	}

	@Override
	public void runImpl() {
		final Player player = client.getPlayer();

		if(!checkPlayerState(player)) {
			return;
		}

		var zone = ZoneEngine.getInstance().getZoneById(zoneId, TimeRestrictZone.class);

		if(!checkZoneRequirements(player, zone)) {
			player.sendPacket(TIMED_HUNTING_ZONES_HAVE_ENTRY_LEVEL_REQUIREMENTS_AVAILABLE_TIMES_AND_ENTRY_FEES_THAT_MUST_BE_MET_IN_ORDER_TO_ENTER);
			return;
		}

		player.teleToLocation(zone.getSpawnLoc());
	}

	private boolean checkZoneRequirements(Player player, TimeRestrictZone zone) {
		if(zone == null || !zone.canEnter(player)) {
			return false;
		}
		return consumeRequiredItems(player, zone);
	}

	private boolean consumeRequiredItems(Player player, TimeRestrictZone zone) {
		var items = zone.requiredItems();
		if(items.size() > 1) {
			return consumeMultipleItems(player, items);
		}
		var consumed = true;
		for (var item : items) {
			consumed = player.destroyItemByItemId("TimedZone", item.getId(), item.getCount(), player, true);
		}
		return consumed;
	}

	private boolean consumeMultipleItems(Player player, Collection<ItemHolder> items) {
		var inventory = player.getInventory();
		try {
			inventory.setInventoryBlock(new ArrayIntList(items.size()), InventoryBlockType.BLACKLIST);

			for (var item : items) {
				inventory.addToInventoryBlock(item.getId());
				if(inventory.getInventoryItemCount(item.getId(), -1) < item.getCount()) {
					return false;
				}
			}

			for (var item : items) {
				player.destroyItemByItemId("TimedZone", item.getId(), item.getCount(), player, true);
			}
		} finally {
			inventory.unblock();
		}
		return true;
	}

	private boolean checkPlayerState(Player player) {
		if (player.isMounted()) {
			player.sendMessage("Cannot use time-limited hunting zones while mounted.");
			return false;
		}
		if (player.isInDuel()) {
			player.sendMessage("Cannot use time-limited hunting zones during a duel.");
			return false;
		}

		if (Olympiad.getInstance().isRegistered(player)) {
			player.sendPacket(CANNOT_USE_TIMED_HUNTING_ZONES_WHILE_WAITING_FOR_THE_OLYMPIAD);
			return false;
		}

		if (player.isOnEvent() || (player.getBlockCheckerArena() > -1))
		{
			player.sendMessage("Cannot use time-limited hunting zones while registered on an event.");
			return false;
		}

		if (player.isInInstance())
		{
			player.sendMessage("Cannot use time-limited hunting zones while in an instance.");
			return false;
		}
		return true;
	}
}
