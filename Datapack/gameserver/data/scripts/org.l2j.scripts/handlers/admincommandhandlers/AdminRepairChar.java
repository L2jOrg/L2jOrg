/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.data.database.dao.ShortcutDAO;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * This class handles following admin commands: - delete = deletes target
 * @version $Revision: 1.1.2.6.2.3 $ $Date: 2005/04/11 10:05:59 $
 */
public class AdminRepairChar implements IAdminCommandHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminRepairChar.class);
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_restore",
		"admin_repair"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		handleRepair(command);
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void handleRepair(String command)
	{
		final String[] parts = command.split(" ");
		if (parts.length != 2)
		{
			return;
		}
		
		final String cmd = "UPDATE characters SET x=-84318, y=244579, z=-3730 WHERE char_name=?";
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(cmd);
			statement.setString(1, parts[1]);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("SELECT charId FROM characters where char_name=?");
			statement.setString(1, parts[1]);
			final ResultSet rset = statement.executeQuery();
			int objId = 0;
			if (rset.next())
			{
				objId = rset.getInt(1);
			}
			
			rset.close();
			statement.close();
			
			if (objId == 0)
			{
				con.close();
				return;
			}

			getDAO(ShortcutDAO.class).deleteAll(objId);

			statement = con.prepareStatement("UPDATE items SET loc='INVENTORY' WHERE owner_id=?");
			statement.setInt(1, objId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			LOGGER.warn("could not repair char:", e);
		}
	}
}
