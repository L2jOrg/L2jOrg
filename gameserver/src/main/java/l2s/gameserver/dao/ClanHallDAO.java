package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dao.JdbcEntityState;
import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.entity.residence.ClanHall;
import l2s.gameserver.tables.ClanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 19:19/15.04.2011
 */
public class ClanHallDAO
{
	private static final Logger _log = LoggerFactory.getLogger(ClanHallDAO.class);
	private static final ClanHallDAO _instance = new ClanHallDAO();

	public static final String SELECT_SQL_QUERY = "SELECT siege_date, owner_id, own_date, last_siege_date, auction_desc, auction_length, auction_min_bid, cycle, paid_cycle FROM clanhall WHERE id = ?";
	public static final String REPLACE_SQL_QUERY = "REPLACE INTO clanhall (id, name, last_siege_date, owner_id, own_date, siege_date, auction_min_bid, auction_length, auction_desc, cycle, paid_cycle) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

	public static ClanHallDAO getInstance()
	{
		return _instance;
	}

	public void select(ClanHall clanHall)
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			statement.setInt(1, clanHall.getId());
			rset = statement.executeQuery();
			if(rset.next())
			{
				clanHall.getSiegeDate().setTimeInMillis(rset.getLong("siege_date") * 1000L);
				clanHall.getLastSiegeDate().setTimeInMillis(rset.getLong("last_siege_date") * 1000L);
				clanHall.setOwner(ClanTable.getInstance().getClan(rset.getInt("owner_id")));
				clanHall.getOwnDate().setTimeInMillis(rset.getLong("own_date") * 1000L);
				//
				clanHall.setAuctionLength(rset.getInt("auction_length"));
				clanHall.setAuctionMinBid(rset.getLong("auction_min_bid"));
				clanHall.setAuctionDescription(rset.getString("auction_desc"));
				//
				clanHall.setCycle(rset.getInt("cycle"));
				clanHall.setPaidCycle(rset.getInt("paid_cycle"));
			}
		}
		catch(Exception e)
		{
			_log.error("ClanHallDAO.select(ClanHall):" + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void update(ClanHall c)
	{
		if(!c.getJdbcState().isUpdatable())
			return;

		c.setJdbcState(JdbcEntityState.STORED);
		update0(c);
	}

	private void update0(ClanHall c)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(REPLACE_SQL_QUERY);

			int i = 0;
			statement.setInt(++i, c.getId());
			statement.setString(++i, c.getName());
			statement.setInt(++i, (int) (c.getLastSiegeDate().getTimeInMillis() / 1000L));
			statement.setInt(++i, c.getOwner() == null ? 0 : c.getOwner().getClanId());
			statement.setInt(++i, (int) (c.getOwnDate().getTimeInMillis() / 1000L));
			statement.setInt(++i, (int) (c.getSiegeDate().getTimeInMillis() / 1000L));
			statement.setLong(++i, c.getAuctionMinBid());
			statement.setInt(++i, c.getAuctionLength());
			statement.setString(++i, c.getAuctionDescription());
			statement.setInt(++i, c.getCycle());
			statement.setInt(++i, c.getPaidCycle());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn("ClanHallDAO#update0(ClanHall): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}