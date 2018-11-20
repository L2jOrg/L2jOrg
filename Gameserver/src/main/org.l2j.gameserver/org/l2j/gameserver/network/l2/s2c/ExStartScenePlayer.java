package org.l2j.gameserver.network.l2.s2c;

public class ExStartScenePlayer extends L2GameServerPacket
{
	private final int _sceneId;

	public ExStartScenePlayer(int sceneId)
	{
		_sceneId = sceneId;
	}

	@Override
	protected void writeImpl()
	{
		writeInt(_sceneId);
	}
}