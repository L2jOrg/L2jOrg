package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;
import l2s.gameserver.utils.Location;

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
		writeD(_charObjId);
		writeD(_fishType); // fish type
		writeD(_loc.x); // x poisson
		writeD(_loc.y); // y poisson
		writeD(_loc.z); // z poisson
		writeC(_isNightLure ? 0x01 : 0x00); // 0 = day lure  1 = night lure
		writeC(0x01); // result Button
	}
}