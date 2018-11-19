package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dao.JdbcEntityState;
import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.ResidenceSide;
import l2s.gameserver.tables.ClanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 18:10/15.04.2011
 */
public class CastleDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CastleDAO.class);
	private static final CastleDAO _instance = new CastleDAO();

	public static final String SELECT_SQL_QUERY = "SELECT treasury, siege_date, last_siege_date, owner_id, own_date, side FROM castle WHERE id=? LIMIT 1";
	public static final String REPLACE_SQL_QUERY = "REPLACE INTO castle (id, name, treasury, last_siege_date, owner_id, own_date, siege_date, side) VALUES (?,?,?,?,?,?,?,?)";

	public static CastleDAO getInstance()
	{
		return _instance;
	}

	public void select(Castle castle)
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			statement.setInt(1, castle.getId());
			rset = statement.executeQuery();
			if(rset.next())
			{
				castle.setTreasury(rset.getLong("treasury"));
				castle.getSiegeDate().setTimeInMillis(rset.getLong("siege_date") * 1000L);
				castle.getLastSiegeDate().setTimeInMillis(rset.getLong("last_siege_date") * 1000L);
				castle.setOwner(ClanTable.getInstance().getClan(rset.getInt("owner_id")));
				castle.getOwnDate().setTimeInMillis(rset.getLong("own_date") * 1000L);
				castle.setResidenceSide(ResidenceSide.VALUES[rset.getInt("side")], true);
			}
		}
		catch(Exception e)
		{
			_log.error("CastleDAO.select(Castle):" + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void update(Castle residence)
	{
		if(!residence.getJdbcState().isUpdatable())
			return;

		residence.setJdbcState(JdbcEntityState.STORED);
		update0(residence);
	}

	private void update0(Castle castle)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(REPLACE_SQL_QUERY);

			int i = 0;
			statement.setInt(++i, castle.getId());
			statement.setString(++i, castle.getName());
			statement.setLong(++i, castle.getTreasury());
			statement.setInt(++i, (int) (castle.getLastSiegeDate().getTimeInMillis() / 1000L));
			statement.setInt(++i, castle.getOwner() == null ? 0 : castle.getOwner().getClanId());
			statement.setInt(++i, (int) (castle.getOwnDate().getTimeInMillis() / 1000L));
			statement.setInt(++i, (int) (castle.getSiegeDate().getTimeInMillis() / 1000L));
			statement.setInt(++i, castle.getResidenceSide().ordinal());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn("CastleDAO#update0(Castle): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}
