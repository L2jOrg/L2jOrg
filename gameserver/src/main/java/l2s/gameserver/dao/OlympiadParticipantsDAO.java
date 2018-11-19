package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.entity.olympiad.OlympiadParticipiantData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlympiadParticipantsDAO
{
	private static final Logger _log = LoggerFactory.getLogger(OlympiadParticipantsDAO.class);
	private static final OlympiadParticipantsDAO _instance = new OlympiadParticipantsDAO();

	private static final String SELECT_SQL_QUERY = "SELECT char_id, characters.char_name as char_name, character_subclasses.class_id, olympiad_points, olympiad_points_past, olympiad_points_past_static, competitions_done, competitions_loose, competitions_win, game_classes_count, game_noclasses_count FROM olympiad_participants LEFT JOIN characters ON characters.obj_Id = olympiad_participants.char_id LEFT JOIN character_subclasses ON character_subclasses.char_obj_id = olympiad_participants.char_id AND character_subclasses.type=? WHERE characters.obj_Id > 0";
	private static final String REPLACE_SQL_QUERY = "REPLACE INTO `olympiad_participants` (`char_id`, `olympiad_points`, `olympiad_points_past`, `olympiad_points_past_static`, `competitions_done`, `competitions_win`, `competitions_loose`, game_classes_count, game_noclasses_count) VALUES (?,?,?,?,?,?,?,?,?)";
	private static final String DELETE_SQL_QUERY = "DELETE FROM olympiad_participants WHERE char_id=?";
	public static final String OLYMPIAD_GET_HEROS = "SELECT `char_id`, characters.char_name AS char_name, character_subclasses.class_id AS class_id FROM `olympiad_participants` LEFT JOIN characters ON char_id=characters.obj_Id LEFT JOIN character_subclasses ON char_id=character_subclasses.char_obj_id AND character_subclasses.type=? WHERE characters.obj_Id > 0 AND (character_subclasses.class_id = ? OR character_subclasses.class_id = ?) AND `competitions_done` >= ? AND `competitions_win` > 0 ORDER BY `olympiad_points` DESC, `competitions_win` DESC, `competitions_done` DESC";
	public static final String GET_EACH_CLASS_LEADER = "SELECT characters.char_name AS char_name FROM `olympiad_participants` LEFT JOIN characters ON char_id=characters.obj_Id LEFT JOIN character_subclasses ON char_id=character_subclasses.char_obj_id AND character_subclasses.type=? WHERE characters.obj_Id > 0 AND (character_subclasses.class_id = ? OR character_subclasses.class_id = ?) AND `olympiad_points_past_static` != 0 ORDER BY `olympiad_points_past_static` DESC LIMIT 10";
	public static final String GET_ALL_CLASSIFIED_PARTICIPANTS = "SELECT `char_id` FROM `olympiad_participants` ORDER BY olympiad_points_past_static DESC";
	public static final String OLYMPIAD_CALCULATE_LAST_PERIOD = "UPDATE `olympiad_participants` SET `olympiad_points_past` = `olympiad_points`, `olympiad_points_past_static` = `olympiad_points` WHERE `competitions_done` >= ?";
	public static final String OLYMPIAD_CLEANUP_PARTICIPANTS = "UPDATE `olympiad_participants` SET `olympiad_points` = ?, `competitions_done` = 0, `competitions_win` = 0, `competitions_loose` = 0, game_classes_count=0, game_noclasses_count=0";

	public static OlympiadParticipantsDAO getInstance()
	{
		return _instance;
	}

	public void select()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			statement.setInt(1, SubClassType.BASE_CLASS.ordinal());
			rset = statement.executeQuery();
			while(rset.next())
			{
				OlympiadParticipiantData data = new OlympiadParticipiantData(rset.getInt("char_id"), rset.getString("char_name"), rset.getInt("class_id"));

				data.setPoints(rset.getInt("olympiad_points"));
				data.setPointsPast(rset.getInt("olympiad_points_past"));
				data.setPointsPastStatic(rset.getInt("olympiad_points_past_static"));
				data.setCompDone(rset.getInt("competitions_done"));
				data.setCompWin(rset.getInt("competitions_win"));
				data.setCompLoose(rset.getInt("competitions_loose"));
				data.setClassedGamesCount(rset.getInt("game_classes_count"));
				data.setNonClassedGamesCount(rset.getInt("game_noclasses_count"));

				Olympiad.getParticipantsMap().put(data.getObjectId(), data);
			}
		}
		catch(Exception e)
		{
			_log.error("OlympiadParticipantsDAO:select():", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void replace(int participantId)
	{
		OlympiadParticipiantData data = Olympiad.getParticipantInfo(participantId);
		if(data == null)
		{
			delete(participantId);
			return;
		}

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(REPLACE_SQL_QUERY);
			statement.setInt(1, data.getObjectId());
			statement.setInt(2, data.getPoints());
			statement.setInt(3, data.getPointsPast());
			statement.setInt(4, data.getPointsPastStatic());
			statement.setInt(5, data.getCompDone());
			statement.setInt(6, data.getCompWin());
			statement.setInt(7, data.getCompLoose());
			statement.setInt(8, data.getClassedGamesCount());
			statement.setInt(9, data.getNonClassedGamesCount());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("OlympiadParticipantsDAO:replace(int): " + participantId, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void delete(int participantId)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setInt(1, participantId);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("OlympiadParticipantsDAO:delete(int): " + participantId, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}
