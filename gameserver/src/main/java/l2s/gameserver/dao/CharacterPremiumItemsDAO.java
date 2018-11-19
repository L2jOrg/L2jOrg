package l2s.gameserver.dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.PremiumItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 */
public class CharacterPremiumItemsDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CharacterPremiumItemsDAO.class);

	private static final CharacterPremiumItemsDAO _instance = new CharacterPremiumItemsDAO();

	public static CharacterPremiumItemsDAO getInstance()
	{
		return _instance;
	}

	public List<PremiumItem> select(Player owner)
	{
		List<PremiumItem> list = new ArrayList<PremiumItem>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT receive_time, item_id, item_count, sender FROM character_premium_items WHERE char_id = ?");
			statement.setInt(1, owner.getObjectId());
			rset = statement.executeQuery();
			while(rset.next())
			{
				int receive_time = rset.getInt("receive_time");
				int item_id = rset.getInt("item_id");
				long item_count = rset.getLong("item_count");
				String sender = rset.getString("sender");

				list.add(new PremiumItem(receive_time, item_id, item_count, sender));
			}
		}
		catch(Exception e)
		{
			_log.error("CharacterPremiumItemsDAO.select(L2Player): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return list;
	}

	public boolean insert(Player owner, PremiumItem item)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO character_premium_items (char_id,receive_time,item_id,item_count,sender) VALUES(?,?,?,?,?)");
			statement.setInt(1, owner.getObjectId());
			statement.setInt(2, item.getReceiveTime());
			statement.setInt(3, item.getItemId());
			statement.setLong(4, item.getItemCount());
			statement.setString(5, item.getSender());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn(owner.getBlockList() + " could not add item to premium item list: " + item, e);
			return false;
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		return true;
	}

	public boolean delete(Player owner, PremiumItem item)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_premium_items WHERE char_id = ? AND receive_time = ? AND item_id = ? AND item_count = ? AND sender = ?");
			statement.setInt(1, owner.getObjectId());
			statement.setInt(2, item.getReceiveTime());
			statement.setInt(3, item.getItemId());
			statement.setLong(4, item.getItemCount());
			statement.setString(5, item.getSender());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn(owner.getBlockList() + " could not delete item: " + item + " ownerId: " + owner.getObjectId(), e);
			return false;
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		return true;
	}

	public boolean update(Player owner, PremiumItem item, long count)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE character_premium_items SET item_count = ? WHERE char_id = ? AND receive_time = ? AND item_id = ? AND item_count = ? AND sender = ?");
			statement.setLong(1, count);
			statement.setInt(2, owner.getObjectId());
			statement.setInt(3, item.getReceiveTime());
			statement.setInt(4, item.getItemId());
			statement.setLong(5, item.getItemCount());
			statement.setString(6, item.getSender());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn(owner.getBlockList() + " could not update item: " + item, e);
			return false;
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		return true;
	}
}