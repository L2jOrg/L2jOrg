package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.cache.CrestCache;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Friend;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId); // Character ID
		writeString(_friend.getName(), buffer); // Name
		buffer.putInt(_friend.isOnline() ? 0x01 : 0x00); // Online
		buffer.putInt(_friend.isOnline() ? _friend.getObjectId() : 0); // Friend OID
		buffer.putShort((short) _friend.getLevel()); // Level
		buffer.putShort((short) _friend.getClassId()); // Class
		buffer.putInt(_friend.getClanId()); // Pledge ID
		buffer.putInt(_clanCrestId); // Pledge crest ID
		writeString(_friend.getClanName(), buffer); // Pledge name
		buffer.putInt(_friend.getAllyId()); // Alliance ID
		buffer.putInt(_allyCrestId); // Alliance crest ID
		writeString(_friend.getAllyName(), buffer); // Alliance name
		buffer.put((byte) (_friend.getCreationMonth() + 1)); // Creation month
		buffer.put((byte)_friend.getCreationDay()); // Creation day
		buffer.putInt(_friend.getLastAccessDelay()); // Last login
		writeString(_friend.getMemo(), buffer); // Memo
	}
}
