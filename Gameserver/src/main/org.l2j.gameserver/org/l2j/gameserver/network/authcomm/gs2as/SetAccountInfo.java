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
	private int[] _deleteChars;

	public SetAccountInfo(String account, int size, int[] deleteChars)
	{
		_account = account;
		_size = size;
		_deleteChars = deleteChars;
	}

	@Override
	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		buffer.put((byte)0x05);
		writeString(_account, buffer);
		buffer.put((byte)_size);
		buffer.putInt(_deleteChars.length);
		for(int i : _deleteChars)
			buffer.putInt(i);
	}
}
