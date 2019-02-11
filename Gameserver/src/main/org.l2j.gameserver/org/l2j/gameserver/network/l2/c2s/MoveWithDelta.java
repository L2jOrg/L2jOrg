package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

/**
 * Format: (c) ddd
 * d: dx
 * d: dy
 * d: dz
 */
public class MoveWithDelta extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _dx, _dy, _dz;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_dx = buffer.getInt();
		_dy = buffer.getInt();
		_dz = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		// TODO this
	}
}