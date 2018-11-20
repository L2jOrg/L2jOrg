package org.l2j.gameserver.taskmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.l2j.commons.dbutils.DbUtils;
import org.l2j.commons.threading.RunnableImpl;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.data.xml.holder.ItemHolder;
import org.l2j.gameserver.database.DatabaseFactory;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.ItemInstance.ItemLocation;
import org.l2j.gameserver.model.items.PcInventory;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.utils.ItemFunctions;
import org.l2j.gameserver.utils.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayedItemsManager extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(DelayedItemsManager.class);
	private static DelayedItemsManager _instance;

	private static final Object _lock = new Object();
	private int last_payment_id = 0;

	public static DelayedItemsManager getInstance()
	{
		if(_instance == null)
			_instance = new DelayedItemsManager();
		return _instance;
	}

	public DelayedItemsManager()
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			last_payment_id = get_last_payment_id(con);
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con);
		}

		ThreadPoolManager.getInstance().schedule(this, 10000L);
	}

	private int get_last_payment_id(Connection con)
	{
		PreparedStatement st = null;
		ResultSet rset = null;
		int result = last_payment_id;
		try
		{
			st = con.prepareStatement("SELECT MAX(payment_id) AS last FROM items_delayed");
			rset = st.executeQuery();
			if(rset.next())
				result = rset.getInt("last");
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(st, rset);
		}
		return result;
	}

	@Override
	public void runImpl() throws Exception
	{
		Player player = null;

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			int last_payment_id_temp = get_last_payment_id(con);
			if(last_payment_id_temp != last_payment_id)
				synchronized (_lock)
				{
					st = con.prepareStatement("SELECT DISTINCT owner_id FROM items_delayed WHERE payment_status=0 AND payment_id > ?");
					st.setInt(1, last_payment_id);
					rset = st.executeQuery();
					while(rset.next())
						if((player = GameObjectsStorage.getPlayer(rset.getInt("owner_id"))) != null)
							loadDelayed(player, true);
					last_payment_id = last_payment_id_temp;
				}
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, st, rset);
		}

		ThreadPoolManager.getInstance().schedule(this, 10000L);
	}

	public static void addDelayed(int objectId, int itemId, long itemCount, int enchant, String desc)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO items_delayed (owner_id, item_id, count, enchant_level, description) VALUES (?, ?, ?, ?, ?)");
			statement.setInt(1, objectId);
			statement.setInt(2, itemId);
			statement.setLong(3, itemCount);
			statement.setInt(4, enchant);
			statement.setString(5, desc);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.info("DelayedItemsManager.addDelayed(int, int, long): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public int loadDelayed(Player player, boolean notify)
	{
		if(player == null)
			return 0;
		final int player_id = player.getObjectId();
		final PcInventory inv = player.getInventory();
		if(inv == null)
			return 0;

		int restored_counter = 0;

		Connection con = null;
		PreparedStatement st = null, st_delete = null;
		ResultSet rset = null;
		synchronized (_lock)
		{
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				st = con.prepareStatement("SELECT * FROM items_delayed WHERE owner_id=? AND payment_status=0");
				st.setInt(1, player_id);
				rset = st.executeQuery();

				ItemInstance item, newItem;
				st_delete = con.prepareStatement("UPDATE items_delayed SET payment_status=1 WHERE payment_id=?");

				while(rset.next())
				{
					final int ITEM_ID = rset.getInt("item_id");
					final int PAYMENT_ID = rset.getInt("payment_id");
					final ItemTemplate ITEM_TEMPLATE = ItemHolder.getInstance().getTemplate(ITEM_ID);
					if(ITEM_TEMPLATE != null)
					{
						final long ITEM_COUNT = rset.getLong("count");
						final int ITEM_ENCHANT = rset.getInt("enchant_level");
						final int FLAGS = rset.getInt("flags");
						final int ATTRIBUTE = rset.getInt("attribute");
						final int ATTRIBUTE_LEVEL = rset.getInt("attribute_level");
						final String DESCRIPTION = rset.getString("description");
						boolean stackable = ITEM_TEMPLATE.isStackable();
						boolean success = false;

						for(int i = 0; i < (stackable ? 1 : ITEM_COUNT); i++)
						{
							if(ITEM_COUNT > 0)
							{
								item = ItemFunctions.createItem(ITEM_ID);
								if(item.isStackable())
									item.setCount(ITEM_COUNT);
								else
									item.setEnchantLevel(ITEM_ENCHANT);
								//FIXME [G1ta0] item-API
								//item.setAttributeElement(ATTRIBUTE, ATTRIBUTE_LEVEL, true);
								item.setLocation(ItemLocation.INVENTORY);
								item.setCustomFlags(FLAGS);

								newItem = inv.addItem(item);
								if(newItem == null)
								{
									_log.warn("Unable to delayed create item " + ITEM_ID + " request " + PAYMENT_ID);
									continue;
								}

								if(notify)
									player.sendPacket(SystemMessagePacket.obtainItems(ITEM_ID, stackable ? ITEM_COUNT : 1, ITEM_ENCHANT));

								Log.LogItem(player, Log.DelayedItemReceive, newItem, ITEM_COUNT, DESCRIPTION);
							}

							success = true;
							restored_counter++;
						}
						if(!success)
							continue;
					}

					st_delete.setInt(1, PAYMENT_ID);
					st_delete.execute();
				}
			}
			catch(Exception e)
			{
				_log.error("Could not load delayed items for player " + player + "!", e);
			}
			finally
			{
				DbUtils.closeQuietly(st_delete);
				DbUtils.closeQuietly(con, st, rset);
			}
		}
		return restored_counter;
	}
}