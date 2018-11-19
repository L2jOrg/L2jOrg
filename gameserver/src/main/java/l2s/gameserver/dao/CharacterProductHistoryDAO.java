package l2s.gameserver.dao;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.data.xml.holder.ProductDataHolder;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.ProductHistoryItem;
import l2s.gameserver.templates.item.product.ProductItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 */
public class CharacterProductHistoryDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CharacterProductHistoryDAO.class);

	private static final CharacterProductHistoryDAO _instance = new CharacterProductHistoryDAO();

	public static CharacterProductHistoryDAO getInstance()
	{
		return _instance;
	}

	public TIntObjectMap<ProductHistoryItem> select(Player owner)
	{
		TIntObjectMap<ProductHistoryItem> map = new TIntObjectHashMap<ProductHistoryItem>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT product_id, last_purchase_time FROM character_product_history WHERE char_id = ?");
			statement.setInt(1, owner.getObjectId());
			rset = statement.executeQuery();
			while(rset.next())
			{
				int product_id = rset.getInt("product_id");
				int last_purchase_time = rset.getInt("last_purchase_time");

				ProductItem product = ProductDataHolder.getInstance().getProduct(product_id);
				if(product == null)
					continue;

				map.put(product_id, new ProductHistoryItem(product, last_purchase_time));
			}
		}
		catch(Exception e)
		{
			_log.error("CharacterProductHistoryDAO.select(Player): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return map;
	}

	public boolean replace(Player owner, ProductHistoryItem item)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO character_product_history (char_id,product_id,last_purchase_time) VALUES(?,?,?)");
			statement.setInt(1, owner.getObjectId());
			statement.setInt(2, item.getProduct().getId());
			statement.setInt(3, item.getLastPurchaseTime());
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
}