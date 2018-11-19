package l2s.authserver.network.gamecomm.as2gs;

import l2s.authserver.network.gamecomm.SendablePacket;

/**
 * @reworked by Bonux
**/
public class LoginServerFail extends SendablePacket
{
	private final String _reason;
	private final boolean _restartConnection;

	public LoginServerFail(String reason, boolean restartConnection)
	{
		_reason = reason;
		_restartConnection = restartConnection;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x01);
		writeC(0x00); // Reason ID
		writeS(_reason);
		writeC(_restartConnection ? 0x01 : 0x00);
	}
}