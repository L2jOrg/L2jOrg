package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.Config;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.utils.Location;

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
	protected final void writeImpl()
	{
		writeD(_targetId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z + Config.CLIENT_Z_SHIFT);
		writeD(0x00); //IsValidation
		writeD(_loc.h);
		writeD(0); // ??? 0
	}
}