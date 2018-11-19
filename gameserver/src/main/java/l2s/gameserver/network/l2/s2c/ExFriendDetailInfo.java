package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.cache.CrestCache;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Friend;

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
		writeD(_objectId); // Character ID 
		writeS(_friend.getName()); // Name 
		writeD(_friend.isOnline()); // Online 
		writeD(_friend.isOnline() ? _friend.getObjectId() : 0); // Friend OID 
		writeH(_friend.getLevel()); // Level 
		writeH(_friend.getClassId()); // Class 
		writeD(_friend.getClanId()); // Pledge ID 
		writeD(_clanCrestId); // Pledge crest ID 
		writeS(_friend.getClanName()); // Pledge name 
		writeD(_friend.getAllyId()); // Alliance ID 
		writeD(_allyCrestId); // Alliance crest ID 
		writeS(_friend.getAllyName()); // Alliance name 
		writeC(_friend.getCreationMonth() + 1); // Creation month 
		writeC(_friend.getCreationDay()); // Creation day 
		writeD(_friend.getLastAccessDelay()); // Last login 
		writeS(_friend.getMemo()); // Memo
	}
}
