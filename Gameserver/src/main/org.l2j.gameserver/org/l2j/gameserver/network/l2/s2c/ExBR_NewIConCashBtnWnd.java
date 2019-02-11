package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class ExBR_NewIConCashBtnWnd extends L2GameServerPacket {

	private final int _value;

	public ExBR_NewIConCashBtnWnd(int value)
	{
		_value = value;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putShort((short) _value);	// Has Updates
	}
}
