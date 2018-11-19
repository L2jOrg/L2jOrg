package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.BoatHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.boat.Boat;
import l2s.gameserver.utils.Location;

public class RequestMoveToLocationInVehicle extends L2GameClientPacket
{
	private Location _pos = new Location();
	private Location _originPos = new Location();
	private int _boatObjectId;

	@Override
	protected void readImpl()
	{
		_boatObjectId = readD();
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
		
		Boat boat = BoatHolder.getInstance().getBoat(_boatObjectId);
		if(boat == null)
		{
			player.sendActionFailed();
			return;
		}

		boat.moveInBoat(player, _originPos, _pos);
	}
}