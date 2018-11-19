package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.BoatHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.boat.Boat;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
 */
public class RequestMoveToLocationInShuttle extends L2GameClientPacket
{
	private Location _pos = new Location();
	private Location _originPos = new Location();
	private int _shuttleId;

	@Override
	protected void readImpl()
	{
		_shuttleId = readD();
		_pos.x = readD();
		_pos.y = readD();
		_pos.z = readD();
		_originPos.x = readD();
		_originPos.y = readD();
		_originPos.z = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		Boat boat = BoatHolder.getInstance().getBoat(_shuttleId);
		if(boat == null)
		{
			player.sendActionFailed();
			return;
		}

		boat.moveInBoat(player, _originPos, _pos);
	}
}