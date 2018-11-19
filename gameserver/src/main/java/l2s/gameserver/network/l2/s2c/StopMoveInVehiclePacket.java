package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.utils.Location;

public class StopMoveInVehiclePacket extends L2GameServerPacket
{
	private int _boatObjectId, _playerObjectId, _heading;
	private Location _loc;

	public StopMoveInVehiclePacket(Player player)
	{
		_boatObjectId = player.getBoat().getBoatId();
		_playerObjectId = player.getObjectId();
		_loc = player.getInBoatPosition();
		_heading = player.getHeading();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_playerObjectId);
		writeD(_boatObjectId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_heading);
	}
}