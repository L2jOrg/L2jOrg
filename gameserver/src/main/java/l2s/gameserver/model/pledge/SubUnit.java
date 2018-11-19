package l2s.gameserver.model.pledge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.network.l2.s2c.ExSubPledgetSkillAdd;
import l2s.gameserver.skills.SkillEntry;
import org.apache.commons.lang3.StringUtils;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.napile.primitive.maps.impl.CTreeIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @author VISTALL
* @date  15:01/01.12.2010
*/
public class SubUnit
{
	private static final Logger _log = LoggerFactory.getLogger(SubUnit.class);

	private IntObjectMap<SkillEntry> _skills = new CTreeIntObjectMap<SkillEntry>();
	private IntObjectMap<UnitMember> _members = new CHashIntObjectMap<UnitMember>();

	private int _type;

	private int _leaderObjectId;
	private UnitMember _leader;

	private String _name;
	private Clan _clan;
	private boolean _upgraded;

	public SubUnit(Clan c, int type, UnitMember leader, String name, boolean upgraded)
	{
		_clan = c;
		_type = type;
		_name = name;
		_upgraded = upgraded;

		setLeader(leader, false);
	}

	public SubUnit(Clan c, int type, int leader, String name)
	{
		_clan = c;
		_type = type;
		_leaderObjectId = leader;
		_name = name;
	}

	public int getType()
	{
		return _type;
	}

	public String getName()
	{
		return _name;
	}

	public UnitMember getLeader()
	{
		return _leader;
	}

	public boolean isUnitMember(int obj)
	{
		return _members.containsKey(obj);
	}

	public void addUnitMember(UnitMember member)
	{
		_members.put(member.getObjectId(), member);
	}

	public UnitMember getUnitMember(int obj)
	{
		if(obj == 0)
			return null;
		return _members.get(obj);
	}

	public UnitMember getUnitMember(String obj)
	{
		for(UnitMember m : getUnitMembers())
		{
			if(m.getName().equalsIgnoreCase(obj))
				return m;
		}

		return null;
	}

	public void removeUnitMember(int objectId)
	{
		UnitMember m = _members.remove(objectId);
		if(m == null)
			return;

		if(objectId == getLeaderObjectId()) // subpledge leader
			setLeader(null, true); // clan leader has to assign another one, via villagemaster

		if(m.hasSponsor())
			_clan.getAnyMember(m.getSponsor()).setApprentice(0);

		removeMemberInDatabase(m);

		m.setPlayerInstance(null, true);
	}

	public void replace(int objectId, int newUnitId)
	{
		SubUnit newUnit = _clan.getSubUnit(newUnitId);
		if(newUnit == null)
			return;

		UnitMember m = _members.remove(objectId);
		if(m == null)
			return;

		m.setPledgeType(newUnitId);
		newUnit.addUnitMember(m);

		if(m.getPowerGrade() > 5)
			m.setPowerGrade(_clan.getAffiliationRank(m.getPledgeType()));
	}

	public int getLeaderObjectId()
	{
		return _leader == null ? 0 : _leader.getObjectId();
	}

	public int size()
	{
		return _members.size();
	}

	public Collection<UnitMember> getUnitMembers()
	{
		return _members.values();
	}

