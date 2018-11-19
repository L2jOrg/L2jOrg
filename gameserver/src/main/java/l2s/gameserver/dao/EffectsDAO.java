package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.skills.EffectUseType;
import l2s.gameserver.utils.SqlBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 13:01/02.02.2011
 */
public class EffectsDAO
{
	private static final int SUMMON_SKILL_OFFSET = 100000;
	private static final Logger _log = LoggerFactory.getLogger(EffectsDAO.class);
	private static final EffectsDAO _instance = new EffectsDAO();

	EffectsDAO()
	{
		//
	}

	public static EffectsDAO getInstance()
	{
		return _instance;
	}

	public void restoreEffects(Playable playable)
	{
		int objectId, id;
		if(playable.isPlayer())
		{
			objectId = playable.getObjectId();
			id = ((Player) playable).getActiveClassId();
		}
		else if(playable.isServitor())
		{
			objectId = playable.getPlayer().getObjectId();
			id = ((Servitor) playable).getEffectIdentifier();
			if(playable.isSummon())
			{
				id += SUMMON_SKILL_OFFSET;
				id *= 10;
			}
		}
		else
			return;

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT `skill_id`,`skill_level`,`duration`,`left_time`,`is_self` FROM `character_effects_save` WHERE `object_id`=? AND `id`=?");
			statement.setInt(1, objectId);
			statement.setInt(2, id);
			rset = statement.executeQuery();
			while(rset.next())
			{
				final int skillId = rset.getInt("skill_id");
				final int skillLvl = rset.getInt("skill_level");

				final Skill skill = SkillHolder.getInstance().getSkill(skillId, skillLvl);
				if(skill == null)
					continue;

				final boolean isSelf = rset.getInt("is_self") > 0;
				final int duration = rset.getInt("duration");
				final int leftTime = rset.getInt("left_time");

				final EffectUseType useType = isSelf ? EffectUseType.SELF : EffectUseType.NORMAL;
				Abnormal abnormal = new Abnormal(playable, playable, skill, useType, true);
				if(abnormal.isSaveable())
				{
					abnormal.setDuration(duration);
					abnormal.setTimeLeft(leftTime);

					playable.getAbnormalList().add(abnormal);
				}
			}

			DbUtils.closeQuietly(statement, rset);

			statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id = ? AND id=?");
			statement.setInt(1, objectId);
			statement.setInt(2, id);
			statement.execute();
			DbUtils.close(statement);
		}
		catch(final Exception e)
		{
			_log.error("Could not restore active effects data!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con);
		}
	}

	public void insert(Playable playable)
	{
		int objectId, id;
		if(playable.isPlayer())
		{
			objectId = playable.getObjectId();
			id = ((Player) playable).getActiveClassId();
		}
		else if(playable.isServitor())
		{
			objectId = playable.getPlayer().getObjectId();
			id = ((Servitor) playable).getEffectIdentifier();
			if(playable.isSummon())
			{
				id += SUMMON_SKILL_OFFSET;
				id *= 10;
			}
		}
		else
			return;

		final Abnormal[] effects = playable.getAbnormalList().toArray();
		if(effects.length == 0)
			return;

		//Arrays.sort(effects, EffectsComparator.getInstance());

		Connection con = null;
		Statement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();

			SqlBatch b = new SqlBatch("INSERT IGNORE INTO `character_effects_save` (`object_id`,`skill_id`,`skill_level`,`duration`,`left_time`,`id`,`is_self`) VALUES");

			StringBuilder sb;
			for(Abnormal effect : effects)
			{
				if(effect != null)
				{
					if(!effect.isOfUseType(EffectUseType.SELF) && !effect.isOfUseType(EffectUseType.NORMAL))
						continue;

					if(effect.isSaveable())
					{
						sb = new StringBuilder("(");
						sb.append(objectId).append(",");
						sb.append(effect.getSkill().getId()).append(",");
						sb.append(effect.getSkill().getLevel()).append(",");
						sb.append(effect.getDuration()).append(",");
						sb.append(effect.getTimeLeft()).append(",");
						sb.append(id).append(",");
						sb.append(effect.isOfUseType(EffectUseType.SELF) ? 1 : 0).append(")");
						b.write(sb.toString());
					}
				}
			}

			if(!b.isEmpty())
				statement.executeUpdate(b.close());
		}
		catch(final Exception e)
		{
			_log.error("Could not store active effects data!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}
