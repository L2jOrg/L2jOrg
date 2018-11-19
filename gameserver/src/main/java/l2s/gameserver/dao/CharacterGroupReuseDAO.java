package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import l2s.gameserver.skills.TimeStamp;
import l2s.gameserver.utils.SqlBatch;

import org.napile.pair.primitive.IntObjectPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 11:41/28.03.2011
 */
public class CharacterGroupReuseDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CharacterGroupReuseDAO.class);
	private static CharacterGroupReuseDAO _instance = new CharacterGroupReuseDAO();
	public static final String DELETE_SQL_QUERY = "DELETE FROM character_group_reuse WHERE object_id=?";
	public static final String SELECT_SQL_QUERY = "SELECT * FROM character_group_reuse WHERE object_id=?";
	public static final String INSERT_SQL_QUERY = "REPLACE INTO `character_group_reuse` (`object_id`,`reuse_group`,`item_id`,`end_time`,`reuse`) VALUES";

	public static CharacterGroupReuseDAO getInstance()
	{
		return _instance;
	}

	public void select(Player player)
	{
		long curTime = System.currentTimeMillis();

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
			{
				int group = rset.getInt("reuse_group");
				int item_id = rset.getInt("item_id");
				long endTime = rset.getLong("end_time");
				long reuse = rset.getLong("reuse");

				if(endTime - curTime > 500)
				{
					TimeStamp stamp = new TimeStamp(item_id, endTime, reuse);
					player.addSharedGroupReuse(group, stamp);
				}
			}
			DbUtils.close(statement);

			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setInt(1, player.getObjectId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("CharacterGroupReuseDAO.select(L2Player):", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void insert(Player player)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setInt(1, player.getObjectId());
			statement.execute();

			Collection<IntObjectPair<TimeStamp>> reuses = player.getSharedGroupReuses();
			if(reuses.isEmpty())
				return;

			SqlBatch b = new SqlBatch(INSERT_SQL_QUERY);
			synchronized (reuses)
			{
				for(IntObjectPair<TimeStamp> entry : reuses)
				{
					int group = entry.getKey();
					TimeStamp timeStamp = entry.getValue();
					if(timeStamp.hasNotPassed())
					{
						StringBuilder sb = new StringBuilder("(");
						sb.append(player.getObjectId()).append(",");
						sb.append(group).append(",");
						sb.append(timeStamp.getId()).append(",");
						sb.append(timeStamp.getEndTime()).append(",");
						sb.append(timeStamp.getReuseBasic()).append(")");
						b.write(sb.toString());
					}
				}
			}
			if(!b.isEmpty())
				statement.executeUpdate(b.close());
		}
		catch(final Exception e)
		{
			_log.error("CharacterGroupReuseDAO.insert(L2Player):", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}