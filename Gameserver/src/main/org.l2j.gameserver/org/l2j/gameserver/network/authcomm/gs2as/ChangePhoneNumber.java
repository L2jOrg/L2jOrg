package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

import java.nio.ByteBuffer;

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
	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		buffer.put((byte)0x0c);
		writeString(_account, buffer);
		buffer.putLong(_phoneNumber);
	}
}