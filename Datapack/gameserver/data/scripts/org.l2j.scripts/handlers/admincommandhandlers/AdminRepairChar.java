package handlers.admincommandhandlers;

import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.data.database.dao.ShortcutDAO;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * This class handles following admin commands: - delete = deletes target
 * @version $Revision: 1.1.2.6.2.3 $ $Date: 2005/04/11 10:05:59 $
 */
public class AdminRepairChar implements IAdminCommandHandler {
	
	private static final String[] ADMIN_COMMANDS = {
		"admin_restore",
		"admin_repair"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar) {
		handleRepair(command);
		return true;
	}

	private void handleRepair(String command) {
		final String[] parts = command.split(" ");
		if (parts.length != 2) {
			return;
		}
		var name = parts[1];

		var objectId = PlayerNameTable.getInstance().getIdByName(name);
		getDAO(PlayerDAO.class).updateToValidLocation(objectId);
		getDAO(ShortcutDAO.class).deleteAll(objectId);
		getDAO(ItemDAO.class).updateToInventory(objectId);
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
