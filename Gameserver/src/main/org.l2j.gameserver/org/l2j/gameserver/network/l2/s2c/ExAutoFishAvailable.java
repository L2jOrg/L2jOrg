package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public final class ExAutoFishAvailable extends L2GameServerPacket
{
	public static final L2GameServerPacket REMOVE = new ExAutoFishAvailable(0);
	public static final L2GameServerPacket SHOW = new ExAutoFishAvailable(1);
	public static final L2GameServerPacket FISHING = new ExAutoFishAvailable(2);

	private final int _type;

	private ExAutoFishAvailable(int type)
	{
		_type = type;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte)_type);
	}
}