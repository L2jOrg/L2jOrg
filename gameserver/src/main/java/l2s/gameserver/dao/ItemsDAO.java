package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import l2s.commons.dao.JdbcDAO;
import l2s.commons.dao.JdbcEntityState;
import l2s.commons.dao.JdbcEntityStats;
import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.ItemInstance.ItemLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemsDAO implements JdbcDAO<Integer, ItemInstance>
{
	private static final Logger _log = LoggerFactory.getLogger(ItemsDAO.class);

	private final static String RESTORE_ITEM = "SELECT object_id, owner_id, item_id, count, enchant_level, loc, loc_data, custom_type1, custom_type2, life_time, custom_flags, variation_stone_id, variation1_id, variation2_id FROM items WHERE object_id = ?";
	private final static String RESTORE_OWNER_ITEMS = "SELECT object_id FROM items WHERE owner_id = ? AND loc = ?";
	private final static String STORE_ITEM = "INSERT INTO items (object_id, owner_id, item_id, count, enchant_level, loc, loc_data, custom_type1, custom_type2, life_time, custom_flags, variation_stone_id, variation1_id, variation2_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private final static String UPDATE_ITEM = "UPDATE items SET owner_id = ?, item_id = ?, count = ?, enchant_level = ?, loc = ?, loc_data = ?, custom_type1 = ?, custom_type2 = ?, life_time = ?, custom_flags = ?, variation_stone_id = ?, variation1_id = ?, variation2_id = ? WHERE object_id = ?";
	private final static String REMOVE_ITEM = "DELETE FROM items WHERE object_id = ?";
	private final static String INSERT_GLOBAL_REMOVE_ITEM = "REPLACE INTO items_to_delete (item_id,description) VALUES (?,?)";

	private final static ItemsDAO instance = new ItemsDAO();

	public final static ItemsDAO getInstance()
	{
		return instance;
	}

	private AtomicLong load = new AtomicLong();
	private AtomicLong insert = new AtomicLong();
	private AtomicLong update = new AtomicLong();
	private AtomicLong delete = new AtomicLong();

	private final Cache cache;

	private final JdbcEntityStats stats = new JdbcEntityStats(){
		@Override
		public long getLoadCount()
		{
			return load.get();
		}

		@Override
		public long getInsertCount()
		{
			return insert.get();
		}

		@Override
		public long getUpdateCount()
		{
			return update.get();
		}

		@Override
		public long getDeleteCount()
		{
			return delete.get();
		}
	};

	private ItemsDAO()
	{
		cache = CacheManager.getInstance().getCache(ItemInstance.class.getName());
	}

	public Cache getCache()
	{
		return cache;
	}

	@Override
	public JdbcEntityStats getStats()
	{
		return stats;
	}

	private ItemInstance load0(int objectId) throws SQLException
	{
		ItemInstance item = null;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(RESTORE_ITEM);
			statement.setInt(1, objectId);
			rset = statement.executeQuery();
			item = load0(rset);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		load.incrementAndGet();
		return item;
	}

	private ItemInstance load0(ResultSet rset) throws SQLException
	{
		ItemInstance item = null;

		if(rset.next())
		{
			int objectId = rset.getInt(1);
			item = new ItemInstance(objectId);
			//item.setObjectId(rset.getInt(1));
			item.setOwnerId(rset.getInt(2));
			item.setItemId(rset.getInt(3));
			item.setCount(rset.getLong(4));
			item.setEnchantLevel(rset.getInt(5));
			item.setLocName(rset.getString(6));
			item.setLocData(rset.getInt(7));
			item.setCustomType1(rset.getInt(8));
			item.setCustomType2(rset.getInt(9));
			item.setLifeTime(rset.getInt(10));
			item.setCustomFlags(rset.getInt(11));
			item.setVariationStoneId(rset.getInt(12));
			item.setVariation1Id(rset.getInt(13));
			item.setVariation2Id(rset.getInt(14));
			item.restoreEnsoul();
		}

		return item;
	}

	private void save0(ItemInstance item, PreparedStatement statement) throws SQLException
	{
		statement.setInt(1, item.getObjectId());
		statement.setInt(2, item.getOwnerId());
		statement.setInt(3, item.getItemId());
		statement.setLong(4, item.getCount());
		statement.setInt(5, item.getEnchantLevel());
		statement.setString(6, item.getLocName());
		statement.setInt(7, item.getLocData());
		statement.setInt(8, item.getCustomType1());
		statement.setInt(9, item.getCustomType2());
		statement.setInt(10, item.getLifeTime());
		statement.setInt(11, item.getCustomFlags());
		statement.setInt(12, item.getVariationStoneId());
		statement.setInt(13, item.getVariation1Id());
		statement.setInt(14, item.getVariation2Id());
	}

	private void save0(ItemInstance item) throws SQLException
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(STORE_ITEM);
			save0(item, statement);
			statement.execute();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}

		insert.incrementAndGet();
	}

	private void delete0(ItemInstance item, PreparedStatement statement) throws SQLException
	{
		statement.setInt(1, item.getObjectId());
	}

	private void delete0(ItemInstance item) throws SQLException
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(REMOVE_ITEM);
			delete0(item, statement);
			statement.execute();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}

		delete.incrementAndGet();
	}

	private void update0(ItemInstance item, PreparedStatement statement) throws SQLException
	{
		statement.setInt(14, item.getObjectId());
		statement.setInt(1, item.getOwnerId());
		statement.setInt(2, item.getItemId());
		statement.setLong(3, item.getCount());
		statement.setInt(4, item.getEnchantLevel());
		statement.setString(5, item.getLocName());
		statement.setInt(6, item.getLocData());
		statement.setInt(7, item.getCustomType1());
		statement.setInt(8, item.getCustomType2());
		statement.setInt(9, item.getLifeTime());
		statement.setInt(10, item.getCustomFlags());
		statement.setInt(11, item.getVariationStoneId());
		statement.setInt(12, item.getVariation1Id());
		statement.setInt(13, item.getVariation2Id());
	}

	private void update0(ItemInstance item) throws SQLException
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(UPDATE_ITEM);
			update0(item, statement);
			statement.execute();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}

		update.incrementAndGet();
	}

	@Override
	public ItemInstance load(Integer objectId)
	{
		ItemInstance item;

		Element ce = cache.get(objectId);
		if(ce != null)
		{
			item = (ItemInstance) ce.getObjectValue();
			return item;
		}

		try
		{
			item = load0(objectId);
			if(item == null)
				return null;

			item.setJdbcState(JdbcEntityState.STORED);
		}
		catch(SQLException e)
		{
			_log.error("Error while restoring item : " + objectId, e);
			return null;
		}

		cache.put(new Element(item.getObjectId(), item));

		return item;
	}

	public Collection<ItemInstance> load(Collection<Integer> objectIds)
	{
		Collection<ItemInstance> list = Collections.emptyList();

		if(objectIds.isEmpty())
			return list;

		list = new ArrayList<ItemInstance>(objectIds.size());

		ItemInstance item;
		for(Integer objectId : objectIds)
		{
			item = load(objectId);
			if(item != null)
				list.add(item);
		}

		return list;
	}

	@Override
	public void save(ItemInstance item)
	{
		if(!item.getJdbcState().isSavable())
			return;

		try
		{
			save0(item);
			item.setJdbcState(JdbcEntityState.STORED);
		}
		catch(SQLException e)
		{
			_log.error("Error while saving item : " + item, e);
			return;
		}

		cache.put(new Element(item.getObjectId(), item));
	}

	public void save(Collection<ItemInstance> items)
	{
		if(items.isEmpty())
			return;

		for(ItemInstance item : items)
			save(item);
	}

	@Override
	public void update(ItemInstance item)
	{
		if(!item.getJdbcState().isUpdatable())
			return;

		try
		{
			update0(item);
			item.setJdbcState(JdbcEntityState.STORED);
		}
		catch(SQLException e)
		{
			_log.error("Error while updating item : " + item, e);
			return;
		}

		cache.putIfAbsent(new Element(item.getObjectId(), item));
	}

	public void update(Collection<ItemInstance> items)
	{
		if(items.isEmpty())
			return;

		for(ItemInstance item : items)
			update(item);
	}

	@Override
	public void saveOrUpdate(ItemInstance item)
	{
		if(item.getJdbcState().isSavable())
			save(item);
		else if(item.getJdbcState().isUpdatable())
			update(item);
	}

	public void saveOrUpdate(Collection<ItemInstance> items)
	{
		if(items.isEmpty())
			return;

		for(ItemInstance item : items)
			saveOrUpdate(item);
	}

	@Override
	public void delete(ItemInstance item)
	{
		if(!item.getJdbcState().isDeletable())
			return;

		try
		{
			delete0(item);
			item.setJdbcState(JdbcEntityState.DELETED);
		}
		catch(SQLException e)
		{
			_log.error("Error while deleting item : " + item, e);
			return;
		}

		cache.remove(item.getObjectId());
	}

	public void delete(Collection<ItemInstance> items)
	{
		if(items.isEmpty())
			return;

		for(ItemInstance item : items)
			delete(item);
	}

	public Collection<ItemInstance> getItemsByOwnerIdAndLoc(int ownerId, ItemLocation loc)
	{
		Collection<Integer> objectIds = Collections.emptyList();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(RESTORE_OWNER_ITEMS);
			statement.setInt(1, ownerId);
			statement.setString(2, loc.name());
			rset = statement.executeQuery();
			objectIds = new ArrayList<Integer>();
			while(rset.next())
				objectIds.add(rset.getInt(1));
		}
		catch(SQLException e)
		{
			_log.error("Error while restore items of owner : " + ownerId, e);
			objectIds.clear();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return load(objectIds);
	}

	public void glovalRemoveItem(int itemId, String description)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_GLOBAL_REMOVE_ITEM);
			statement.setInt(1, itemId);
			statement.setString(2, description);
			statement.execute();
		}
		catch(SQLException e)
		{
			_log.error("Error while global remove item: " + itemId + "(" + description + ")", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}