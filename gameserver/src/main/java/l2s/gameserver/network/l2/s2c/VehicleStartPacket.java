package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.entity.boat.Boat;

public class VehicleStartPacket extends L2GameServerPacket
{
	private int _objectId, _state;

	public VehicleStartPacket(Boat boat)
	{
		_objectId = boat.getBoatId();
		_state = boat.getRunState();
	}

	@Override
	protected void writeImpl()
	{
		writeD(_objectId);
		writeD(_state);
	}
}