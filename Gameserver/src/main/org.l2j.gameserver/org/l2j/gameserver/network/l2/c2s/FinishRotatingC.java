package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.FinishRotatingPacket;

import java.nio.ByteBuffer;

/**
 * format:		cdd
 */
public class FinishRotatingC extends L2GameClientPacket
{
	private int _degree;
	@SuppressWarnings("unused")
	private int _unknown;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_degree = buffer.getInt();
		_unknown = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;
		activeChar.broadcastPacket(new FinishRotatingPacket(activeChar, _degree, 0));
	}
}