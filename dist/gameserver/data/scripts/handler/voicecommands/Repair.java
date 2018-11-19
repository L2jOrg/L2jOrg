package handler.voicecommands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;

import l2s.commons.dao.JdbcEntityState;
import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.dao.CharacterVariablesDAO;
import l2s.gameserver.dao.ItemsDAO;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.ItemInstance.ItemLocation;
import l2s.gameserver.network.l2.components.CustomMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Repair extends ScriptVoiceCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(Repair.class);

	private final String[] COMMANDS = new String[] { "repair" };

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if(!target.isEmpty())
		{
			if(activeChar.getName().equalsIgnoreCase(target))
			{
				activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.YouCantRepairYourself"));
				return false;
			}

			int objId = 0;

			for(Map.Entry<Integer, String> e : activeChar.getAccountChars().entrySet())
			{
				if(e.getValue().equalsIgnoreCase(target))
				{
					objId = e.getKey();
					break;
				}
			}

			if(objId == 0)
			{
				activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.YouCanRepairOnlyOnSameAccount"));
				return false;
			}
			else if(World.getPlayer(objId) != null)
			{
				activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.CharIsOnline"));
				return false;
			}

			Connection con = null;
			PreparedStatement statement = null;
			ResultSet rs = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("SELECT karma FROM characters WHERE obj_Id=?");
				statement.setInt(1, objId);
				statement.execute();
				rs = statement.getResultSet();

				int karma = 0;

				rs.next();

				karma = rs.getInt("karma");

				DbUtils.close(statement, rs);

				if(karma > 0)
				{
					statement = con.prepareStatement("UPDATE characters SET x=17144, y=170156, z=-3502 WHERE obj_Id=?");
					statement.setInt(1, objId);
					statement.execute();
					DbUtils.close(statement);
				}
				else
				{
					statement = con.prepareStatement("UPDATE characters SET x=-119664, y=246306, z=-1232 WHERE obj_Id=?");
					statement.setInt(1, objId);
					statement.execute();
					DbUtils.close(statement);

					Collection<ItemInstance> items = ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(objId, ItemLocation.PAPERDOLL);
					for(ItemInstance item : items)
					{
						item.setEquipped(false);
						item.setLocData(0);
						item.setLocation(ItemLocation.INVENTORY);
						item.setJdbcState(JdbcEntityState.UPDATED);
						item.update();
					}
				}

				CharacterVariablesDAO.getInstance().delete(objId, "reflection");

				activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.RepairDone"));
				return true;
			}
			catch(Exception e)
			{
				_log.error("", e);
				return false;
			}
			finally
			{
				DbUtils.closeQuietly(con, statement, rs);
			}
		}
		else
			activeChar.sendMessage(".repair <name>");

		return false;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}