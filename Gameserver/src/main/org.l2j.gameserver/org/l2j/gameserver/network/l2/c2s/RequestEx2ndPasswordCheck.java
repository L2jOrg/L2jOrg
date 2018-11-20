package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.l2.s2c.ActionFailPacket;
import org.l2j.gameserver.network.l2.s2c.Ex2NDPasswordCheckPacket;
import org.l2j.gameserver.security.SecondaryPasswordAuth;

/**
 * Format: (ch)
 */
public class RequestEx2ndPasswordCheck extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		SecondaryPasswordAuth spa = getClient().getSecondaryAuth();
		if(Config.EX_SECOND_AUTH_ENABLED && spa == null)
		{
			sendPacket(ActionFailPacket.STATIC);
			return;
		}
		if(!Config.EX_SECOND_AUTH_ENABLED || spa.isAuthed())
		{
			sendPacket(new Ex2NDPasswordCheckPacket(Ex2NDPasswordCheckPacket.PASSWORD_OK));
			return;
		}
		spa.openDialog();
	}
}
