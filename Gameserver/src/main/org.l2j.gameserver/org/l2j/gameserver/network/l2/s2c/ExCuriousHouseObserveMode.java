package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

@StaticPacket
public class ExCuriousHouseObserveMode extends L2GameServerPacket
{
	public static final L2GameServerPacket ENTER = new ExCuriousHouseObserveMode(false);
	public static final L2GameServerPacket LEAVE = new ExCuriousHouseObserveMode(true);

	private final boolean _leave;

	public ExCuriousHouseObserveMode(boolean leave)
	{
		_leave = leave;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte) (_leave ? 1 : 0));
	}
}