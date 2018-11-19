package l2s.gameserver.network.authcomm.gs2as;

import l2s.gameserver.network.authcomm.SendablePacket;

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
		writeC(0x0c);
		writeS(_account);
		writeQ(_phoneNumber);
	}
}