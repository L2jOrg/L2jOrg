package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.cache.CrestCache;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Friend;

/**
 * @author Bonux
 */
public class ExFriendDetailInfo extends L2GameServerPacket
{
	private final int _objectId;
	private final Friend _friend;
	private final int _clanCrestId;
	private final int _allyCrestId;

	public ExFriendDetailInfo(Player player, Friend friend)
	{
		_objectId = player.getObjectId();
		_friend = friend;
		_clanCrestId = _friend.getClanId() > 0 ? CrestCache.getInstance().getPledgeCrestId(_friend.getClanId()) : 0;
		_allyCrestId = _friend.getAllyId() > 0 ? CrestCache.getInstance().getAllyCrestId(_friend.getAllyId()) : 0;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_objectId); // Character ID
		writeString(_friend.getName()); // Name
		writeInt(_friend.isOnline()); // Online
		writeInt(_friend.isOnline() ? _friend.getObjectId() : 0); // Friend OID
		writeShort(_friend.getLevel()); // Level
		writeShort(_friend.getClassId()); // Class
		writeInt(_friend.getClanId()); // Pledge ID
		writeInt(_clanCrestId); // Pledge crest ID
		writeString(_friend.getClanName()); // Pledge name
		writeInt(_friend.getAllyId()); // Alliance ID
		writeInt(_allyCrestId); // Alliance crest ID
		writeString(_friend.getAllyName()); // Alliance name
		writeByte(_friend.getCreationMonth() + 1); // Creation month
		writeByte(_friend.getCreationDay()); // Creation day
		writeInt(_friend.getLastAccessDelay()); // Last login
		writeString(_friend.getMemo()); // Memo
	}
}
