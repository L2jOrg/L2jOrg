package org.l2j.gameserver.instancemanager.clansearch;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.l2j.commons.dbutils.DbUtils;
import org.l2j.commons.threading.RunnableImpl;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.database.DatabaseFactory;
import org.l2j.gameserver.model.clansearch.ClanSearchClan;
import org.l2j.gameserver.model.clansearch.ClanSearchPlayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ClanSearchTask extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(ClanSearchTask.class);

	private final TIntObjectMap<ClanSearchClan> _newClans = new TIntObjectHashMap<ClanSearchClan>();
	private final TIntObjectMap<ClanSearchPlayer> _newWaiters = new TIntObjectHashMap<ClanSearchPlayer>();
	private final TIntObjectMap<ClanSearchPlayer> _newApplicants = new TIntObjectHashMap<ClanSearchPlayer>();

	private final TIntList _removalClans = new TIntArrayList();
	private final TIntList _removalWaiters = new TIntArrayList();
	private final TIntList _removalApplicants = new TIntArrayList();

	private final TIntLongMap _clanLocks = new TIntLongHashMap();
	private final TIntLongMap _applicantLocks = new TIntLongHashMap();
	private final TIntLongMap _waiterLocks = new TIntLongHashMap();

	public void scheduleClanForAddition(ClanSearchClan clan)
	{
		_newClans.put(clan.getClanId(), clan);
	}

	public void scheduleWaiterForAddition(ClanSearchPlayer player)
	{
		_newWaiters.put(player.getCharId(), player);
	}

	public void scheduleApplicantForAddition(ClanSearchPlayer player)
	{
		_newApplicants.put(player.getCharId(), player);
	}

	public void scheduleClanForRemoval(int clanId)
	{
		_removalClans.add(clanId);
	}

	public void scheduleWaiterForRemoval(int playerId)
	{
		_removalWaiters.add(playerId);
	}

	public void scheduleApplicantForRemoval(int playerId)
	{
		_removalApplicants.add(playerId);
	}

	@Override
	public void runImpl() throws Exception
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			for(ClanSearchClan csClan : _newClans.valueCollection())
			{
				statement = con.prepareStatement(ClanSearchQueries.ADD_CLAN);
				statement.setInt(1, csClan.getClanId());
				statement.setString(2, csClan.getSearchType().name());
				statement.setString(3, csClan.getDesc());
				statement.setInt(4, csClan.getApplication());
				statement.setInt(5, csClan.getSubUnit());
				statement.setString(6, csClan.getSearchType().name());
				statement.setString(7, csClan.getDesc());
				statement.setInt(8, csClan.getApplication());
				statement.setInt(9, csClan.getSubUnit());
				statement.executeUpdate();

				DbUtils.closeQuietly(statement);
			}
		}
		catch(SQLException e)
		{
			failed(e);
		}

		if(_newWaiters.size() > 0)
		{
			int offset = 0;
			try
			{
				statement = con.prepareStatement(ClanSearchQueries.getAddWaitingPlayerQuery(_newWaiters.size()));
				for(ClanSearchPlayer csPlayer : _newWaiters.valueCollection())
				{
					statement.setInt(++offset, csPlayer.getCharId());
					statement.setString(++offset, csPlayer.getName());
					statement.setInt(++offset, csPlayer.getLevel());
					statement.setInt(++offset, csPlayer.getClassId());
					statement.setString(++offset, csPlayer.getSearchType().name());
				}
				statement.executeUpdate();
			}
			catch(SQLException e)
			{
				failed(e);
			}
			finally
			{
				DbUtils.closeQuietly(statement);
			}
		}

		if(_newApplicants.size() > 0)
		{
			int offset = 0;
			try
			{
				statement = con.prepareStatement(ClanSearchQueries.getAddApplicantPlayerQuery(_newApplicants.size()));
				for(ClanSearchPlayer csPlayer : _newApplicants.valueCollection())
				{
					statement.setInt(++offset, csPlayer.getCharId());
					statement.setInt(++offset, csPlayer.getPrefferedClanId());
					statement.setString(++offset, csPlayer.getName());
					statement.setInt(++offset, csPlayer.getLevel());
					statement.setInt(++offset, csPlayer.getClassId());
					statement.setString(++offset, csPlayer.getSearchType().name());
					statement.setString(++offset, csPlayer.getDesc());
				}
				statement.executeUpdate();
			}
			catch(SQLException e)
			{
				failed(e);
			}
			finally
			{
				DbUtils.closeQuietly(statement);
			}
		}

		if(_removalClans.size() > 0)
		{
			int offset = 0;
			try
			{
				statement = con.prepareStatement(ClanSearchQueries.getRemoveClanQuery(_removalClans.size()));
				for(int clanId : _removalClans.toArray())
					statement.setInt(++offset, clanId);
				statement.executeUpdate();
			}
			catch(SQLException e)
			{
				failed(e);
			}
			finally
			{
				DbUtils.closeQuietly(statement);
			}

			offset = 0;
			try
			{
				statement = con.prepareStatement(ClanSearchQueries.getRemoveClanApplicants(_removalClans.size()));
				for(int clanId : _removalClans.toArray())
					statement.setInt(++offset, clanId);
				statement.executeUpdate();
			}
			catch(SQLException e)
			{
				failed(e);
			}
			finally
			{
				DbUtils.closeQuietly(statement);
			}
		}

		if(_removalWaiters.size() > 0)
		{
			int offset = 0;
			try
			{
				statement = con.prepareStatement(ClanSearchQueries.getRemoveWaiterQuery(_removalWaiters.size()));
				for(int playerId : _removalWaiters.toArray())
					statement.setInt(++offset, playerId);
				statement.executeUpdate();
			}
			catch(SQLException e)
			{
				failed(e);
			}
			finally
			{
				DbUtils.closeQuietly(statement);
			}
		}

		if(_removalApplicants.size() > 0)
		{
			int offset = 0;
			try
			{
				statement = con.prepareStatement(ClanSearchQueries.getRemoveApplicantQuery(_removalApplicants.size()));
				for(int charId : _removalApplicants.toArray())
					statement.setInt(++offset, charId);
				statement.executeUpdate();
			}
			catch(SQLException e)
			{
				failed(e);
			}
			finally
			{
				DbUtils.closeQuietly(statement);
			}
		}

		try
		{
			statement = con.prepareStatement(ClanSearchQueries.CLEAN_CLANS);
			statement.executeUpdate();
		}
		catch(SQLException e)
		{
			failed(e);
		}
		finally
		{
			DbUtils.closeQuietly(statement);
		}

		try
		{
			statement = con.prepareStatement(ClanSearchQueries.CLEAN_APPLICANTS);
			statement.executeUpdate();
		}
		catch(SQLException e)
		{
			failed(e);
		}
		finally
		{
			DbUtils.closeQuietly(statement);
		}

		try
		{
			statement = con.prepareStatement(ClanSearchQueries.CLEAN_WAITERS);
			statement.executeUpdate();
		}
		catch(SQLException e)
		{
			failed(e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}

		_newClans.clear();
		_newWaiters.clear();
		_newApplicants.clear();
		_removalClans.clear();
		_removalApplicants.clear();
		_removalWaiters.clear();
	}

	private void failed(Exception e)
	{
		_log.error(getClass().getSimpleName() + ": Failed to update database for clan search system.", e);
	}

	public void lockClan(final int clanId, long lockTime)
	{
		_clanLocks.put(clanId, System.currentTimeMillis() + lockTime);

		ThreadPoolManager.getInstance().schedule(() ->
		{
			_clanLocks.remove(clanId);
		}
		, lockTime);
	}

	public boolean isClanLocked(int clanId)
	{
		return _clanLocks.containsKey(clanId);
	}

	public long getClanLockTime(int clanId)
	{
		return _clanLocks.containsKey(clanId) ? Math.max(0L, System.currentTimeMillis() - _clanLocks.get(clanId)) : 0L;
	}

	public void lockWaiter(final int charId, long lockTime)
	{
		_waiterLocks.put(charId, System.currentTimeMillis() + lockTime);

		ThreadPoolManager.getInstance().schedule(() ->
		{
			_waiterLocks.remove(charId);
		}
		, lockTime);
	}

	public boolean isWaiterLocked(int charId)
	{
		return _waiterLocks.containsKey(charId);
	}

	public long getWaiterLockTime(int clanId)
	{
		return _waiterLocks.containsKey(clanId) ? Math.max(0L, System.currentTimeMillis() - _waiterLocks.get(clanId)) : 0L;
	}

	public void lockApplicant(final int charId, long lockTime)
	{
		_applicantLocks.put(charId, System.currentTimeMillis() + lockTime);

		ThreadPoolManager.getInstance().schedule(() ->
		{
			_applicantLocks.remove(charId);
		}
		, lockTime);
	}

	public boolean isApplicantLocked(int charId)
	{
		return _applicantLocks.containsKey(charId);
	}

	public long getApplicantLockTime(int clanId)
	{
		return _applicantLocks.containsKey(clanId) ? Math.max(0L, System.currentTimeMillis() - _applicantLocks.get(clanId)) : 0L;
	}
}