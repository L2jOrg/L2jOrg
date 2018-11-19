package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.matching.MatchingRoom;
import l2s.gameserver.network.l2.components.SystemMsg;

/**
 * format (ch) d
 */
public class RequestOustFromPartyRoom extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();

		final MatchingRoom room = player.getMatchingRoom();
		if(room == null || room.getType() != MatchingRoom.PARTY_MATCHING)
			return;

		if(room.getLeader() != player)
			return;

		final Player member = GameObjectsStorage.getPlayer(_objectId);
		if(member == null)
			return;

		final int type = room.getMemberType(member);
		if (type == MatchingRoom.ROOM_MASTER)
			return;
		if (type == MatchingRoom.PARTY_MEMBER)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_DISMISS_A_PARTY_MEMBER_BY_FORCE);
			return;
		}

		room.removeMember(member, true);
	}
}