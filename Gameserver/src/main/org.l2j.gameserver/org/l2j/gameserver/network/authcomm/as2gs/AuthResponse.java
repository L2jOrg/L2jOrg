package org.l2j.gameserver.network.authcomm.as2gs;

import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerInGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
		int serverId = readByte();
		String serverName = readString();
		if(availableData() <= 0) {
			_servers = new ArrayList<>(1);
			_servers.add(new ServerInfo(serverId, serverName));
		} else {
			int serversCount = readByte();
			_servers = new ArrayList<>(serversCount);
			for(int i = 0; i < serversCount; i++)
				_servers.add(new ServerInfo(readByte(), readString()));
		}
	}

	@Override
	protected void runImpl()
	{
		for(ServerInfo info : _servers)
			_log.info("Registered on authserver as " + info.getId() + " [" + info.getName() + "]");

		String[] accounts = AuthServerCommunication.getInstance().getAccounts();
		for(String account : accounts)
			sendPacket(new PlayerInGame(account));
	}
}
