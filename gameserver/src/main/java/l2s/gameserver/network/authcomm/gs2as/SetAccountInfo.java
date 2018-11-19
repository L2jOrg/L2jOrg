package l2s.gameserver.network.authcomm.gs2as;

import l2s.gameserver.network.authcomm.SendablePacket;

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
		writeC(0x05);
		writeS(_account);
		writeC(_size);
		writeD(_deleteChars.length);
		for(int i : _deleteChars)
			writeD(i);
	}
}
