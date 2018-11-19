package l2s.authserver;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import l2s.authserver.database.DatabaseFactory;
import l2s.authserver.network.gamecomm.GameServerCommunication;
import l2s.authserver.network.l2.L2LoginClient;
import l2s.authserver.network.l2.L2LoginPacketHandler;
import l2s.authserver.network.l2.SelectorHelper;
import l2s.commons.net.nio.impl.SelectorConfig;
import l2s.commons.net.nio.impl.SelectorStats;
import l2s.commons.net.nio.impl.SelectorThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthServer
{
	private static final Logger _log = LoggerFactory.getLogger(AuthServer.class);

	private static AuthServer authServer;

	private GameServerCommunication _gameServerListener;
	private SelectorThread<L2LoginClient> _selectorThread;

	public static AuthServer getInstance()
	{
		return authServer;
	}

	public AuthServer() throws Exception
	{
		Config.initCrypt();
		GameServerManager.getInstance();

		L2LoginPacketHandler lph = new L2LoginPacketHandler();
		SelectorHelper sh = new SelectorHelper();
		SelectorConfig sc = new SelectorConfig();
		sc.AUTH_TIMEOUT = Config.LOGIN_TIMEOUT;
		SelectorStats sts = new SelectorStats();
		_selectorThread = new SelectorThread<L2LoginClient>(sc, sts, lph, sh, sh, sh);

		_gameServerListener = GameServerCommunication.getInstance();
		_gameServerListener.openServerSocket(Config.GAME_SERVER_LOGIN_HOST.equals("*") ? null : InetAddress.getByName(Config.GAME_SERVER_LOGIN_HOST), Config.GAME_SERVER_LOGIN_PORT);
		_gameServerListener.start();
		_log.info("Listening for gameservers on " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT);

		_selectorThread.openServerSocket(Config.LOGIN_HOST.equals("*") ? null : InetAddress.getByName(Config.LOGIN_HOST), Config.PORT_LOGIN);
		_selectorThread.start();
		_log.info("Listening for clients on " + Config.LOGIN_HOST + ":" + Config.PORT_LOGIN);
	}

	public GameServerCommunication getGameServerListener()
	{
		return _gameServerListener;
	}

	public static void checkFreePorts() throws IOException
	{
		ServerSocket ss = null;

		try
		{
			if(Config.LOGIN_HOST.equalsIgnoreCase("*"))
				ss = new ServerSocket(Config.PORT_LOGIN);
			else
				ss = new ServerSocket(Config.PORT_LOGIN, 50, InetAddress.getByName(Config.LOGIN_HOST));
		}
		finally
		{
			if(ss != null)
				try
				{
					ss.close();
				}
				catch(Exception e)
				{}
		}
	}

	public static void main(String[] args) throws Exception
	{
		new File("./log/").mkdir();
		// Initialize config
		Config.load();
		// Check binding address
		checkFreePorts();
		// Initialize database
		Class.forName(Config.DATABASE_DRIVER).newInstance();
		DatabaseFactory.getInstance().getConnection().close();

		authServer = new AuthServer();
	}
}