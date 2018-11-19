package l2s.gameserver.handler.admincommands.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.dao.CharacterVariablesDAO;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.Player;

@SuppressWarnings("unused")
public class AdminRepairChar implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_restore,
		admin_repair
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(activeChar.getPlayerAccess() == null || !activeChar.getPlayerAccess().CanEditChar)
			return false;

		if(wordList.length != 2)
			return false;

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		int objId = 0;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET x=-84318, y=244579, z=-3730 WHERE char_name=?");
			statement.setString(1, wordList[1]);
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement("SELECT obj_id FROM characters where char_name=?");
			statement.setString(1, wordList[1]);
			rset = statement.executeQuery();
			if(rset.next())
				objId = rset.getInt(1);

			DbUtils.close(statement, rset);

			if(objId == 0)
				return false;

			// con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=?");
			statement.setInt(1, objId);
			statement.execute();
			DbUtils.close(statement);

			// con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE items SET loc='INVENTORY' WHERE owner_id=? AND loc!='WAREHOUSE'");
			statement.setInt(1, objId);
			statement.execute();
			DbUtils.close(statement);
		}
		catch(Exception e)
		{

		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		CharacterVariablesDAO.getInstance().delete(objId, "reflection");

		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}