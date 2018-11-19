package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.matching.MatchingRoom;

/**
 * @author VISTALL
 */
public class RequestExDismissMpccRoom extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{

	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
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