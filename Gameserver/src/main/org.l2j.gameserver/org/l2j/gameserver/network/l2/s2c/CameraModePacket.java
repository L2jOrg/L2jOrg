package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class CameraModePacket extends L2GameServerPacket
{
	int _mode;

	/**
	 * Forces client camera mode change
	 * @param mode
	 * 0 - third person cam
	 * 1 - first person cam
	 */
	public CameraModePacket(int mode)
	{
		_mode = mode;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_mode);
	}
}