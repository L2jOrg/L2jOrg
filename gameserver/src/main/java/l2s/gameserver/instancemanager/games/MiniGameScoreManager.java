package l2s.gameserver.instancemanager.games;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.Config;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import org.napile.primitive.comparators.IntComparator;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CTreeIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date  15:15/15.10.2010
 * @see ext.properties
 */
public class MiniGameScoreManager
{
	private static final Logger _log = LoggerFactory.getLogger(MiniGameScoreManager.class);
	private final IntObjectMap<Set<String>> _scores = new CTreeIntObjectMap<>((o1, o2) -> o2 - o1);

	private static MiniGameScoreManager _instance = new MiniGameScoreManager();

	public static MiniGameScoreManager getInstance()
	{
		return _instance;
	}

	private MiniGameScoreManager()
	{
		if(Config.EX_JAPAN_MINIGAME)
			load();
	}

	private void load()
	{
		Connection con = null;
		Statement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			rset = statement.executeQuery("SELECT characters.char_name AS name, character_minigame_score.score AS score FROM characters, character_minigame_score WHERE characters.obj_Id=character_minigame_score.object_id");
			while(rset.next())
			{
				String name = rset.getString("name");
				int score = rset.getInt("score");

				addScore(name, score);
			}
		}
		catch(SQLException e)
		{
			_log.info("Exception: " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void insertScore(Player player, int score)
	{
		if(addScore(player.getName(), score))
		{
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("INSERT INTO character_minigame_score(object_id, score) VALUES (?, ?)");
				statement.setInt(1, player.getObjectId());
				statement.setInt(2, score);
				statement.execute();
			}
			catch(final Exception e)
			{
				_log.info("Exception: " + e, e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
	}

	public boolean addScore(String name, int score)
	{
		Set<String> set = _scores.get(score);
		if(set == null)
			_scores.put(score, (set = new CopyOnWriteArraySet<String>()));

		return set.add(name);
	}

	public IntObjectMap<Set<String>> getScores()
	{
		return _scores;
	}
}
