package l2s.gameserver.network.authcomm.as2gs;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.authcomm.ReceivablePacket;
import l2s.gameserver.network.authcomm.gs2as.OnlineStatus;
import l2s.gameserver.network.authcomm.gs2as.PlayerInGame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @reworked by Bonux
**/
public class AuthResponse extends ReceivablePacket
{
	private static class ServerInfo
	{
		private final int _id;
		private final String _name;

		public ServerInfo(int id, String name)
		{
			_id = id;
			_name = name;
		}

		public int getId()
		{
			return _id;
		}

		public String getName()
		{
			return _name;
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(AuthResponse.class);

	private List<ServerInfo> _servers;

	@Override
	protected void readImpl()
	{
		int serverId = readC();
		String serverName = readS();
		if(!getByteBuffer().hasRemaining())
		{
			_servers = new ArrayList<ServerInfo>(1);
			_servers.add(new ServerInfo(serverId, serverName));
		}
		else
		{
			int serversCount = readC();
			_servers = new ArrayList<ServerInfo>(serversCount);
			for(int i = 0; i < serversCount; i++)
				_servers.add(new ServerInfo(readC(), readS()));
		}
	}

	@Override
	protected void runImpl()
	{
		for(ServerInfo info : _servers)
			_log.info("Registered on authserver as " + info.getId() + " [" + info.getName() + "]");

		sendPacket(new OnlineStatus(true));

		String[] accounts = AuthServerCommunication.getInstance().getAccounts();
		for(String account : accounts)
			sendPacket(new PlayerInGame(account));
	}
}
