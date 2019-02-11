package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;

import java.nio.ByteBuffer;

/**
 * Format (ch) dd
 */
public class RequestWithdrawPartyRoom extends L2GameClientPacket
{
	private int _roomId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_roomId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		final Player player = client.getActiveChar();
		if(player == null)
			return;

		final MatchingRoom room = player.getMatchingRoom();
		if(room == null || room.getId() != _roomId || room.getType() != MatchingRoom.PARTY_MATCHING)
			return;

		final int type = room.getMemberType(player);
		if(type == MatchingRoom.ROOM_MASTER || type == MatchingRoom.PARTY_MEMBER)
		{
			player.setMatchingRoomWindowOpened(false);
			return;
		}

		room.removeMember(player, false);
	}
}