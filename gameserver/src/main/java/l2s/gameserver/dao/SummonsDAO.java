package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.SummonInstance;
import l2s.gameserver.model.instances.SummonInstance.RestoredSummon;
import l2s.gameserver.utils.SqlBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public class SummonsDAO
{
	private static final Logger _log = LoggerFactory.getLogger(SummonsDAO.class);
	private static final SummonsDAO _instance = new SummonsDAO();

	SummonsDAO()
	{
		//
	}

	public static SummonsDAO getInstance()
	{
		return _instance;
	}

	public List<RestoredSummon> restore(Player player)
	{
		List<RestoredSummon> result = new ArrayList<RestoredSummon>();
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			int objectId = player.getObjectId();
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT `skill_id`,`skill_level`,`curHp`,`curMp`,`time` FROM `character_summons_save` WHERE `owner_obj_id`=?");
			statement.setInt(1, objectId);
			rset = statement.executeQuery();
			if(rset.next())
			{
				int skillId = rset.getInt("skill_id");
				int skillLvl = rset.getInt("skill_level");
				int curHp = rset.getInt("curHp");
				int curMp = rset.getInt("curMp");
				int time = rset.getInt("time");

				result.add(new RestoredSummon(skillId, skillLvl, curHp, curMp, time));
			}

			DbUtils.closeQuietly(statement, rset);

			statement = con.prepareStatement("DELETE FROM character_summons_save WHERE owner_obj_id = ?");
			statement.setInt(1, objectId);
			statement.execute();
			DbUtils.close(statement);
		}
		catch(final Exception e)
		{
			_log.error("Could not restore active summon data!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con);
		}
		return result;
	}

	public void insert(SummonInstance summon)
	{
		if(!summon.isSaveable())
			return;

		Player owner = summon.getPlayer();
		if(owner == null)
			return;

		Connection con = null;
		Statement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();

			SqlBatch b = new SqlBatch("INSERT IGNORE INTO `character_summons_save` (`owner_obj_id`,`skill_id`,`skill_level`,`curHp`,`curMp`,`time`) VALUES");

			StringBuilder sb = new StringBuilder("(");
			sb.append(owner.getObjectId()).append(",");
			sb.append(summon.getSkillId()).append(",");
			sb.append(summon.getSkillLvl()).append(",");
			sb.append(summon.getCurrentHp()).append(",");
			sb.append(summon.getCurrentMp()).append(",");
			sb.append(summon.getConsumeCountdown()).append(")");
			b.write(sb.toString());

			if(!b.isEmpty())
				statement.executeUpdate(b.close());
		}
		catch(final Exception e)
		{
			_log.error("Could not store active summon data!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}