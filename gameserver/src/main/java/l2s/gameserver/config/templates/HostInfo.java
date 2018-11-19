package l2s.gameserver.config.templates;

/**
 * @author Bonux
**/
public class HostInfo
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

	public HostInfo(String ip, int port)
	{
		_id = 0;
		_ip = ip;
		_innerIP = null;
		_port = port;
		_key = null;
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