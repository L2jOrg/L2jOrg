package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.matching.MatchingRoom;

/**
 * Format: (ch) dd
 */
public class RequestDismissPartyRoom extends L2GameClientPacket
{
	private int _roomId;

	@Override
	protected void readImpl()
	{
		_roomId = readD(); //room id
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		MatchingRoom room = player.getMatchingRoom();
		if(room == null || room.getId() != _roomId || room.getType() != MatchingRoom.PARTY_MATCHING)
			return;

		if(room.getLeader() != player)
			return;

		room.disband();
	}
}