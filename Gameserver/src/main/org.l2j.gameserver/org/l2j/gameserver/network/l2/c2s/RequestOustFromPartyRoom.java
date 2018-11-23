package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.l2.components.SystemMsg;

/**
 * format (ch) d
 */
public class RequestOustFromPartyRoom extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readInt();
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