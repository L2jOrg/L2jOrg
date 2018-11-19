package l2s.gameserver.model.pledge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.PledgeAttendanceType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExPledgeBonusUpdate;
import l2s.gameserver.network.l2.s2c.ExPledgeBonusUpdate.BonusType;
import l2s.gameserver.network.l2.s2c.NickNameChangedPacket;
import l2s.gameserver.network.l2.s2c.PledgeShowMemberListUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.PledgeBonusUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitMember
{
	private static final Logger _log = LoggerFactory.getLogger(UnitMember.class);

	private static final int ATTENDANCE_CHANGE_DELAY = 1800000;

	private Player _player;

	private Clan _clan;
	private String _name;
	private String _title;
	private int _objectId;
	private int _level;
	private int _classId;
	private int _sex;
	private int _pledgeType;
	private int _powerGrade;
	private int _apprentice;

	private PledgeAttendanceType _attendanceType;

	private int _leaderOf = Clan.SUBUNIT_NONE;

	private ScheduledFuture<?> _changeAttendanceTask = null;

	public UnitMember(Clan clan, String name, String title, int level, int classId, int objectId, int pledgeType, int powerGrade, int apprentice, int sex, int leaderOf, PledgeAttendanceType clanAttendance)
	{
		_clan = clan;
		_objectId = objectId;
		_name = name;
		_title = title;
		_level = level;
		_classId = classId;
		_pledgeType = pledgeType;
		_powerGrade = powerGrade;
		_apprentice = apprentice;
		_sex = sex;
		_leaderOf = leaderOf;
		_attendanceType = clanAttendance;

		if(powerGrade != 0)
		{
			RankPrivs r = clan.getRankPrivs(powerGrade);
			r.setParty(clan.countMembersByRank(powerGrade));
		}
	}

	public UnitMember(Player player)
	{
		_objectId = player.getObjectId();
		_player = player;
	}

	public void setPlayerInstance(Player player, boolean exit)
	{
		_player = exit ? null : player;
		if(player == null)
		{
			cancelChangeAttendanceTask();
			return;
		}

		_clan = player.getClan();
		_name = player.getName();
		_title = player.getTitle();
		_level = player.getLevel();
		_classId = player.getClassId().getId();
		_pledgeType = player.getPledgeType();
		_powerGrade = player.getPowerGrade();
		_apprentice = player.getApprentice();
		_sex = player.getSex().ordinal();

		if(!exit)
		{
			if(_attendanceType == PledgeAttendanceType.NOT_ACQUIRED && _changeAttendanceTask == null)
			{
				_changeAttendanceTask = ThreadPoolManager.getInstance().schedule(() ->
				{
					int oldLevel = PledgeBonusUtils.getAttendanceProgressLevel(_clan.getAttendanceProgress());
					setAttendanceType(PledgeAttendanceType.ACQUIRED);
					_clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdatePacket(this));
					_clan.broadcastToOnlineMembers(new ExPledgeBonusUpdate(BonusType.ATTENDANCE, _clan.getAttendanceProgress()));
					int newLevel = PledgeBonusUtils.getAttendanceProgressLevel(_clan.getAttendanceProgress());
					if(newLevel > oldLevel)
						_clan.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.YOUR_CLAN_HAS_ACHIEVED_LOGIN_BONUS_LV_S1).addInteger(newLevel));

				}, ATTENDANCE_CHANGE_DELAY);
			}
		}
		else
			cancelChangeAttendanceTask();
	}

	private void cancelChangeAttendanceTask()
	{
		if(_changeAttendanceTask != null)
		{
			_changeAttendanceTask.cancel(false);
			_changeAttendanceTask = null;
		}
	}

	public Player getPlayer()
	{
		return _player;
	}

	public boolean isOnline()
	{
		Player player = getPlayer();
		return player != null && !player.isInOfflineMode();
	}

	public Clan getClan()
	{
		Player player = getPlayer();
		return player == null ? _clan : player.getClan();
	}

	public int getClassId()
	{
		Player player = getPlayer();
		return player == null ? _classId : player.getClassId().getId();
	}

	public int getSex()
	{
		Player player = getPlayer();
		return player == null ? _sex : player.getSex().ordinal();
	}

	public int getLevel()
	{
		Player player = getPlayer();
		return player == null ? _level : player.getLevel();
	}

	public String getName()
	{
		Player player = getPlayer();
		return player == null ? _name : player.getName();
	}

	public int getObjectId()
	{
		return _objectId;
	}

	public String getTitle()
	{
		Player player = getPlayer();
		return player == null ? _title : player.getTitle();
	}

	public void setTitle(String title)
	{
		Player player = getPlayer();
		_title = title;
		if(player != null)
		{
			player.setTitle(title);
			player.broadcastPacket(new NickNameChangedPacket(player));
		}
		else
		{
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE characters SET title=? WHERE obj_Id=?");
				statement.setString(1, title);
				statement.setInt(2, getObjectId());
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
	}

	public SubUnit getSubUnit()
	{
		return _clan.getSubUnit(_pledgeType);
	}

	public int getPledgeType()
	{
		Player player = getPlayer();
		return player == null ? _pledgeType : player.getPledgeType();
	}

	public void setPledgeType(int pledgeType)
	{
		Player player = getPlayer();
		_pledgeType = pledgeType;
		if(player != null)
			player.setPledgeType(pledgeType);
		else
			updatePledgeType();
	}

	private void updatePledgeType()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET pledge_type=? WHERE obj_Id=?");
			statement.setInt(1, _pledgeType);
			statement.setInt(2, getObjectId());
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

	public int getPowerGrade()
	{
		Player player = getPlayer();
		return player == null ? _powerGrade : player.getPowerGrade();
	}

	public void setPowerGrade(int newPowerGrade)
	{
		Player player = getPlayer();
		int oldPowerGrade = getPowerGrade();
		_powerGrade = newPowerGrade;
		if(player != null)
			player.setPowerGrade(newPowerGrade);
		else
			updatePowerGrade();
		updatePowerGradeParty(oldPowerGrade, newPowerGrade);
	}

	private void updatePowerGradeParty(int oldGrade, int newGrade)
	{
		if(oldGrade != 0)
		{
			RankPrivs r1 = getClan().getRankPrivs(oldGrade);
			r1.setParty(getClan().countMembersByRank(oldGrade));
		}
		if(newGrade != 0)
		{
			RankPrivs r2 = getClan().getRankPrivs(newGrade);
			r2.setParty(getClan().countMembersByRank(newGrade));
		}
	}

	private void updatePowerGrade()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET pledge_rank=? WHERE obj_Id=?");
			statement.setInt(1, _powerGrade);
			statement.setInt(2, getObjectId());
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

	private int getApprentice()
	{
		Player player = getPlayer();
		return player == null ? _apprentice : player.getApprentice();
	}

	public void setApprentice(int apprentice)
	{
		Player player = getPlayer();
		_apprentice = apprentice;
		if(player != null)
			player.setApprentice(apprentice);
		else
			updateApprentice();
	}

	private void updateApprentice()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET apprentice=? WHERE obj_Id=?");
			statement.setInt(1, _apprentice);
			statement.setInt(2, getObjectId());
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

	public PledgeAttendanceType getAttendanceType()
	{
		return _attendanceType;
	}

	public void setAttendanceType(PledgeAttendanceType attendanceType)
	{
		if(_attendanceType == attendanceType)
			return;

		_attendanceType = attendanceType;
		updateAttendance();
	}

	private void updateAttendance()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET clan_attendance=? WHERE obj_Id=?");
			statement.setInt(1, _attendanceType.ordinal());
			statement.setInt(2, getObjectId());
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

	public String getApprenticeName()
	{
		if(getApprentice() != 0)
			if(getClan().getAnyMember(getApprentice()) != null)
				return getClan().getAnyMember(getApprentice()).getName();
		return "";
	}

	public boolean hasApprentice()
	{
		return getApprentice() != 0;
	}

	public int getSponsor()
	{
		if(getPledgeType() != Clan.SUBUNIT_ACADEMY)
			return 0;
		int id = getObjectId();
		for(UnitMember element : getClan())
			if(element.getApprentice() == id)
				return element.getObjectId();
		return 0;
	}

	private String getSponsorName()
	{
		int sponsorId = getSponsor();
		if(sponsorId == 0)
			return "";
		else if(getClan().getAnyMember(sponsorId) != null)
			return getClan().getAnyMember(sponsorId).getName();
		return "";
	}

	public boolean hasSponsor()
	{
		return getSponsor() != 0;
	}

	public String getRelatedName()
	{
		if(getPledgeType() == Clan.SUBUNIT_ACADEMY)
			return getSponsorName();
		return getApprenticeName();
	}

	public boolean isClanLeader()
	{
		Player player = getPlayer();
		return player == null ? (_leaderOf == Clan.SUBUNIT_MAIN_CLAN) : player.isClanLeader();
	}

	public int isSubLeader()
	{
		for(SubUnit pledge : getClan().getAllSubUnits())
			if(pledge.getLeaderObjectId() == getObjectId())
				return pledge.getType();
		return 0;
	}

	public void setLeaderOf(int leaderOf)
	{
		_leaderOf = leaderOf;
	}

	public int isLeaderOf()
	{
		return _leaderOf;
	}
}