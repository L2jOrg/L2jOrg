package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.matching.MatchingRoom;

/**
 * @author VISTALL
 */
public class RequestExOustFromMpccRoom extends L2GameClientPacket
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
		Player player = getClient().getActiveChar();
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