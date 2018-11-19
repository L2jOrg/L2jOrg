package bosses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpicBossState
{
	private static final Logger _log = LoggerFactory.getLogger(EpicBossState.class);

	public static enum State
	{
		NOTSPAWN,
		ALIVE,
		DEAD,
		INTERVAL
	}

	private int _bossId;
	private long _respawnDate;
	private State _state;

	public int getBossId()
	{
		return _bossId;
	}

	public void setBossId(int newId)
	{
		_bossId = newId;
	}

	public State getState()
	{
		return _state;
	}

	public void setState(State newState)
	{
		_state = newState;
	}

	public long getRespawnDate()
	{
		return _respawnDate;
	}

	public void setRespawnDate(long interval)
	{
		_respawnDate = interval + System.currentTimeMillis();
	}

	public EpicBossState(int bossId)
	{
		this(bossId, true);
	}

	public EpicBossState(int bossId, boolean isDoLoad)
	{
		_bossId = bossId;
		_respawnDate = 0;
		_state = State.NOTSPAWN;
		if(isDoLoad)
			load();
	}

	public void load()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT * FROM epic_boss_spawn WHERE bossId = ? LIMIT 1");
			statement.setInt(1, _bossId);
			rset = statement.executeQuery();

			if(rset.next())
			{
				_respawnDate = rset.getLong("respawnDate") * 1000L;

				if(_respawnDate - System.currentTimeMillis() <= 0)
					_state = State.NOTSPAWN;
				else
				{
					int tempState = rset.getInt("state");
					if(tempState == State.NOTSPAWN.ordinal())
						_state = State.NOTSPAWN;
					else if(tempState == State.INTERVAL.ordinal())
						_state = State.INTERVAL;
					else if(tempState == State.ALIVE.ordinal())
						_state = State.ALIVE;
					else if(tempState == State.DEAD.ordinal())
						_state = State.DEAD;
					else
						_state = State.NOTSPAWN;
				}
			}
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void save()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO epic_boss_spawn (bossId,respawnDate,state) VALUES(?,?,?)");
			statement.setInt(1, _bossId);
			statement.setInt(2, (int) (_respawnDate / 1000));
			statement.setInt(3, _state.ordinal());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void setNextRespawnDate(long newRespawnDate)
	{
		_respawnDate = newRespawnDate;
	}

	public long getInterval()
	{
		long interval = _respawnDate - System.currentTimeMillis();
		return interval > 0 ? interval : 0;
	}
}