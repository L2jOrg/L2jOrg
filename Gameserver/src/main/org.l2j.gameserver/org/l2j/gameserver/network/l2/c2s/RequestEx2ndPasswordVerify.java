package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;

import java.nio.ByteBuffer;

public class RequestEx2ndPasswordVerify extends L2GameClientPacket
{
	private String _password;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_password = readString(buffer);
	}

	@Override
	protected void runImpl()
	{
		if(!Config.EX_SECOND_AUTH_ENABLED)
			return;

		client.getSecondaryAuth().checkPassword(_password, false);
	}
}
