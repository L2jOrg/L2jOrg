package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.actor.instances.player.CharacterVariable;
import l2s.gameserver.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 */
public class CharacterVariablesDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CharacterVariablesDAO.class);
	private static final CharacterVariablesDAO _instance = new CharacterVariablesDAO();

	public static final String SELECT_SQL_QUERY = "SELECT name, value, expire_time FROM character_variables WHERE obj_id = ?";
	public static final String SELECT_FROM_PLAYER_SQL_QUERY = "SELECT value, expire_time FROM character_variables WHERE obj_id = ? AND name = ?";
	public static final String DELETE_SQL_QUERY = "DELETE FROM character_variables WHERE obj_id = ? AND name = ? LIMIT 1";
	public static final String DELETE_EXPIRED_SQL_QUERY = "DELETE FROM character_variables WHERE expire_time > 0 AND expire_time < ?";
	public static final String INSERT_SQL_QUERY = "REPLACE INTO character_variables (obj_id, name, value, expire_time) VALUES (?,?,?,?)";

	public CharacterVariablesDAO()
	{
		deleteExpiredVars();
	}

	public static CharacterVariablesDAO getInstance()
	{
		return _instance;
	}

	private void deleteExpiredVars()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_EXPIRED_SQL_QUERY);
			statement.setLong(1, System.currentTimeMillis());
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.error("CharacterVariablesDAO:deleteExpiredVars()", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public boolean delete(int playerObjId, String varName)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setInt(1, playerObjId);
			statement.setString(2, varName);
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.error("CharacterVariablesDAO:delete(playerObjId,varName)", e);
			return false;
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		return true;
	}

	public boolean insert(int playerObjId, CharacterVariable var)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_SQL_QUERY);
			statement.setInt(1, playerObjId);
			statement.setString(2, var.getName());
			statement.setString(3, var.getValue());
			statement.setLong(4, var.getExpireTime());
			statement.executeUpdate();
		}
		catch(final Exception e)
		{
			_log.error("CharacterVariablesDAO:insert(playerObjId,var)", e);
			return false;
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		return true;
	}

	public List<CharacterVariable> restore(int playerObjId)
	{
		List<CharacterVariable> result = new ArrayList<CharacterVariable>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			statement.setInt(1, playerObjId);
			rset = statement.executeQuery();
			while(rset.next())
			{
				long expireTime = rset.getLong("expire_time");
				if(expireTime > 0 && expireTime < System.currentTimeMillis())
					continue;

				result.add(new CharacterVariable(rset.getString("name"), Strings.stripSlashes(rset.getString("value")), expireTime));
			}
		}
		catch(Exception e)
		{
			_log.error("CharacterVariablesDAO:restore(playerObjId)", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return result;
	}

	public String getVarFromPlayer(int playerObjId, String var)
	{
		String value = null;

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_FROM_PLAYER_SQL_QUERY);
			statement.setInt(1, playerObjId);
			statement.setString(2, var);
			rset = statement.executeQuery();
			if(rset.next())
			{
				long expireTime = rset.getLong("expire_time");
				if(expireTime <= 0 || expireTime >= System.currentTimeMillis())
					value = Strings.stripSlashes(rset.getString("value"));
			}
		}
		catch(Exception e)
		{
			_log.error("CharacterVariablesDAO:getVarFromPlayer(playerObjId,var)", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return value;
	}
}