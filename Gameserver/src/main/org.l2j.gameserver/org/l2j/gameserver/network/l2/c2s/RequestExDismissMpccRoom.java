package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
public class RequestExDismissMpccRoom extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{

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

		room.disband();
	}
}