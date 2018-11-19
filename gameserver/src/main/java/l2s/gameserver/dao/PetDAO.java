package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.items.ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 */
public class PetDAO
{
	private static final Logger _log = LoggerFactory.getLogger(PetDAO.class);
	private static final PetDAO _instance = new PetDAO();

	public static final String SELECT_SQL_QUERY = "SELECT objId FROM pets WHERE item_obj_id=?";
	public static final String DELETE_SQL_QUERY = "DELETE FROM pets WHERE item_obj_id=?";

	public static PetDAO getInstance()
	{
		return _instance;
	}

	public void deletePet(ItemInstance item, Creature owner)
	{
		int petObjectId = 0;

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			int itemObjId = item.getObjectId();
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			statement.setInt(1, itemObjId);
			rset = statement.executeQuery();
			while(rset.next())
				petObjectId = rset.getInt("objId");

			DbUtils.close(statement, rset);

			Player player = owner.getPlayer();

			PetInstance pet = player.getPet();
			if(pet != null && pet.getObjectId() == petObjectId)
				pet.unSummon(false);

			if(player != null && player.isMounted() && player.getMountControlItemObjId() == itemObjId)
				player.getMount().onControlItemDelete();

			// if it's a pet control item, delete the pet
			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setInt(1, itemObjId);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("CharNameTable.deletePet(ItemInstance, Creature): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}
}