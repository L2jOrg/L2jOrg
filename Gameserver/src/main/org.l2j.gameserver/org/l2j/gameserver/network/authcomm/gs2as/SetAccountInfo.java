package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.SendablePacket;

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
	protected void writeImpl()
	{
		writeByte(0x05);
		writeString(_account);
		writeByte(_size);
		writeInt(_deleteChars.length);
		for(int i : _deleteChars)
			writeInt(i);
	}
}