	public void setLeader(UnitMember newLeader, boolean updateDB)
	{
		final UnitMember old = _leader;
		if(old != null) // обновляем старого мембера
			old.setLeaderOf(Clan.SUBUNIT_NONE);

		_leader = newLeader;
		_leaderObjectId = newLeader == null ? 0 : newLeader.getObjectId();

		if(newLeader != null)
			newLeader.setLeaderOf(_type);

		if(updateDB)
		{
			if(old != null && old.getPlayer() != null)
				old.getPlayer().getInventory().validateItems();

			if(newLeader.getPlayer() != null)
				newLeader.getPlayer().getInventory().validateItems();

			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE clan_subpledges SET leader_id=? WHERE clan_id=? and type=?");
				statement.setInt(1, getLeaderObjectId());
				statement.setInt(2, _clan.getClanId());
				statement.setInt(3, _type);
				statement.execute();
			}
			catch(Exception e)
			{
				_log.error("Exception: " + e, e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
	}

	public void setName(String name, boolean updateDB)
	{
		_name = name;
		if(updateDB)
		{
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE clan_subpledges SET name=? WHERE clan_id=? and type=?");
				statement.setString(1, _name);
				statement.setInt(2, _clan.getClanId());
				statement.setInt(3, _type);
				statement.execute();
			}
			catch(Exception e)
			{
				_log.error("Exception: " + e, e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
	}

	public String getLeaderName()
	{
		return _leader == null ? StringUtils.EMPTY : _leader.getName();
	}

	public SkillEntry addSkill(SkillEntry newSkillEntry, boolean store)
	{
		SkillEntry oldSkillEntry = null;
		if(newSkillEntry != null)
		{
			// Replace oldSkillEntry by newSkillEntry or Add the newSkillEntry
			oldSkillEntry = _skills.put(newSkillEntry.getId(), newSkillEntry);

			if(store)
			{
				Connection con = null;
				PreparedStatement statement = null;

				try
				{
					con = DatabaseFactory.getInstance().getConnection();

					if(oldSkillEntry != null)
					{
						statement = con.prepareStatement("UPDATE clan_subpledges_skills SET skill_level=? WHERE skill_id=? AND clan_id=? AND type=?");
						statement.setInt(1, newSkillEntry.getLevel());
						statement.setInt(2, oldSkillEntry.getId());
						statement.setInt(3, _clan.getClanId());
						statement.setInt(4, _type);
						statement.execute();
					}
					else
					{
						statement = con.prepareStatement("INSERT INTO clan_subpledges_skills (clan_id,type,skill_id,skill_level) VALUES (?,?,?,?)");
						statement.setInt(1, _clan.getClanId());
						statement.setInt(2, _type);
						statement.setInt(3, newSkillEntry.getId());
						statement.setInt(4, newSkillEntry.getLevel());
						statement.execute();
					}
				}
				catch(Exception e)
				{
					_log.warn("Exception: " + e, e);
				}
				finally
				{
					DbUtils.closeQuietly(con, statement);
				}
			}

			ExSubPledgetSkillAdd packet = new ExSubPledgetSkillAdd(_type, newSkillEntry.getId(), newSkillEntry.getLevel());
			for(UnitMember temp : _clan)
				if(temp.isOnline())
				{
					Player player = temp.getPlayer();
					if(player != null)
					{
						player.sendPacket(packet);
						if(player.getPledgeType() == _type)
							addSkill(player, newSkillEntry);
					}
				}
		}

		return oldSkillEntry;
	}

	/**
	 * Функция вызывается из клановской addSkillsQuietly(), отдельно вызывать не нужно
	 */
	public void addSkillsQuietly(Player player)
	{
		for(SkillEntry skillEntry : _skills.values())
			addSkill(player, skillEntry);
	}

	public void enableSkills(Player player)
	{
		for(SkillEntry skillEntry : _skills.values())
		{
			Skill skill = skillEntry.getTemplate();
			if(skill.getMinPledgeRank().ordinal() <= player.getPledgeRank().ordinal())
			{
				if(!skill.clanLeaderOnly() || skill.clanLeaderOnly() && player.isClanLeader())
					player.removeUnActiveSkill(skill);
			}
		}
	}

	public void disableSkills(Player player)
	{
		for(SkillEntry skillEntry : _skills.values())
			player.addUnActiveSkill(skillEntry.getTemplate());
	}

	private void addSkill(Player player, SkillEntry skillEntry)
	{
		Skill skill = skillEntry.getTemplate();
		if(skill.getMinPledgeRank().ordinal() <= player.getPledgeRank().ordinal())
		{
			if(!skill.clanLeaderOnly() || skill.clanLeaderOnly() && player.isClanLeader())
			{
				player.addSkill(skillEntry, false);
				if(_clan.getReputationScore() < 0 || player.isInOlympiadMode())
					player.addUnActiveSkill(skill);
			}
		}
	}

	public Collection<SkillEntry> getSkills()
	{
		return _skills.values();
	}

	private static void removeMemberInDatabase(UnitMember member)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET clanid=0, clan_attendance=0, pledge_type=?, pledge_rank=0, lvl_joined_academy=0, apprentice=0, title='', leaveclan=? WHERE obj_Id=?");
			statement.setInt(1, Clan.SUBUNIT_NONE);
			statement.setLong(2, System.currentTimeMillis() / 1000);
			statement.setInt(3, member.getObjectId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn("Exception: " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void restore()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(//
			"SELECT `c`.`char_name` AS `char_name`," + //
			"`s`.`level` AS `level`," + //
			"`s`.`class_id` AS `classid`," + //
			"`c`.`obj_Id` AS `obj_id`," + //
			"`c`.`title` AS `title`," + //
			"`c`.`pledge_rank` AS `pledge_rank`," + //
			"`c`.`apprentice` AS `apprentice`, " + //
			"`c`.`sex` AS `sex`, " + //
			"`c`.`clan_attendance` AS `clan_attendance` " + //
			"FROM `characters` `c` " + //
			"LEFT JOIN `character_subclasses` `s` ON (`s`.`char_obj_id` = `c`.`obj_Id` AND `s`.`type` = '" + SubClassType.BASE_CLASS.ordinal() + "') " + //
			"WHERE `c`.`clanid`=? AND `c`.`pledge_type`=? ORDER BY `c`.`lastaccess` DESC");

			statement.setInt(1, _clan.getClanId());
			statement.setInt(2, _type);
			rset = statement.executeQuery();

			while(rset.next())
			{
				UnitMember member = new UnitMember(_clan, rset.getString("char_name"), rset.getString("title"), rset.getInt("level"), rset.getInt("classid"), rset.getInt("obj_Id"), _type, rset.getInt("pledge_rank"), rset.getInt("apprentice"), rset.getInt("sex"), -128, l2s.gameserver.model.base.PledgeAttendanceType.VALUES[rset.getInt("clan_attendance")]);

				addUnitMember(member);
			}

			if(_type != Clan.SUBUNIT_ACADEMY)
			{
				SubUnit mainClan = _clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
				UnitMember leader = mainClan.getUnitMember(_leaderObjectId);
				if(leader != null)
					setLeader(leader, false);
				else if(_type == Clan.SUBUNIT_MAIN_CLAN)
					_log.error("Clan " + _name + " have no leader!");
			}
		}
		catch(Exception e)
		{
			_log.warn("Error while restoring clan members for clan: " + _clan.getClanId() + " " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void restoreSkills()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT skill_id,skill_level FROM clan_subpledges_skills WHERE clan_id=? AND type=?");
			statement.setInt(1, _clan.getClanId());
			statement.setInt(2, _type);
			rset = statement.executeQuery();

			while(rset.next())
			{
				int id = rset.getInt("skill_id");
				int level = rset.getInt("skill_level");

				SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(id, level);

				_skills.put(skillEntry.getId(), skillEntry);
			}
		}
		catch(Exception e)
		{
			_log.warn("Exception: " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public int getSkillLevel(int id, int def)
	{
		SkillEntry skillEntry = _skills.get(id);
		return skillEntry == null ? def : skillEntry.getLevel();
	}

	public int getSkillLevel(int id)
	{
		return getSkillLevel(id, -1);
	}

	public boolean isUpgraded()
	{
		return _upgraded;
	}

	public void setUpgraded(boolean upgraded, boolean updateInDb)
	{
		_upgraded = upgraded;
		if(updateInDb)
		{
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE clan_subpledges SET upgraded=? WHERE clan_id=? and type=?");
				statement.setInt(1, isUpgraded() ? 1 : 0);
				statement.setInt(2, _clan.getClanId());
				statement.setInt(3, _type);
				statement.execute();
			}
			catch(Exception e)
			{
				_log.error("Exception: " + e, e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
	}
}