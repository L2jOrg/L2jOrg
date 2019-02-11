package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExStartScenePlayer extends L2GameServerPacket
{
	private final int _sceneId;

	public ExStartScenePlayer(int sceneId)
	{
		_sceneId = sceneId;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_sceneId);
	}
}