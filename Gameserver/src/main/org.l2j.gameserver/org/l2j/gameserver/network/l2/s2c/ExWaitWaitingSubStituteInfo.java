package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

@StaticPacket
public class ExWaitWaitingSubStituteInfo extends L2GameServerPacket
{
	public static final L2GameServerPacket OPEN = new ExWaitWaitingSubStituteInfo(true);
	public static final L2GameServerPacket CLOSE = new ExWaitWaitingSubStituteInfo(false);

	private boolean _open;

	public ExWaitWaitingSubStituteInfo(boolean open)
	{
		_open = open;
	}

	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_open ? 0x01 : 0x00);
	}
}