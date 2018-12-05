package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.SendablePacket;

public class ChangePhoneNumber extends SendablePacket
{
	private final String _account;
	private final long _phoneNumber;

	public ChangePhoneNumber(String account, long phoneNumber)
	{
		_account = account;
		_phoneNumber = phoneNumber;
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0x0c);
		writeString(_account);
		writeLong(_phoneNumber);
	}
}