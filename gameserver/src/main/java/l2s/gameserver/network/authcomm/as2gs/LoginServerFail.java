package l2s.gameserver.network.authcomm.as2gs;

import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.authcomm.ReceivablePacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServerFail extends ReceivablePacket
{
	private static final Logger _log = LoggerFactory.getLogger(LoginServerFail.class);

	private static final String[] REASONS = {
			"none",
			"IP banned",
			"IP reserved",
			"wrong hexid",
			"ID reserved",
			"no free ID",
			"not authed",
			"already logged in" };

	private String _reason;
	private boolean _restartConnection = true;

	@Override
	protected void readImpl()
	{
		int reasonId = readC();
		if(!getByteBuffer().hasRemaining())
			_reason = "Authserver registration failed! Reason: " + REASONS[reasonId];
		else
		{
			_reason = readS();
			_restartConnection = readC() > 0;
		}
	}

	protected void runImpl()
	{
		_log.warn(_reason);
		if(_restartConnection)
			AuthServerCommunication.getInstance().restart();
	}
}