package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.pledge.ClanChangeLeaderRequest;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 13:56/13.04.2012
 */
public class ClanLeaderRequestDAO
{
	private static final Logger _log = LoggerFactory.getLogger(ClanHallDAO.class);
	private static final ClanLeaderRequestDAO _instance = new ClanLeaderRequestDAO();

	private static final String SELECT_SQL = "SELECT * FROM clan_leader_request";
	private static final String INSERT_SQL = "INSERT INTO  clan_leader_request(clan_id, new_leader_id, time) VALUES (?,?,?)";
	private static final String DELETE_SQL = "DELETE  FROM clan_leader_request WHERE clan_id=?";

	public static ClanLeaderRequestDAO getInstance()
	{
		return _instance;
	}

	public IntObjectMap<ClanChangeLeaderRequest> select()
	{
		IntObjectMap<ClanChangeLeaderRequest> requestList = new HashIntObjectMap<ClanChangeLeaderRequest>();

		Connection con = null;
		Statement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			rset = statement.executeQuery(SELECT_SQL);
			while(rset.next())
			{
				int clanId = rset.getInt("clan_id");

				requestList.put(clanId, new ClanChangeLeaderRequest(clanId, rset.getInt("new_leader_id"), rset.getLong("time") * 1000L));
			}
		}
		catch(Exception e)
		{
			_log.error("ClanLeaderRequestDAO.select(): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return requestList;
	}

	public void delete(ClanChangeLeaderRequest changeLeaderRequest)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL);
			statement.setInt(1, changeLeaderRequest.getClanId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("ClanLeaderRequestDAO.delete(ClanChangeLeaderRequest): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void insert(ClanChangeLeaderRequest request)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_SQL);
			statement.setInt(1, request.getClanId());
			statement.setInt(2, request.getNewLeaderId());
			statement.setLong(3, request.getTime() / 1000L);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("ClanLeaderRequestDAO.insert(ClanChangeLeaderRequest): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}