package l2s.authserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import l2s.authserver.database.DatabaseFactory;
import l2s.authserver.network.gamecomm.GameServer;
import l2s.commons.dbutils.DbUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerManager
{
	private static Logger _log = LoggerFactory.getLogger(GameServerManager.class);

	private static final GameServerManager _instance = new GameServerManager();

	public static final GameServerManager getInstance()
	{
		return _instance;
	}

	private final Map<Integer, GameServer> _gameServers = new TreeMap<Integer, GameServer>();
	private final ReadWriteLock _lock = new ReentrantReadWriteLock();
	private final Lock _readLock = _lock.readLock();
	private final Lock _writeLock = _lock.writeLock();

	public GameServerManager()
	{
		load();
		_log.info("Loaded " + _gameServers.size() + " registered GameServer(s).");
	}

	private void load()
	{
		/*Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT server_id FROM gameservers");
			rset = statement.executeQuery();

			int id;

			while(rset.next())
			{
				id = rset.getInt("server_id");

				GameServer gs = new GameServer(id);

				_gameServers.put(id, gs);
			}
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}*/
	}

	/**
	 * Поулчить массив всех зарегистрированных игровых серверов
	 * 
	 * @return массив всех игровых серверов
	 */
	public GameServer[] getGameServers()
	{
		_readLock.lock();
		try
		{
			Set<GameServer> gameservers = new HashSet<GameServer>(_gameServers.values());
			return gameservers.toArray(new GameServer[gameservers.size()]);
		}
		finally
		{
			_readLock.unlock();
		}
	}

	/**
	 * Получить зарегистрированный игровой сервер по идентификатору
	 * 
	 * @param id идентификатор игрового сервера
	 * @return игровой сервер
	 */
	public GameServer getGameServerById(int id)
	{
		_readLock.lock();
		try
		{
			return _gameServers.get(id);
		}
		finally
		{
			_readLock.unlock();
		}
	}

	/**
	 * Регистрация игрового сервера на требуемый идентификатор
	 * 
	 * @param id требуемый идентификатор игрового сервера
	 * @param gs игровой сервер
	 * @return true если игрвоой сервер успешно зарегистрирован
	 */
	public boolean registerGameServer(int id, GameServer gs)
	{
		_writeLock.lock();
		try
		{
			GameServer pgs = _gameServers.get(id);
			if(!Config.ACCEPT_NEW_GAMESERVER && pgs == null)
				return false;
			
			if(pgs == null || !pgs.isAuthed())
			{
				if(pgs != null)
					pgs.removeHost(id);

				_gameServers.put(id, gs);
				return true;
			}
		}
		finally
		{
			_writeLock.unlock();
		}
		return false;
	}
}
