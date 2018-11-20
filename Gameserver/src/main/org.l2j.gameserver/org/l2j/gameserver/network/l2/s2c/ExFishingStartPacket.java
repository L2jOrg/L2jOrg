package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.utils.Location;

/**
 * Format (ch)ddddd
 */
public class ExFishingStartPacket extends L2GameServerPacket
{
	private int _charObjId;
	private Location _loc;
	private int _fishType;
	private boolean _isNightLure;

	public ExFishingStartPacket(Creature character, int fishType, Location loc, boolean isNightLure)
	{
		_charObjId = character.getObjectId();
		_fishType = fishType;
		_loc = loc;
		_isNightLure = isNightLure;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_charObjId);
		writeInt(_fishType); // fish type
		writeInt(_loc.x); // x poisson
		writeInt(_loc.y); // y poisson
		writeInt(_loc.z); // z poisson
		writeByte(_isNightLure ? 0x01 : 0x00); // 0 = day lure  1 = night lure
		writeByte(0x01); // result Button
	}
}