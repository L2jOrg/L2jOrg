package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * 0000: 3e 2a 89 00 4c 01 00 00 00                         .|...
 *
 * format   dd
 */
public class ChangeMoveTypePacket extends L2GameServerPacket
{
	public static int WALK = 0;
	public static int RUN = 1;

	private int _chaId;
	private boolean _running;

	public ChangeMoveTypePacket(Creature cha)
	{
		_chaId = cha.getObjectId();
		_running = cha.isRunning();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_chaId);
		buffer.putInt(_running ? 1 : 0);
		buffer.putInt(0); //c2
	}
}