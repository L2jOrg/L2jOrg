package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.BoatHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.boat.Boat;
import l2s.gameserver.utils.Location;

public class RequestGetOnVehicle extends L2GameClientPacket
{
	private int _objectId;
	private Location _loc = new Location();

	/**
	 * packet type id 0x53
	 * format:      cdddd
	 */
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_loc.x = readD();
		_loc.y = readD();
		_loc.z = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		Boat boat = BoatHolder.getInstance().getBoat(_objectId);
		if(boat == null)
			return;

		boat.addPlayer(player, _loc);
	}
}