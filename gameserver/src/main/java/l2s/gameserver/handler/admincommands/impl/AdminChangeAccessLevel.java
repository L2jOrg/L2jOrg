package l2s.gameserver.handler.admincommands.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.dao.CharacterVariablesDAO;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.database.mysql;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.CharacterVariable;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.utils.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * changelvl - Изменение уровня доступа
 * moders - Панель управления модераторами
 * moders_add - Добавление модератора
 * moders_del - Удаление модератора
 * penalty - Штраф за некорректное модерирование
 */
public class AdminChangeAccessLevel implements IAdminCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(AdminChangeAccessLevel.class);

	private static enum Commands
	{
		admin_changelvl,
		admin_moders,
		admin_moders_add,
		admin_moders_del,
		admin_penalty
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanGmEdit)
			return false;

		switch(command)
		{
			case admin_changelvl:
				if(wordList.length == 2)
				{
					int lvl = Integer.parseInt(wordList[1]);
					if(activeChar.getTarget().isPlayer())
						((Player) activeChar.getTarget()).setAccessLevel(lvl);
				}
				else if(wordList.length == 3)
				{
					int lvl = Integer.parseInt(wordList[2]);
					Player player = GameObjectsStorage.getPlayer(wordList[1]);
					if(player != null)
						player.setAccessLevel(lvl);
				}
				break;
			case admin_moders:
				// Панель управления модераторами
				showModersPannel(activeChar);
				break;
			case admin_moders_add:
				if(activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()/* || activeChar.getTarget() == activeChar*/)
				{
					activeChar.sendMessage("Incorrect target. Please select a player.");
					showModersPannel(activeChar);
					return false;
				}

				Player modAdd = activeChar.getTarget().getPlayer();
				if(Config.gmlist.containsKey(modAdd.getObjectId()))
				{
					activeChar.sendMessage("Error: Moderator " + modAdd.getName() + " already in server access list.");
					showModersPannel(activeChar);
					return false;
				}

				// Копируем файл с привилегиями модератора
				String newFName = "m" + modAdd.getObjectId() + ".xml";
				if(!Files.copyFile(Config.GM_ACCESS_FILES_DIR + "template/moderator.xml", Config.GM_ACCESS_FILES_DIR + newFName))
				{
					activeChar.sendMessage("Error: Failed to copy access-file.");
					showModersPannel(activeChar);
					return false;
				}

				// Замена objectId
				String res = "";
				try
				{
					BufferedReader in = new BufferedReader(new FileReader(Config.GM_ACCESS_FILES_DIR + newFName));
					String str;
					while((str = in.readLine()) != null)
						res += str + "\n";
					in.close();

					res = res.replaceFirst("ObjIdPlayer", "" + modAdd.getObjectId());
					Files.writeFile(Config.GM_ACCESS_FILES_DIR + newFName, res);
				}
				catch(Exception e)
				{
					activeChar.sendMessage("Error: Failed to modify object ID in access-file.");
					File fDel = new File(Config.GM_ACCESS_FILES_DIR + newFName);
					if(fDel.exists())
						fDel.delete();
					showModersPannel(activeChar);
					return false;
				}

				// Устанавливаем права модератору
				File af = new File(Config.GM_ACCESS_FILES_DIR + newFName);
				if(!af.exists())
				{
					activeChar.sendMessage("Error: Failed to read access-file for " + modAdd.getName());
					showModersPannel(activeChar);
					return false;
				}

				Config.loadGMAccess(af);
				modAdd.setPlayerAccess(Config.gmlist.get(modAdd.getObjectId()));

				activeChar.sendMessage("Moderator " + modAdd.getName() + " added.");
				showModersPannel(activeChar);
				break;
			case admin_moders_del:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("Please specify moderator object ID to delete moderator.");
					showModersPannel(activeChar);
					return false;
				}

				int oid = Integer.parseInt(wordList[1]);

				// Удаляем права из серверного списка
				if(Config.gmlist.containsKey(oid))
					Config.gmlist.remove(oid);
				else
				{
					activeChar.sendMessage("Error: Moderator with object ID " + oid + " not found in server access lits.");
					showModersPannel(activeChar);
					return false;
				}

				// Если удаляемый модератор онлайн, то отбираем у него права на ходу
				Player modDel = GameObjectsStorage.getPlayer(oid);
				if(modDel != null)
					modDel.setPlayerAccess(null);

				// Удаляем файл с правами
				String fname = "m" + oid + ".xml";
				File f = new File(Config.GM_ACCESS_FILES_DIR + fname);
				if(!f.exists() || !f.isFile() || !f.delete())
				{
					activeChar.sendMessage("Error: Can't delete access-file: " + fname);
					showModersPannel(activeChar);
					return false;
				}

				if(modDel != null)
					activeChar.sendMessage("Moderator " + modDel.getName() + " deleted.");
				else
					activeChar.sendMessage("Moderator with object ID " + oid + " deleted.");

				showModersPannel(activeChar);
				break;
			case admin_penalty:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //penalty charName [count] [reason]");
					return false;
				}

				int count = 1;
				if(wordList.length > 2)
					count = Integer.parseInt(wordList[2]);

				String reason = "не указана";

				if(wordList.length > 3)
					reason = wordList[3];

				int oId = 0;

				Player player = GameObjectsStorage.getPlayer(wordList[1]);
				if(player != null && player.getPlayerAccess().CanBanChat)
				{
					oId = player.getObjectId();
					int oldPenaltyCount = 0;
					String oldPenalty = player.getVar("penaltyChatCount");
					if(oldPenalty != null)
						oldPenaltyCount = Integer.parseInt(oldPenalty);

					player.setVar("penaltyChatCount", "" + (oldPenaltyCount + count), -1);
				}
				else
				{
					// TODO: Не плохо было бы сделать сперва проверку, модератор это или нет.
					oId = mysql.simple_get_int("obj_Id", "characters", "`char_name`='" + wordList[1] + "'");
					if(oId > 0)
					{
						String val = CharacterVariablesDAO.getInstance().getVarFromPlayer(oId, "penaltyChatCount");
						int oldCount = 0;
						if(val != null && !val.isEmpty())
							oldCount = Integer.parseInt(val);

						CharacterVariablesDAO.getInstance().insert(oId, new CharacterVariable("penaltyChatCount", String.valueOf(oldCount + count), -1));
					}
				}

				if(oId > 0)
					if(Config.BANCHAT_ANNOUNCE_FOR_ALL_WORLD)
						Announcements.announceToAll(activeChar + " оштрафовал модератора " + wordList[1] + " на " + count + ", причина: " + reason + ".");
					else
						Announcements.shout(activeChar, activeChar + " оштрафовал модератора " + wordList[1] + " на " + count + ", причина: " + reason + ".", ChatType.CRITICAL_ANNOUNCE);

				break;
		}

		return true;
	}

	// Панель управления модераторами
	private static void showModersPannel(Player activeChar)
	{
		HtmlMessage reply = new HtmlMessage(5);
		String html = "Moderators managment panel.<br>";

		File dir = new File(Config.GM_ACCESS_FILES_DIR);
		if(!dir.exists() || !dir.isDirectory())
		{
			html += "Error: Can't open permissions folder.";
			reply.setHtml(html);
			activeChar.sendPacket(reply);
			return;
		}

		html += "<p align=right>";
		html += "<button width=120 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h admin_moders_add\" value=\"Add modrator\">";
		html += "</p><br>";

		html += "<center><font color=LEVEL>Moderators:</font></center>";
		html += "<table width=285>";
		for(File f : dir.listFiles())
		{
			if(f.isDirectory() || !f.getName().startsWith("m") || !f.getName().endsWith(".xml"))
				continue;

			// Для файлов модераторов префикс m
			int oid = Integer.parseInt(f.getName().substring(1, 10));
			String pName = getPlayerNameByObjId(oid);
			boolean on = false;

			if(pName == null || pName.isEmpty())
				pName = "" + oid;
			else
				on = GameObjectsStorage.getPlayer(pName) != null;

			html += "<tr>";
			html += "<td width=140>" + pName;
			html += on ? " <font color=\"33CC66\">(on)</font>" : "";
			html += "</td>";
			html += "<td width=45><button width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h admin_moders_log " + oid + "\" value=\"Logs\"></td>";
			html += "<td width=45><button width=20 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h admin_moders_del " + oid + "\" value=\"X\"></td>";
			html += "</tr>";
		}
		html += "</table>";

		reply.setHtml(html);
		activeChar.sendPacket(reply);
	}

	private static String getPlayerNameByObjId(int oid)
	{
		String pName = null;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT `char_name` FROM `characters` WHERE `obj_Id`=\"" + oid + "\" LIMIT 1");
			rset = statement.executeQuery();
			if(rset.next())
				pName = rset.getString(1);
		}
		catch(Exception e)
		{
			_log.warn("SQL Error: " + e);
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return pName;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}