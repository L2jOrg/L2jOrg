package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.entity.Hero;

import org.napile.pair.primitive.IntIntPair;
import org.napile.primitive.maps.IntIntMap;
import org.napile.primitive.maps.impl.HashIntIntMap;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Iqman
 * @reworked by Bonux
**/
public class CustomHeroDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CustomHeroDAO.class);
	private static final CustomHeroDAO _instance = new CustomHeroDAO();

	private IntIntMap _heroes = new HashIntIntMap();

	public CustomHeroDAO()
	{
		deleteExpiredHeroes();
		loadCustomHeroes();
	}

	public static CustomHeroDAO getInstance()
	{
		return _instance;
	}

	private void deleteExpiredHeroes()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM custom_heroes WHERE time > 0 AND time < ?");
			statement.setInt(1, (int) (System.currentTimeMillis() / 1000L));
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.error("CustomHeroDAO:deleteExpiredHeroes()", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void loadCustomHeroes()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM custom_heroes");
			rset = statement.executeQuery();
			while(rset.next())
			{
				_heroes.put(rset.getInt("hero_id"), rset.getInt("time"));
			}	
		}
		catch(final Exception e)
		{
			_log.error("CharacterVariablesDAO:loadCustomHeroes()", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
			_log.info("CustomHeroDAO: loaded " + _heroes.size() + " custom heroes.");
		}
	}

	public void addCustomHero(int objectId, int time)
	{
		if(time != -1 && _heroes.get(objectId) > time)
			return;

		Connection con = null;
		PreparedStatement statement = null;			
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO custom_heroes (hero_id, time) VALUES(?,?)");
			statement.setInt(1, objectId);	
			statement.setInt(2, time);		
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("CharacterVariablesDAO:addCustomHero(int,int)", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
			_heroes.put(objectId, time);
		}
	}

	public void removeCustomHero(int objectId)
	{
		if(_heroes.containsKey(objectId) || Hero.getInstance().isHero(objectId))
			return;

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM custom_heroes WHERE hero_id =?");
			statement.setInt(1, objectId);
			statement.execute();
		}
		catch(Exception e)
		{
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
			_heroes.remove(objectId);
		}
	}

	public boolean isCustomHero(int objectId)
	{
		int time = _heroes.get(objectId);
		if(time == -1 || time > (System.currentTimeMillis() / 1000L))
			return true;

		return false;	
	}

	public int getExpiryTime(int objectId)
	{
		return _heroes.get(objectId);
	}

	public int[] getActiveHeroes()
	{
		IntSet result = new HashIntSet();
		for(IntIntPair entry : _heroes.entrySet())
		{
			int time = entry.getValue();
			if(time == -1 || time > (System.currentTimeMillis() / 1000L))
				result.add(entry.getKey());
		}
		return result.toArray();
	}
}