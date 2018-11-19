package l2s.authserver.network.gamecomm.as2gs;

import l2s.authserver.Config;
import l2s.authserver.network.gamecomm.GameServer;
import l2s.authserver.network.gamecomm.GameServer.HostInfo;
import l2s.authserver.network.gamecomm.SendablePacket;

/**
 * @reworked by Bonux
**/
public class AuthResponse extends SendablePacket
{
	private HostInfo[] _hosts;

	public AuthResponse(GameServer gs)
	{
		_hosts = gs.getHosts();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x00);
		writeC(0x00); // ServerId
		writeS(""); // ServerName
		writeC(_hosts.length);
		for(HostInfo host : _hosts)
		{
			writeC(host.getId());
			writeS(Config.SERVER_NAMES.get(host.getId()));
		}
	}
}