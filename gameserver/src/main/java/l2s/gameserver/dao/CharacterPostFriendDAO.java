package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 21:53/22.03.2011
 */
public class CharacterPostFriendDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CharacterPostFriendDAO.class);
	private static final CharacterPostFriendDAO _instance = new CharacterPostFriendDAO();

	private static final String SELECT_SQL_QUERY = "SELECT pf.post_friend, c.char_name FROM character_post_friends pf LEFT JOIN characters c ON pf.post_friend = c.obj_Id WHERE pf.object_id = ?";
	private static final String INSERT_SQL_QUERY = "INSERT INTO character_post_friends(object_id, post_friend) VALUES (?,?)";
	private static final String DELETE_SQL_QUERY = "DELETE FROM character_post_friends WHERE object_id=? AND post_friend=?";

	public static CharacterPostFriendDAO getInstance()
	{
		return _instance;
	}

	public IntObjectMap<String> select(Player player)
	{
		IntObjectMap<String> set = new CHashIntObjectMap<String>();
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			statement.setInt(1, player.getObjectId());
			rset = statement.executeQuery();
			while(rset.next())
				set.put(rset.getInt(1), rset.getString(2) == null ? "" : rset.getString(2));
		}
		catch(Exception e)
		{
			_log.error("CharacterPostFriendDAO.load(L2Player): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return set;
	}

	public void insert(Player player, int val)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_SQL_QUERY);
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, val);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("CharacterPostFriendDAO.insert(L2Player, int): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void delete(Player player, int val)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, val);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("CharacterPostFriendDAO.delete(L2Player, int): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}