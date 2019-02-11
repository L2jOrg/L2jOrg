package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class FlyToLocationPacket extends L2GameServerPacket
{
	private final int _chaObjId;
	private final FlyType _type;
	private final Location _loc;
	private final Location _destLoc;
	private final int _flySpeed;
	private final int _flyDelay;
	private final int _animationSpeed;

	public enum FlyType
	{
		THROW_UP,
		THROW_HORIZONTAL,
		DUMMY,
		CHARGE,
		PUSH_HORIZONTAL,
		JUMP_EFFECTED,
		NONE,
		PUSH_DOWN_HORIZONTAL,
		WARP_BACK,
		WARP_FORWARD;
	}

	public FlyToLocationPacket(Creature cha, Location destLoc, FlyType type, int flySpeed, int flyDelay, int animationSpeed)
	{
		_destLoc = destLoc;
		_type = type;
		_chaObjId = cha.getObjectId();
		_loc = cha.getLoc();
		_flySpeed = flySpeed;
		_flyDelay = flyDelay;
		_animationSpeed = animationSpeed;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_chaObjId);
		buffer.putInt(_destLoc.x);
		buffer.putInt(_destLoc.y);
		buffer.putInt(_destLoc.z);
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);
		buffer.putInt(_type.ordinal());
		buffer.putInt(_flySpeed);
		buffer.putInt(_flyDelay);
		buffer.putInt(_animationSpeed);
	}
}