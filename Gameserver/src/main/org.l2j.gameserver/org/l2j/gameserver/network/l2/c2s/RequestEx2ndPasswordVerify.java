package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;

public class RequestEx2ndPasswordVerify extends L2GameClientPacket
{
	private String _password;

	@Override
	protected void readImpl()
	{
		_password = readS();
	}

	@Override
	protected void runImpl()
	{
		if(!Config.EX_SECOND_AUTH_ENABLED)
			return;

		getClient().getSecondaryAuth().checkPassword(_password, false);
	}
}
