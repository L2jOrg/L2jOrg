package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.l2.s2c.Ex2NDPasswordAckPacket;
import org.l2j.gameserver.security.SecondaryPasswordAuth;

import java.nio.ByteBuffer;

/**
 * (ch)cS{S}
 * c: change pass?
 * S: current password
 * S: new password
 */
public class RequestEx2ndPasswordReq extends L2GameClientPacket
{
	private int _changePass;
	private String _password, _newPassword;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_changePass = buffer.get();
		_password = readString(buffer);
		if(_changePass == 2)
			_newPassword = readString(buffer);
	}

	@Override
	protected void runImpl()
	{
		if(!Config.EX_SECOND_AUTH_ENABLED)
			return;

		SecondaryPasswordAuth spa = client.getSecondaryAuth();
		boolean exVal = false;

		if(_changePass == 0 && !spa.passwordExist())
			exVal = spa.savePassword(_password);
		else if(_changePass == 2 && spa.passwordExist())
			exVal = spa.changePassword(_password, _newPassword);

		if(exVal)
			client.sendPacket(new Ex2NDPasswordAckPacket(Ex2NDPasswordAckPacket.SUCCESS));
	}
}
