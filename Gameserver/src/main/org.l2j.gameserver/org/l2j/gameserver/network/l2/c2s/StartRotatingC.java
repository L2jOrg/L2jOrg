package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.StartRotatingPacket;

import java.nio.ByteBuffer;

/**
 * packet type id 0x5b
 * format:		cdd
 */
public class StartRotatingC extends L2GameClientPacket
{
	private int _degree;
	private int _side;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_degree = buffer.getInt();
		_side = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;
		activeChar.setHeading(_degree);
		activeChar.broadcastPacket(new StartRotatingPacket(activeChar, _degree, _side, 0));
	}
}