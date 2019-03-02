package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;

/**
 * @reworked to Ertheia by Bonux
**/
public class TradeStartPacket extends L2GameServerPacket
{
	private static final int IS_FRIEND = 1 << 0;
	private static final int CLAN_MEMBER = 1 << 1;
	private static final int ALLY_MEMBER = 1 << 3;

	private final List<ItemInfo> _tradelist = new ArrayList<ItemInfo>();
	private final int _targetId;
	private final int _targetLevel;
	private final int sendType;

	private int _flags = 0;

	public TradeStartPacket(int sendType, Player player, Player target) {
		this.sendType =sendType;
		_targetId = target.getObjectId();
		_targetLevel = target.getLevel();

		if(player.getFriendList().contains(target.getObjectId()))
			_flags |= IS_FRIEND;

		if(player.getClan() != null && player.getClan() == target.getClan())
			_flags |= CLAN_MEMBER;

		if(player.getAlliance() != null && player.getAlliance() == target.getAlliance())
			_flags |= ALLY_MEMBER;

		ItemInstance[] items = player.getInventory().getItems();
		for(ItemInstance item : items)
			if(item.canBeTraded(player))
				_tradelist.add(new ItemInfo(item, item.getTemplate().isBlocked(player, item)));
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer) {
		buffer.put((byte) sendType);
		if(sendType == 2) {
			buffer.putInt(_tradelist.size());
			buffer.putInt(_tradelist.size());
			for(ItemInfo item : _tradelist)
				writeItemInfo(buffer, item);
		}  else {
			buffer.putInt(_targetId);
			buffer.put((byte)_flags); // UNK
			if((_flags & 0x10) == 0) {
				buffer.put((byte) _targetLevel);
			}
		}
	}
}