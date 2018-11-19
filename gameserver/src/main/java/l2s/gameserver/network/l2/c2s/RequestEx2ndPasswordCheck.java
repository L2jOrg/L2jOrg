package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.network.l2.s2c.ActionFailPacket;
import l2s.gameserver.network.l2.s2c.Ex2NDPasswordCheckPacket;
import l2s.gameserver.security.SecondaryPasswordAuth;

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
