package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 */
public class AccountVariablesDAO
{
	private static final Logger _log = LoggerFactory.getLogger(AccountVariablesDAO.class);
	private static final AccountVariablesDAO _instance = new AccountVariablesDAO();

	public static final String SELECT_SQL_QUERY = "SELECT value FROM account_variables WHERE account_name=? AND var=?";
	public static final String DELETE_SQL_QUERY = "DELETE FROM account_variables WHERE account=? AND var=?";
	public static final String DELETE_ALL_SQL_QUERY = "DELETE FROM account_variables WHERE var=?";
	public static final String INSERT_SQL_QUERY = "INSERT INTO account_variables VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE value=?";

	public static AccountVariablesDAO getInstance()
	{
		return _instance;
	}

	public String select(String account, String var)
	{
		return select(account, var, null);
	}

	public String select(String account, String var, String defaultVal)
	{
		String result_value = defaultVal;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			statement.setString(1, account);
			statement.setString(2, var);
			rset = statement.executeQuery();
			if(rset.next())
				result_value = rset.getString("value");
		}
		catch(Exception e)
		{
			_log.info("AccountVariablesDAO.select(String, String): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return result_value;
	}

	public void delete(String account, String var)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setString(1, account);
			statement.setString(2, var);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.info("AccountVariablesDAO.delete(String, String): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void delete(String var)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_ALL_SQL_QUERY);
			statement.setString(1, var);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.info("AccountVariablesDAO.delete(String): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void insert(String account, String var, String value)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_SQL_QUERY);
			statement.setString(1, account);
			statement.setString(2, var);
			statement.setString(3, value);
			statement.setString(4, value);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.info("AccountVariablesDAO.insert(String, String, String): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}