package l2s.authserver.network.gamecomm.gs2as;

import java.util.ArrayList;
import java.util.List;

import l2s.authserver.GameServerManager;
import l2s.authserver.network.gamecomm.GameServer;
import l2s.authserver.network.gamecomm.GameServer.HostInfo;
import l2s.authserver.network.gamecomm.ReceivablePacket;
import l2s.authserver.network.gamecomm.as2gs.AuthResponse;
import l2s.authserver.network.gamecomm.as2gs.LoginServerFail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @reworked by Bonux
**/
public class AuthRequest extends ReceivablePacket
{
	private final static Logger _log = LoggerFactory.getLogger(AuthRequest.class);

	private int _protocolVersion;
	private List<HostInfo> _hosts;
	private int _serverType;
	private int _ageLimit;
	private boolean _gmOnly;
	private boolean _brackets;
	private boolean _pvp;
	private int _maxOnline;

	@Override
	protected void readImpl()
	{
		_protocolVersion = readD();
		readC(); // requestId
		readC(); // acceptAlternateID
		_serverType = readD();
		_ageLimit = readD();
		_gmOnly = readC() == 1;
		_brackets = readC() == 1;
		_pvp = readC() == 1;
		readS(); // external IP
		readS(); // internal IP

		int ports = readH();
		for(int i = 0; i < ports; i++)
			readH(); // port

		_maxOnline = readD();

		int hostsCount = readC();
		_hosts = new ArrayList<HostInfo>(hostsCount);
		for(int i = 0; i < hostsCount; i++)
		{
			int id = readC();
			String ip = readS();
			String innerIP = readS();
			int port = readH();
			String key = readS();
			_hosts.add(new HostInfo(id, ip, innerIP, port, key));
		}
	}

	@Override
	protected void runImpl()
	{
		_log.info("Trying to register gameserver: IP[" + getGameServer().getConnection().getIpAddress() + "]");

		GameServer gs = getGameServer();
		for(HostInfo host : _hosts)
		{
			if(GameServerManager.getInstance().registerGameServer(host.getId(), gs))
				gs.addHost(host);
			else
			{
				sendPacket(new LoginServerFail("Gameserver registration on ID[" + host.getId() + "] failed. ID[" + host.getId() + "] is already in use!", false));
				sendPacket(new LoginServerFail("Free ID[" + host.getId() + "] or change to another ID, and restart your authserver or gameserver!", false));
			}
		}

		if(gs.getHosts().length > 0)
		{
			gs.setProtocol(_protocolVersion);
			gs.setServerType(_serverType);
			gs.setAgeLimit(_ageLimit);
			gs.setGmOnly(_gmOnly);
			gs.setShowingBrackets(_brackets);
			gs.setPvp(_pvp);
			gs.setMaxPlayers(_maxOnline);

			gs.setAuthed(true);
			gs.getConnection().startPingTask();
		}
		else
		{
			sendPacket(new LoginServerFail("Gameserver registration failed. All ID's is already in use!", true));
			_log.info("Gameserver registration failed.");
			return;
		}

		_log.info("Gameserver registration successful.");
		sendPacket(new AuthResponse(gs));
	}
}
