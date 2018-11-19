package l2s.authserver.network.gamecomm;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @reworked by Bonux
**/
public class GameServer
{
	public static class HostInfo
	{
		private final int _id;
		private final String _ip;
		private final String _innerIP;
		private final int _port;
		private final String _key;

		public HostInfo(int id, String ip, String innerIP, int port, String key)
		{
			_id = id;
			_ip = ip;
			_innerIP = innerIP;
			_port = port;
			_key = key;
		}

		public int getId()
		{
			return _id;
		}

		public String getIP()
		{
			return _ip;
		}

		public String getInnerIP()
		{
			return _innerIP;
		}

		public int getPort()
		{
			return _port;
		}

		public String getKey()
		{
			return _key;
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(GameServer.class);

	private final TIntObjectMap<HostInfo> _hosts = new TIntObjectHashMap<HostInfo>();
	private int _serverType;
	private int _ageLimit;
	private int _protocol;
	private boolean _isOnline;
	private boolean _isPvp;
	private boolean _isShowingBrackets;
	private boolean _isGmOnly;

	private int _maxPlayers;

	private GameServerConnection _conn;
	private boolean _isAuthed;

	private Set<String> _accounts = new CopyOnWriteArraySet<String>();

	public GameServer(GameServerConnection conn)
	{
		_conn = conn;
	}

	public void addHost(HostInfo host)
	{
		_hosts.put(host.getId(), host);
	}

	public HostInfo removeHost(int id)
	{
		return _hosts.remove(id);
	}

	public HostInfo[] getHosts()
	{
		return _hosts.values(new HostInfo[_hosts.size()]);
	}

	public void setAuthed(boolean isAuthed)
	{
		_isAuthed = isAuthed;
	}

	public boolean isAuthed()
	{
		return _isAuthed;
	}

	public void setConnection(GameServerConnection conn)
	{
		_conn = conn;
	}

	public GameServerConnection getConnection()
	{
		return _conn;
	}

	public void setMaxPlayers(int maxPlayers)
	{
		_maxPlayers = maxPlayers;
	}

	public int getMaxPlayers()
	{
		return _maxPlayers;
	}

	public int getOnline()
	{
		return _accounts.size();
	}

	public Set<String> getAccounts()
	{
		return _accounts;
	}

	public void addAccount(String account)
	{
		_accounts.add(account);
	}

	public void removeAccount(String account)
	{
		_accounts.remove(account);
	}

	public void setDown()
	{
		setAuthed(false);
		setConnection(null);
		setOnline(false);

		_accounts.clear();
	}

	public void sendPacket(SendablePacket packet)
	{
		GameServerConnection conn = getConnection();
		if(conn != null)
			conn.sendPacket(packet);
	}

	public int getServerType()
	{
		return _serverType;
	}

	public boolean isOnline()
	{
		return _isOnline;
	}

	public void setOnline(boolean online)
	{
		_isOnline = online;
	}

	public void setServerType(int serverType)
	{
		_serverType = serverType;
	}

	public boolean isPvp()
	{
		return _isPvp;
	}

	public void setPvp(boolean pvp)
	{
		_isPvp = pvp;
	}

	public boolean isShowingBrackets()
	{
		return _isShowingBrackets;
	}

	public void setShowingBrackets(boolean showingBrackets)
	{
		_isShowingBrackets = showingBrackets;
	}

	public boolean isGmOnly()
	{
		return _isGmOnly;
	}

	public void setGmOnly(boolean gmOnly)
	{
		_isGmOnly = gmOnly;
	}

	public int getAgeLimit()
	{
		return _ageLimit;
	}

	public void setAgeLimit(int ageLimit)
	{
		_ageLimit = ageLimit;
	}

	public int getProtocol()
	{
		return _protocol;
	}

	public void setProtocol(int protocol)
	{
		_protocol = protocol;
	}
}