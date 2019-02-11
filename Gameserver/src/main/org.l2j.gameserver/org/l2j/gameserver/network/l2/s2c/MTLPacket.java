package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;
import org.l2j.gameserver.utils.Log;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);

		buffer.putInt(_destination.x);
		buffer.putInt(_destination.y);
		buffer.putInt(_destination.z + _client_z_shift);

		buffer.putInt(_current.x);
		buffer.putInt(_current.y);
		buffer.putInt(_current.z + _client_z_shift);
	}
}