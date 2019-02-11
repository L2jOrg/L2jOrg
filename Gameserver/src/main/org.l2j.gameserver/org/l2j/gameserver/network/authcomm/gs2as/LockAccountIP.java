package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

import java.nio.ByteBuffer;

/**
 * @Author: SYS -> changed by Iqman 02.02.2012 12:12
 * @Date: 10/4/2008
 */
public class LockAccountIP extends SendablePacket
{
	String _account;
	String _IP;
	int _time;

	public LockAccountIP(String account, String IP, int time)
	{
		_account = account;
		_IP = IP;
		_time = time;
	}

	@Override
	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		buffer.put((byte)0x0b);
		writeString(_account, buffer);
		writeString(_IP, buffer);
		buffer.putInt(_time);
	}
}