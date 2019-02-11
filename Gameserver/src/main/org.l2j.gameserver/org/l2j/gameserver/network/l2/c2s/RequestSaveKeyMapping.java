package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExUISettingPacket;

import java.nio.ByteBuffer;

/**
 * format: (ch)db
 */
public class RequestSaveKeyMapping extends L2GameClientPacket
{
	private byte[] _data;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		int length = buffer.getInt();
		if(length > buffer.remaining() || length > Short.MAX_VALUE || length < 0)
		{
			_data = null;
			return;
		}
		_data = new byte[length];
		buffer.get(_data);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null || _data == null)
			return;
		activeChar.setKeyBindings(_data);
		activeChar.sendPacket(new ExUISettingPacket(activeChar));
	}
}