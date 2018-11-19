package l2s.authserver.network.gamecomm.gs2as;

import l2s.authserver.network.gamecomm.GameServer;
import l2s.authserver.network.gamecomm.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingResponse extends ReceivablePacket
{
	private static final Logger _log = LoggerFactory.getLogger(PingResponse.class);

	private long _serverTime;

	@Override
	protected void readImpl()
	{
		_serverTime = readQ();
	}

	@Override
	protected void runImpl()
	{
		GameServer gameServer = getGameServer();
		if(!gameServer.isAuthed())
			return;

		gameServer.getConnection().onPingResponse();

		long diff = System.currentTimeMillis() - _serverTime;

		if(Math.abs(diff) > 999)
			_log.warn("Gameserver IP[" + gameServer.getConnection().getIpAddress() + "]: time offset " + diff + " ms.");
	}
}