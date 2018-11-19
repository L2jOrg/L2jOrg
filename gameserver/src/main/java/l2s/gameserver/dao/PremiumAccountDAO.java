package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 20:22/07.04.2011
 */
public class PremiumAccountDAO
{
	private static final Logger _log = LoggerFactory.getLogger(PremiumAccountDAO.class);
	private static final PremiumAccountDAO _instance = new PremiumAccountDAO();

	public static final String SELECT_SQL_QUERY = "SELECT type, expire_time FROM premium_accounts WHERE account=?";
	public static final String DELETE_SQL_QUERY = "DELETE FROM premium_accounts WHERE account=?";
	public static final String INSERT_SQL_QUERY = "REPLACE INTO premium_accounts(account, type, expire_time) VALUES (?,?,?)";

	public static PremiumAccountDAO getInstance()
	{
		return _instance;
	}

	public int[] select(String account)
	{
		int bonus = 1;
		int time = 0;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			statement.setString(1, account);
			rset = statement.executeQuery();
			if(rset.next())
			{
				bonus = rset.getInt("type");
				time = rset.getInt("expire_time");
			}
		}
		catch(Exception e)
		{
			_log.info("PremiumAccountDAO.select(String): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return new int[]{bonus, time};
	}

	public void delete(String account)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setString(1, account);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.info("PremiumAccountDAO.delete(String): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void insert(String account, int bonus, int endTime)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_SQL_QUERY);
			statement.setString(1, account);
			statement.setInt(2, bonus);
			statement.setInt(3, endTime);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.info("PremiumAccountDAO.insert(String, double, int): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}