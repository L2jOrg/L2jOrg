package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

import java.nio.ByteBuffer;

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
	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		buffer.put((byte)0x08);
		writeString(_account, buffer);
		writeString(_oldPass, buffer);
		writeString(_newPass, buffer);
		writeString(_hwid, buffer);
	}
}
