package l2s.gameserver.model.entity.olympiad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.dao.OlympiadParticipantsDAO;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.instancemanager.ServerVariables;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.StatsSet;

import org.napile.primitive.maps.IntIntMap;
import org.napile.primitive.maps.impl.HashIntIntMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlympiadDatabase
{
	private static final Logger _log = LoggerFactory.getLogger(OlympiadDatabase.class);

	public static synchronized void loadParticipantsRank()
	{
		Olympiad._participantRank.clear();

		IntIntMap tmpPlace = new HashIntIntMap();
		for(int heroId : Hero.getInstance().getHeroes().keySet().toArray())
			Olympiad._participantRank.put(heroId, 1);

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(OlympiadParticipantsDAO.GET_ALL_CLASSIFIED_PARTICIPANTS);
			rset = statement.executeQuery();
			int place = 1;
			while (rset.next())
			{
				int charId = rset.getInt(Hero.CHAR_ID);
				if(!Olympiad._participantRank.containsKey(charId))
					tmpPlace.put(charId, place++);
			}
		}
		catch(Exception e)
		{
			_log.error("Olympiad System: Error!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		int rank1 = (int) Math.round(tmpPlace.size() * 0.01);
		int rank2 = (int) Math.round(tmpPlace.size() * 0.10);
		int rank3 = (int) Math.round(tmpPlace.size() * 0.25);
		int rank4 = (int) Math.round(tmpPlace.size() * 0.50);

		if(rank1 == 0)
		{
			rank1 = 1;
			rank2++;
			rank3++;
			rank4++;
		}

		for(int charId : tmpPlace.keySet().toArray())
		{
			if(tmpPlace.get(charId) <= rank1)
				Olympiad._participantRank.put(charId, 2);
			else if(tmpPlace.get(charId) <= rank2)
				Olympiad._participantRank.put(charId, 3);
			else if(tmpPlace.get(charId) <= rank3)
				Olympiad._participantRank.put(charId, 4);
			else if(tmpPlace.get(charId) <= rank4)
				Olympiad._participantRank.put(charId, 5);
			else
				Olympiad._participantRank.put(charId, 6);
		}
	}

	/**
	 * Сбрасывает информацию о ноблесах, сохраняя очки за предыдущий период
	 */
	public static synchronized void cleanupParticipants()
	{
		_log.info("Olympiad: Calculating last period...");
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(OlympiadParticipantsDAO.OLYMPIAD_CALCULATE_LAST_PERIOD);
			statement.setInt(1, Config.OLYMPIAD_BATTLES_FOR_REWARD);
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement(OlympiadParticipantsDAO.OLYMPIAD_CLEANUP_PARTICIPANTS);
			statement.setInt(1, Config.OLYMPIAD_POINTS_DEFAULT);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("Olympiad System: Couldn't calculate last period!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}

		for(OlympiadParticipiantData participantsInfo : Olympiad.getParticipantsMap().values())
		{
			int points = participantsInfo.getPoints();
			int compDone = participantsInfo.getCompDone();
			int compWin = participantsInfo.getCompWin();
			participantsInfo.setPoints(Config.OLYMPIAD_POINTS_DEFAULT);
			if(compDone >= Config.OLYMPIAD_BATTLES_FOR_REWARD)
			{
				if(compWin > 0)
					points += Config.OLYMPIAD_1_OR_MORE_WIN_POINTS_BONUS;
				else
					points += Config.OLYMPIAD_ALL_LOOSE_POINTS_BONUS;

				participantsInfo.setPointsPast(points);
				participantsInfo.setPointsPastStatic(points);
			}
			else
			{
				participantsInfo.setPointsPast(0);
				participantsInfo.setPointsPastStatic(0);
			}
			participantsInfo.setCompDone(0);
			participantsInfo.setCompWin(0);
			participantsInfo.setCompLoose(0);
			participantsInfo.setClassedGamesCount(0);
			participantsInfo.setNonClassedGamesCount(0);
		}
	}

	public static synchronized List<StatsSet> computeHeroesToBe()
	{
		if(Olympiad._period != 1)
			return Collections.emptyList();

		List<StatsSet> heroesToBe = new ArrayList<StatsSet>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			StatsSet hero;

			for(ClassId id3 : ClassId.VALUES)
			{
				if(id3.isOfLevel(ClassLevel.THIRD))
				{
					ClassId id2 = id3.getParent(0);
					if(id2.isOfLevel(ClassLevel.SECOND))
					{
						statement = con.prepareStatement(OlympiadParticipantsDAO.OLYMPIAD_GET_HEROS);
						statement.setInt(1, SubClassType.BASE_CLASS.ordinal());
						statement.setInt(2, id2.getId());
						statement.setInt(3, id3.getId());
						statement.setInt(4, Config.OLYMPIAD_BATTLES_FOR_REWARD);
						rset = statement.executeQuery();

						if(rset.next())
						{
							hero = new StatsSet();
							hero.set(Hero.CLASS_ID, rset.getInt(Hero.CLASS_ID));
							hero.set(Hero.CHAR_ID, rset.getInt(Hero.CHAR_ID));
							hero.set(Hero.CHAR_NAME, rset.getString(Hero.CHAR_NAME));

							heroesToBe.add(hero);
						}
						DbUtils.close(statement, rset);
					}
				}
			}
		}
		catch(Exception e)
		{
			_log.error("Olympiad System: Couldnt heros from db!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return heroesToBe;
	}

	public static synchronized void saveParticipantData(int participantId)
	{
		OlympiadParticipantsDAO.getInstance().replace(participantId);
	}

	public static synchronized void saveParticipantsData()
	{
		for(int participantId : Olympiad.getParticipantsMap().keySet().toArray())
			saveParticipantData(participantId);
	}

	public static synchronized void deleteParticipantData(int participantId)
	{
		OlympiadParticipantsDAO.getInstance().delete(participantId);
	}

	public static synchronized void setNewOlympiadStartTime()
	{
		Announcements.announceToAll(new SystemMessage(SystemMessage.OLYMPIAD_PERIOD_S1_HAS_STARTED).addNumber(Olympiad._currentCycle));

		Olympiad.setOlympiadPeriodStartTime(System.currentTimeMillis());
		Olympiad.setWeekStartTime(System.currentTimeMillis());

		Olympiad._isOlympiadEnd = false;
	}

	public static void save()
	{
		saveParticipantsData();
		ServerVariables.set("Olympiad_CurrentCycle", Olympiad._currentCycle);
		ServerVariables.set("Olympiad_Period", Olympiad._period);
		ServerVariables.set("olympiad_period_start_time", Olympiad.getOlympiadPeriodStartTime());
		ServerVariables.set("olympiad_validation_start_time", Olympiad.getValidationStartTime());
		ServerVariables.set("olympiad_week_start_time", Olympiad.getWeekStartTime());
	}
}