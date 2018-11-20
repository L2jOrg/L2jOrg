package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.utils.Location;
import org.l2j.gameserver.utils.Log;

public class MTLPacket extends L2GameServerPacket
{
	private int _objectId, _client_z_shift;
	private Location _current;
	private Location _destination;

	public MTLPacket(Creature cha)
	{
		_objectId = cha.getObjectId();
		_current = cha.getLoc();
		_destination = cha.getDestination();
		if(!cha.isFlying())
			_client_z_shift = Config.CLIENT_Z_SHIFT;
		if(cha.isInWater())
			_client_z_shift += Config.CLIENT_Z_SHIFT;

		if(_destination == null)
		{
			Log.debug("MTLPacket: desc is null, but moving. L2Character: " + cha.getObjectId() + ":" + cha.getName() + "; Loc: " + _current);
			_destination = _current;
		}
	}

	public MTLPacket(int objectId, Location from, Location to)
	{
		_objectId = objectId;
		_current = from;
		_destination = to;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_objectId);

		writeInt(_destination.x);
		writeInt(_destination.y);
		writeInt(_destination.z + _client_z_shift);

		writeInt(_current.x);
		writeInt(_current.y);
		writeInt(_current.z + _client_z_shift);
	}
}