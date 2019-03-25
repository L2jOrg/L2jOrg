package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 * @date 21:07/25.03.2011
 */
public class SetAccountInfo extends SendablePacket
{
	private String _account;
	private int _size;

	public SetAccountInfo(String account, int size)
	{
		_account = account;
		_size = size;
	}

	@Override
	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		buffer.put((byte)0x05);
		writeString(_account, buffer);
		buffer.put((byte)_size);
	}
}
