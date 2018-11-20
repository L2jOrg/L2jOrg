package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.utils.Location;

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
	protected final void writeImpl()
	{
		writeInt(_targetId);
		writeInt(_loc.x);
		writeInt(_loc.y);
		writeInt(_loc.z + Config.CLIENT_Z_SHIFT);
		writeInt(0x00); //IsValidation
		writeInt(_loc.h);
		writeInt(0); // ??? 0
	}
}