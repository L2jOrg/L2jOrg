package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.matching.MatchingRoom;

/**
 * Format (ch) dd
 */
public class RequestWithdrawPartyRoom extends L2GameClientPacket
{
	private int _roomId;

	@Override
	protected void readImpl()
	{
		_roomId = readD();
	}

	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
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