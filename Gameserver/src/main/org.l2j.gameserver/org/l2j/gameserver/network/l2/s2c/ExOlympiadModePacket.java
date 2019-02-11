package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExOlympiadModePacket extends L2GameServerPacket
{
	// chc
	private int _mode;

	/**
	 * @param _mode (0 = return, 3 = spectate)
	 */
	public ExOlympiadModePacket(int mode)
	{
		_mode = mode;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte)_mode);
	}
}