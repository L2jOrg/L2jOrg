package l2s.gameserver.network.authcomm.as2gs;

import l2s.gameserver.network.authcomm.ReceivablePacket;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;

/**
 * @Author: Death
 * @Date: 8/2/2007
 * @Time: 14:39:46
 */
public class ChangePasswordResponse extends ReceivablePacket
{
	public String _account;
	public boolean _changed;
	
	@Override
	protected void readImpl()
	{
		_account = readS();
		_changed = readD() == 1;
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