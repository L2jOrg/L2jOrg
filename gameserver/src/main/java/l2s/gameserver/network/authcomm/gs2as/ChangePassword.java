package l2s.gameserver.network.authcomm.gs2as;

import l2s.gameserver.network.authcomm.SendablePacket;
/**
 * @Author: Death
 * @Date: 8/2/2007
 * @Time: 14:35:35
 */
public class ChangePassword extends SendablePacket
{
	public String _account;
	public String _oldPass;
	public String _newPass;
	public String _hwid;

	public ChangePassword(String account, String oldPass, String newPass, String hwid)
	{
		_account = account;
		_oldPass = oldPass;
		_newPass = newPass;
		_hwid = hwid;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x08);
		writeS(_account);
		writeS(_oldPass);
		writeS(_newPass);
		writeS(_hwid);
	}
}
