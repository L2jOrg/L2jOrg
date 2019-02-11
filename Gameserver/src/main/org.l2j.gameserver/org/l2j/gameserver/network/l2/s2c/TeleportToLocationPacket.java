package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

/**
 * format  dddd
 *
 * sample
 * 0000: 3a  69 08 10 48  02 c1 00 00  f7 56 00 00  89 ea ff    :i..H.....V.....
 * 0010: ff  0c b2 d8 61                                     ....a
 */
public class TeleportToLocationPacket extends L2GameServerPacket
{
	private int _targetId;
	private Location _loc;

	public TeleportToLocationPacket(GameObject cha, Location loc)
	{
		_targetId = cha.getObjectId();
		_loc = loc;
	}

	public TeleportToLocationPacket(GameObject cha, int x, int y, int z)
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