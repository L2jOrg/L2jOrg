package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;

public class RequestPartyMatchDetail extends L2GameClientPacket
{
	private int _roomId;
	private int _locations;
	private int _level;

	/**
	 * Format: dddd
	 */
	@Override
	protected void readImpl()
	{
		_roomId = readInt(); // room id, если 0 то autojoin
		_locations = readInt(); // location
		_level = readInt(); // 1 - all, 0 - my level (только при autojoin)
		//readInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(player.getMatchingRoom() != null)
			return;

		if(_roomId > 0)
		{
			MatchingRoom room = MatchingRoomManager.getInstance().getMatchingRoom(MatchingRoom.PARTY_MATCHING, _roomId);
			if(room == null)
				return;

			room.addMember(player);
		}
		else
		{
			for(MatchingRoom room : MatchingRoomManager.getInstance().getMatchingRooms(MatchingRoom.PARTY_MATCHING, _locations, _level == 1, player))
				if(room.addMember(player))
					break;
		}
	}
}