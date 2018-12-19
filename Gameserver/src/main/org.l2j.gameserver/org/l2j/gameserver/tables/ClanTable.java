package org.l2j.gameserver.tables;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.l2j.commons.database.L2DatabaseFactory;
import org.l2j.commons.dbutils.DbUtils;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.dao.ClanLeaderRequestDAO;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.instancemanager.ServerVariables;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.PledgeAttendanceType;
import org.l2j.gameserver.model.pledge.*;
import org.l2j.gameserver.model.pledge.ClanWar.ClanWarPeriod;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.utils.Log;
import org.l2j.gameserver.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ClanTable
{
	private static final Logger _log = LoggerFactory.getLogger(ClanTable.class);

	private static final long CLAN_WAR_STORE_DELAY = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS);

	private static final String REFRESH_CLAN_ATTENDANCE_INFO_VAR = "clan_refresh_date";

	private static ClanTable _instance;

	private final Map<Integer, Clan> _clans = new ConcurrentHashMap<>();
	private final Map<Integer, Alliance> _alliances = new ConcurrentHashMap<Integer, Alliance>();

	private final TIntObjectMap<ClanChangeLeaderRequest> _changeRequests = new TIntObjectHashMap<>();

	private final List<ClanWar> _clanWarUpdateCache = new ArrayList<ClanWar>();

	public static ClanTable getInstance()
	{
		if(_instance == null)
		{
			new ClanTable();
		}
		return _instance;
	}

	public Clan[] getClans()
	{
		return _clans.values().toArray(new Clan[_clans.size()]);
	}

	public Alliance[] getAlliances()
	{
		return _alliances.values().toArray(new Alliance[_alliances.size()]);
	}

	private ClanTable()
	{
		_instance = this;

		restoreClans();
		restoreAllies();
		restoreWars();

		_changeRequests.putAll(ClanLeaderRequestDAO.getInstance().select());
	}

	public Clan getClan(int clanId)
	{
		if(clanId <= 0)
			return null;
		return _clans.get(clanId);
	}

	public String getClanName(int clanId)
	{
		Clan c = getClan(clanId);
		return c != null ? c.getName() : Util.STRING_EMPTY;
	}

	public Clan getClanByCharId(int charId)
	{
		if(charId <= 0)
			return null;

		for(Clan clan : getClans())
		{
			if(clan != null && clan.isAnyMember(charId))
				return clan;
		}
		return null;
	}

	public Alliance getAlliance(int allyId)
	{
		if(allyId <= 0)
			return null;
		return _alliances.get(allyId);
	}

	public Alliance getAllianceByCharId(int charId)
	{
		if(charId <= 0)
			return null;

		Clan charClan = getClanByCharId(charId);
		return charClan == null ? null : charClan.getAlliance();
	}

	public Map.Entry<Clan, Alliance> getClanAndAllianceByCharId(int charId)
	{
		Player player = GameObjectsStorage.getPlayer(charId);
		Clan charClan = player != null ? player.getClan() : getClanByCharId(charId);
		return new SimpleEntry<Clan, Alliance>(charClan, charClan == null ? null : charClan.getAlliance());
	}

	public void restoreClans()
	{
		List<Integer> clanIds = new ArrayList<Integer>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT clan_id FROM clan_data");
			result = statement.executeQuery();
			while(result.next())
			{
				clanIds.add(result.getInt("clan_id"));
			}
		}
		catch(Exception e)
		{
			_log.warn("Error while restoring clans!!! " + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, result);
		}

		for(int clanId : clanIds)
		{
			Clan clan = Clan.restore(clanId);
			if(clan == null)
			{
				_log.warn("Error while restoring clanId: " + clanId);
				continue;
			}

			if(clan.getAllSize() <= 0)
			{
				_log.warn("membersCount = 0 for clanId: " + clanId);
				continue;
			}

			if(clan.getLeader() == null)
			{
				_log.warn("Not found leader for clanId: " + clanId);
				continue;
			}

			_clans.put(clan.getClanId(), clan);
		}

		long lastRefreshTime = ServerVariables.getLong(REFRESH_CLAN_ATTENDANCE_INFO_VAR, System.currentTimeMillis());
		if(TimeUtils.DAILY_DATE_PATTERN.next(lastRefreshTime) < System.currentTimeMillis())
			refreshClanAttendanceInfo();
	}

	public void restoreAllies()
	{
		List<Integer> allyIds = new ArrayList<Integer>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT ally_id FROM ally_data");
			result = statement.executeQuery();
			while(result.next())
			{
				allyIds.add(result.getInt("ally_id"));
			}
		}
		catch(Exception e)
		{
			_log.warn("Error while restoring allies!!! " + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, result);
		}

		for(int allyId : allyIds)
		{
			Alliance ally = new Alliance(allyId);

			if(ally.getMembersCount() <= 0)
			{
				_log.warn("membersCount = 0 for allyId: " + allyId);
				continue;
			}

			if(ally.getLeader() == null)
			{
				_log.warn("Not found leader for allyId: " + allyId);
				continue;
			}

			_alliances.put(ally.getAllyId(), ally);
		}
	}

	public Clan getClanByName(String clanName)
	{
		for(Clan clan : _clans.values())
		{
			if(clan.getName().equalsIgnoreCase(clanName))
				return clan;
		}

		return null;
	}

	public int getClansSizeByName(String clanName)
	{
		int result = 0;
		for(Clan clan : _clans.values())
		{
			if(clan.getName().equalsIgnoreCase(clanName))
				result ++;
		}
		return result;
	}

	public Alliance getAllyByName(String allyName)
	{
		for(Alliance ally : _alliances.values())
		{
			if(ally.getAllyName().equalsIgnoreCase(allyName))
				return ally;
		}

		return null;
	}

	public Clan createClan(Player player, String clanName)
	{
		if(getClanByName(clanName) == null)
		{
			UnitMember leader = new UnitMember(player);
			leader.setLeaderOf(Clan.SUBUNIT_MAIN_CLAN);

			Clan clan = new Clan(IdFactory.getInstance().getNextId());

			SubUnit unit = new SubUnit(clan, Clan.SUBUNIT_MAIN_CLAN, leader, clanName, false);
			unit.addUnitMember(leader);
			clan.addSubUnit(unit, false); //не нужно совать в базу. пихается ниже

			clan.store();

			player.setPledgeType(Clan.SUBUNIT_MAIN_CLAN);
			player.setClan(clan);
			player.setPowerGrade(6);

			leader.setAttendanceType(PledgeAttendanceType.NOT_ACQUIRED);
			leader.setPlayerInstance(player, false);

			_clans.put(clan.getClanId(), clan);

			clan.onEnterClan(player);

			return clan;
		}
		else
		{
			return null;
		}
	}

	public void dissolveClan(Clan clan)
	{
		int leaderId = clan.getLeaderId();
		clan.flush();

		deleteClanFromDb(clan.getClanId(), leaderId);

		_clans.remove(clan.getClanId());
	}

	public static void deleteClanFromDb(int clanId, int leaderId)
	{
		long curtime = System.currentTimeMillis();

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET clanid=0,title='',pledge_type=0,pledge_rank=0,lvl_joined_academy=0,apprentice=0,leaveclan=? WHERE clanid=?");
			statement.setLong(1, curtime / 1000L);
			statement.setInt(2, clanId);
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE characters SET deleteclan=? WHERE obj_Id=?");
			statement.setLong(1, curtime / 1000L);
			statement.setInt(2, leaderId);
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM clan_data WHERE clan_id=?");
			statement.setInt(1, clanId);
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM clan_subpledges WHERE clan_id=?");
			statement.setInt(1, clanId);
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM clan_privs WHERE clan_id=?");
			statement.setInt(1, clanId);
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM siege_clans WHERE clan_id=?");
			statement.setInt(1, clanId);
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM siege_players WHERE clan_id=?");
			statement.setInt(1, clanId);
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM clan_wars WHERE attacker_clan=? OR opposing_clan=?");
			statement.setInt(1, clanId);
			statement.setInt(2, clanId);
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM clan_skills WHERE clan_id=?");
			statement.setInt(1, clanId);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn("could not dissolve clan:" + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public Alliance createAlliance(Player player, String allyName)
	{
		Alliance alliance = null;

		if(getAllyByName(allyName) == null)
		{
			Clan leader = player.getClan();
			alliance = new Alliance(IdFactory.getInstance().getNextId(), allyName, leader);
			alliance.store();
			_alliances.put(alliance.getAllyId(), alliance);

			player.getClan().setAllyId(alliance.getAllyId());
			for(Player temp : player.getClan().getOnlineMembers(0))
			{
				temp.broadcastCharInfo();
			}
		}

		return alliance;
	}

	public void dissolveAlly(Player player)
	{
		int allyId = player.getAllyId();
		for(Clan member : player.getAlliance().getMembers())
		{
			member.setAllyId(0);
			member.broadcastClanStatus(false, true, false);
			member.broadcastToOnlineMembers(SystemMsg.YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE);
			member.setLeavedAlly();
		}
		deleteAllyFromDb(allyId);
		_alliances.remove(allyId);
		player.sendPacket(SystemMsg.THE_ALLIANCE_HAS_BEEN_DISSOLVED);
		player.getClan().setDissolvedAlly();
	}

	public void deleteAllyFromDb(int allyId)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET ally_id=0 WHERE ally_id=?");
			statement.setInt(1, allyId);
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM ally_data WHERE ally_id=?");
			statement.setInt(1, allyId);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn("could not dissolve clan:" + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void storeClanWar(ClanWar war, boolean force)
	{
		if(force)
			storeClanWar0(war);
		else if(!_clanWarUpdateCache.contains(war))
			_clanWarUpdateCache.add(war);
	}

	public void storeClanWar0(ClanWar war)
	{
		Clan attackerClan = war.getAttackerClan();
		Clan opposingClan = war.getOpposingClan();

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO clan_wars (attacker_clan, opposing_clan, period, period_start_time, last_kill_time, attackers_kill_counter, opposers_kill_counter) VALUES(?,?,?,?,?,?,?)");
			statement.setInt(1, attackerClan.getClanId());
			statement.setInt(2, opposingClan.getClanId());
			statement.setString(3, war.getPeriod().toString());
			statement.setInt(4, war.getCurrentPeriodStartTime());
			statement.setInt(5, war.getLastKillTime());
			statement.setInt(6, war.getAttackersKillCounter());
			statement.setInt(7, war.getOpposersKillCounter());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn(getClass().getSimpleName() + ": Could not store clan war data:" + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void storeClanWars()
	{
		synchronized(_clanWarUpdateCache)
		{
			for(ClanWar war : _clanWarUpdateCache)
				storeClanWar0(war);

			_clanWarUpdateCache.clear();
		}
	}

	public void deleteClanWar(ClanWar war)
	{
		Clan attackerClan = war.getAttackerClan();
		Clan opposingClan = war.getOpposingClan();

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM clan_wars WHERE attacker_clan=? AND opposing_clan=?");
			statement.setInt(1, attackerClan.getClanId());
			statement.setInt(2, opposingClan.getClanId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn(getClass().getSimpleName() + ": Error removing clan wars data.", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	private void restoreWars()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT attacker_clan, opposing_clan, period, period_start_time, last_kill_time, attackers_kill_counter, opposers_kill_counter FROM clan_wars");
			rset = statement.executeQuery();
			while(rset.next())
			{
				int attackerClanId = rset.getInt("attacker_clan");
				int opposinClanId = rset.getInt("opposing_clan");

				Clan attackerClan = getClan(attackerClanId);
				Clan opposinClan = getClan(opposinClanId);

				ClanWarPeriod period = ClanWarPeriod.valueOf(rset.getString("period"));

				int periodStartTime = rset.getInt("period_start_time");
				int lastKillTime = rset.getInt("last_kill_time");
				int attackersKillCounter = rset.getInt("attackers_kill_counter");
				int opposersKilLCounter = rset.getInt("opposers_kill_counter");

				if(attackerClan != null && opposinClan != null)
					new ClanWar(attackerClan, opposinClan, period, periodStartTime, lastKillTime, attackersKillCounter, opposersKilLCounter);
				else
					_log.warn(getClass().getSimpleName() + ": restorewars one of clans is null attacker_clan:" + attackerClanId + " opposing_clan:" + opposinClanId);
			}
		}
		catch(Exception e)
		{
			_log.warn(getClass().getSimpleName() + ": Error restoring clan wars data.", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> ClanTable.this.storeClanWars(), CLAN_WAR_STORE_DELAY, CLAN_WAR_STORE_DELAY);
	}

	public void checkClans()
	{
		final long currentTime = System.currentTimeMillis();

		for(Clan clan : getClans())
		{
			if(clan.getDisbandEndTime() > 0 && clan.getDisbandEndTime() < currentTime)
				dissolveClan(clan);
		}

		for(ClanChangeLeaderRequest changeLeaderRequest : _changeRequests.valueCollection())
		{
			if(changeLeaderRequest.getTime() < System.currentTimeMillis())
			{
				loop:
				{
					Clan clan = getClan(changeLeaderRequest.getClanId());
					if(clan == null)
						break loop;

					SubUnit subUnit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
					if(subUnit == null)
						break loop;

					UnitMember newLeader = subUnit.getUnitMember(changeLeaderRequest.getNewLeaderId());
					if(newLeader == null)
						break loop;

					subUnit.setLeader(newLeader, true);
				}

				cancelRequest(changeLeaderRequest, true);
			}
		}
	}

	public void cancelRequest(ClanChangeLeaderRequest changeLeaderRequest, boolean done)
	{
		_changeRequests.remove(changeLeaderRequest.getClanId());

		ClanLeaderRequestDAO.getInstance().delete(changeLeaderRequest);

		Log.add("Clan: " + changeLeaderRequest.getClanId() + ", newLeaderId: " + changeLeaderRequest.getNewLeaderId() + ", endTime: " + TimeUtils.toSimpleFormat(changeLeaderRequest.getTime()), done ? Log.ClanChangeLeaderRequestDone : Log.ClanChangeLeaderRequestCancel);
	}

	public ClanChangeLeaderRequest getRequest(int clanId)
	{
		return _changeRequests.get(clanId);
	}

	public void addRequest(ClanChangeLeaderRequest request)
	{
		_changeRequests.put(request.getClanId(), request);

		ClanLeaderRequestDAO.getInstance().insert(request);

		Log.add("Clan: " + request.getClanId() + ", newLeaderId: " + request.getNewLeaderId() + ", endTime: " + TimeUtils.toSimpleFormat(request.getTime()), Log.ClanChangeLeaderRequestAdd);
	}

	public void refreshClanAttendanceInfo()
	{
		for(Clan clan : getClans())
			clan.refreshAttendanceInfo();

		ServerVariables.set(REFRESH_CLAN_ATTENDANCE_INFO_VAR, System.currentTimeMillis());
	}

	public void saveClanHuntingProgress()
	{
		for(Clan clan : getClans())
			clan.saveHuntingProgress();
	}
}