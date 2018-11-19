package l2s.gameserver.dao;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Friend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 23:27/22.03.2011
 */
public class CharacterFriendDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CharacterFriendDAO.class);

	private static final CharacterFriendDAO _instance = new CharacterFriendDAO();

	public static CharacterFriendDAO getInstance()
	{
		return _instance;
	}

	public TIntObjectMap<Friend> select(Player owner)
	{
		TIntObjectMap<Friend> map = new TIntObjectHashMap<Friend>();
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT f.friend_id, f.memo, c.char_name, s.class_id, s.level, c.clanid, clan.ally_id, subpledge.name, ally.ally_name, c.createtime, c.lastAccess FROM character_friends f LEFT JOIN characters c ON f.friend_id = c.obj_Id LEFT JOIN clan_data clan ON c.clanid = clan.clan_id LEFT JOIN clan_subpledges subpledge ON (c.clanid = subpledge.clan_id AND subpledge.type = 0) LEFT JOIN ally_data ally ON clan.ally_id = ally.ally_id LEFT JOIN character_subclasses s ON (f.friend_id = s.char_obj_id AND s.active = 1) WHERE f.char_id = ?");
			statement.setInt(1, owner.getObjectId());
			rset = statement.executeQuery();
			while(rset.next())
			{
				String name = rset.getString("c.char_name");
				if(name == null)
					continue;

				int objectId = rset.getInt("f.friend_id");
				int classId = rset.getInt("s.class_id");
				int level = rset.getInt("s.level");
				String memo = rset.getString("f.memo");
				int clanId = rset.getInt("c.clanid");
				int allyId = rset.getInt("clan.ally_id");
				String clanName = rset.getString("subpledge.name");
				String allyName = rset.getString("ally.ally_name");
				int createTime = rset.getInt("c.createtime");
				int lastAccess = rset.getInt("c.lastAccess");

				map.put(objectId, new Friend(objectId, name, classId, level, memo, clanId, clanName, allyId, allyName, createTime, lastAccess));
			}
		}
		catch(Exception e)
		{
			_log.error("CharacterFriendDAO.load(L2Player): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return map;
	}

	public void insert(Player owner, Player friend)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO character_friends (char_id,friend_id) VALUES(?,?)");
			statement.setInt(1, owner.getObjectId());
			statement.setInt(2, friend.getObjectId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn(owner.getFriendList() + " could not add friend objectid: " + friend.getObjectId(), e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public boolean updateMemo(Player owner, int friend, String memo)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE character_friends SET memo=? WHERE char_id=? AND friend_id=?");
			statement.setString(1, memo);
			statement.setInt(2, owner.getObjectId());
			statement.setInt(3, friend);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn(owner.getFriendList() + " could not update memo objectid: " + friend, e);
			return false;
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		return true;
	}

	public void delete(int ownerId, int friendId)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_friends WHERE (char_id=? AND friend_id=?) OR (char_id=? AND friend_id=?)");
			statement.setInt(1, ownerId);
			statement.setInt(2, friendId);
			statement.setInt(3, friendId);
			statement.setInt(4, ownerId);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn(getClass().getSimpleName() + ": could not delete friend objectId: " + friendId + " ownerId: " + ownerId, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}