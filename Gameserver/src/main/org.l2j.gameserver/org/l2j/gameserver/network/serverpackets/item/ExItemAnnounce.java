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
package org.l2j.gameserver.network.serverpackets.item;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExItemAnnounce extends ServerPacket {

	private final Item item;
	private final Player player;
	private final ItemAnnounceType type;
	private int sourceItemId;

	public ExItemAnnounce(ItemAnnounceType type, Player player, Item item) {
		this.item = item;
		this.player = player;
		this.type = type;
	}

	public ExItemAnnounce withSourceItem(int itemId) {
		sourceItemId = itemId;
		return this;
	}
	
	@Override
	public void writeImpl(GameClient client) {
		writeId(ServerExPacketId.EX_ITEM_ANNOUNCE);
		writeByte(type.ordinal());
		writeSizedString(player.getName());
		writeInt(item.getId());
		writeByte(item.getEnchantLevel());
		writeInt(sourceItemId);
	}
}