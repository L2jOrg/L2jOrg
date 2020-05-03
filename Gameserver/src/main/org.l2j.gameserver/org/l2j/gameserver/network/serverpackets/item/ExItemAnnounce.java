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