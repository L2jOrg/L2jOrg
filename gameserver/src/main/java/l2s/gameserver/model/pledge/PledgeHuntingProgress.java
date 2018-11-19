package l2s.gameserver.model.pledge;

import java.sql.Connection;
import java.sql.PreparedStatement;

import l2s.commons.dao.JdbcEntity;
import l2s.commons.dao.JdbcEntityState;
import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PledgeHuntingProgress implements JdbcEntity
{
	private static final Logger _log = LoggerFactory.getLogger(PledgeHuntingProgress.class);

	private JdbcEntityState _jdbcEntityState = JdbcEntityState.CREATED;

	private final Clan _clan;

	private int _value = 0;

	public PledgeHuntingProgress(Clan clan)
	{
		_clan = clan;
	}

	public void setValue(int value)
	{
		_value = value;
	}

	public int getValue()
	{
		return _value;
	}

	@Override
	public void setJdbcState(JdbcEntityState state)
	{
		_jdbcEntityState = state;
	}

	@Override
	public JdbcEntityState getJdbcState()
	{
		return _jdbcEntityState;
	}

	@Override
	public void update()
	{
		if(!getJdbcState().isUpdatable())
			return;

		int clanId = _clan.getClanId();
		if(clanId == 0)
		{
			_log.warn("HuntingProgress#update() with empty ClanId");
			Thread.dumpStack();
			return;
		}

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET hunting_progress=? WHERE clan_id=?");
			statement.setInt(1, getValue());
			statement.setInt(2, clanId);
			statement.execute();

			setJdbcState(JdbcEntityState.STORED);
		}
		catch(Exception e)
		{
			_log.error("HuntingProgress#update(): ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	@Override
	public void save()
	{
		update();
	}

	@Override
	public void delete()
	{
		update();
	}
}