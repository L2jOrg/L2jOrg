package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_charObjId);
		buffer.putInt(_fishType); // fish type
		buffer.putInt(_loc.x); // x poisson
		buffer.putInt(_loc.y); // y poisson
		buffer.putInt(_loc.z); // z poisson
		buffer.put((byte) (_isNightLure ? 0x01 : 0x00)); // 0 = day lure  1 = night lure
		buffer.put((byte)0x01); // result Button
	}
}