package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
public class RequestExOustFromMpccRoom extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_objectId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		MatchingRoom room = player.getMatchingRoom();
		if(room == null || room.getType() != MatchingRoom.CC_MATCHING)
			return;

		if(room.getLeader() != player)
			return;

		Player member = GameObjectsStorage.getPlayer(_objectId);
		if(member == null)
			return;

		if(member == room.getLeader())
			return;

		room.removeMember(member, true);
	}
}