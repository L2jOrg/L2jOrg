package l2s.gameserver.database;

import java.sql.Connection;
import java.sql.SQLException;

import l2s.commons.dbcp.BasicDataSource;
import l2s.gameserver.Config;

public class DatabaseFactory extends BasicDataSource
{
	private static final DatabaseFactory _instance = new DatabaseFactory();

	public static final DatabaseFactory getInstance() throws SQLException
	{
		return _instance;
	}

	public DatabaseFactory()
	{
		super(Config.DATABASE_DRIVER, Config.DATABASE_URL, Config.DATABASE_LOGIN, Config.DATABASE_PASSWORD, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_IDLE_TIMEOUT, Config.DATABASE_IDLE_TEST_PERIOD, false);
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		return getConnection(null);
	}
}