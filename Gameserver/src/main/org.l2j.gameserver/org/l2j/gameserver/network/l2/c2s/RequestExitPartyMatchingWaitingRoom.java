package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

/**
 * Format: (ch)
 */
public class RequestExitPartyMatchingWaitingRoom extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		MatchingRoomManager.getInstance().removeFromWaitingList(player);
	}
}