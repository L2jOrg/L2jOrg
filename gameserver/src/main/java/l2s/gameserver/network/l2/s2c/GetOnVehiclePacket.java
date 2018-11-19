package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.boat.Boat;
import l2s.gameserver.utils.Location;

public class GetOnVehiclePacket extends L2GameServerPacket
{
	private int _playerObjectId, _boatObjectId;
	private Location _loc;

	public GetOnVehiclePacket(Player activeChar, Boat boat, Location loc)
	{
		_loc = loc;
		_playerObjectId = activeChar.getObjectId();
		_boatObjectId = boat.getBoatId();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_playerObjectId);
		writeD(_boatObjectId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
	}
}