package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
 */
public class ExStopScenePlayerPacket extends L2GameServerPacket
{
	private final int _movieId;

	public ExStopScenePlayerPacket(int movieId)
	{
		_movieId = movieId;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_movieId);
	}
}
