package org.l2j.gameserver.network.authcomm.as2gs;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.network.l2.s2c.ExShowScreenMessage;
import org.l2j.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;

import java.nio.ByteBuffer;

/**
 * @author Death
 */
public class ChangePasswordResponse extends ReceivablePacket
{
	private String _account;
	private boolean _changed;
	
	@Override
	protected void readImpl(ByteBuffer buffer) {
		_account = readString(buffer);
		_changed = buffer.getInt() == 1;
	}
	
	@Override
	protected void runImpl()
	{
		GameClient client = AuthServerCommunication.getInstance().getAuthedClient(_account);
		if(client == null)
			return;

		Player activeChar = client.getActiveChar();

		if(activeChar == null)
			return;

		if(_changed)
			activeChar.sendPacket(new ExShowScreenMessage(new CustomMessage("scripts.commands.user.password.ResultTrue").toString(activeChar), 3000, ScreenMessageAlign.BOTTOM_CENTER, true));
		else
			activeChar.sendPacket(new ExShowScreenMessage(new CustomMessage("scripts.commands.user.password.ResultFalse").toString(activeChar), 3000, ScreenMessageAlign.BOTTOM_CENTER, true));
	}	
}