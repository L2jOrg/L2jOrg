package l2s.authserver.network.gamecomm.as2gs;

import l2s.authserver.network.gamecomm.SendablePacket;

public class ChangePasswordResponse extends SendablePacket
{
	public String _account;
	public boolean _hasChanged;

	public ChangePasswordResponse(String account, boolean hasChanged)
	{
		_account = account;
		_hasChanged = hasChanged;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x06);
		writeS(_account);
		writeD(_hasChanged ? 1 : 0);
	}
}
