package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class ExTeleportToLocationActivate extends L2GameServerPacket
{
	private int _targetId;
	private Location _loc;

	public ExTeleportToLocationActivate(GameObject cha, Location loc)
	{
		_targetId = cha.getObjectId();
		_loc = loc;
	}

	public ExTeleportToLocationActivate(GameObject cha, int x, int y, int z)
	{
		_targetId = cha.getObjectId();
		_loc = new Location(x, y, z, cha.getHeading());
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_targetId);
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z + Config.CLIENT_Z_SHIFT);
		buffer.putInt(0x00); //IsValidation
		buffer.putInt(_loc.h);
		buffer.putInt(0); // ??? 0
	}
}