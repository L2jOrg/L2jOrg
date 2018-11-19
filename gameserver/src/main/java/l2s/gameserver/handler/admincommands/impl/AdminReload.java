package l2s.gameserver.handler.admincommands.impl;

import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.dao.OlympiadParticipantsDAO;
import l2s.gameserver.data.string.ItemNameHolder;
import l2s.gameserver.data.string.SkillNameHolder;
import l2s.gameserver.data.string.StringsHolder;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.xml.parser.*;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.olympiad.OlympiadDatabase;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.utils.Strings;

public class AdminReload implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_reload,
		admin_reload_config,
		admin_reload_multisell,
		admin_reload_gmaccess,
		admin_reload_htm,
		admin_reload_qs,
		admin_reload_qs_help,
		admin_reload_skills,
		admin_reload_npc,
		admin_reload_spawn,
		admin_reload_fish,
		admin_reload_abuse,
		admin_reload_translit,
		admin_reload_shops,
		admin_reload_static,
		admin_reload_pets,
		admin_reload_locale,
		admin_reload_nobles,
		admin_reload_im,
		admin_reload_events
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanReload)
			return false;

		switch(command)
		{
			case admin_reload:
				break;
			case admin_reload_config:
			{
				try
				{
					Config.load();
				}
				catch(Exception e)
				{
					activeChar.sendMessage("Error: " + e.getMessage() + "!");
					return false;
				}
				activeChar.sendMessage("Config reloaded!");
				break;
			}
			case admin_reload_multisell:
			{
				MultiSellParser.getInstance().reload();
				activeChar.sendMessage("Multisell list reloaded!");
				break;
			}
			case admin_reload_gmaccess:
			{
				try
				{
					Config.loadGMAccess();
					for(Player player : GameObjectsStorage.getPlayers())
						if(!Config.EVERYBODY_HAS_ADMIN_RIGHTS)
							player.setPlayerAccess(Config.gmlist.get(player.getObjectId()));
						else
							player.setPlayerAccess(Config.gmlist.get(new Integer(0)));
				}
				catch(Exception e)
				{
					return false;
				}
				activeChar.sendMessage("GMAccess reloaded!");
				break;
			}
			case admin_reload_htm:
			{
				HtmCache.getInstance().reload();
				activeChar.sendMessage("HTML cache clearned.");
				break;
			}
			case admin_reload_qs:
			{
				if(fullString.endsWith("all"))
					for(Player p : GameObjectsStorage.getPlayers())
						reloadQuestStates(p);
				else
				{
					GameObject t = activeChar.getTarget();

					if(t != null && t.isPlayer())
					{
						Player p = (Player) t;
						reloadQuestStates(p);
					}
					else
						reloadQuestStates(activeChar);
				}
				break;
			}
			case admin_reload_qs_help:
			{
				activeChar.sendMessage("");
				activeChar.sendMessage("Quest Help:");
				activeChar.sendMessage("reload_qs_help - This Message.");
				activeChar.sendMessage("reload_qs <selected target> - reload all quest states for target.");
				activeChar.sendMessage("reload_qs <no target or target is not player> - reload quests for self.");
				activeChar.sendMessage("reload_qs all - reload quests for all players in world.");
				activeChar.sendMessage("");
				break;
			}
			case admin_reload_skills:
			{
				SkillParser.getInstance().reload();
				break;
			}
			case admin_reload_npc:
			{
				NpcParser.getInstance().reload();
				break;
			}
			case admin_reload_spawn:
			{
				ThreadPoolManager.getInstance().execute(() -> SpawnManager.getInstance().reloadAll());
				break;
			}
			case admin_reload_fish:
			{
				FishDataParser.getInstance().reload();
				break;
			}
			case admin_reload_abuse:
			{
				Config.abuseLoad();
				break;
			}
			case admin_reload_translit:
			{
				Strings.reload();
				break;
			}
			case admin_reload_shops:
			{
				BuyListParser.getInstance().reload();
				break;
			}
			case admin_reload_static:
			{
				//StaticObjectsTable.getInstance().reloadStaticObjects();
				break;
			}
			case admin_reload_pets:
			{
				PetDataParser.getInstance().reload();
				break;
			}
			case admin_reload_locale:
			{
				ItemNameHolder.getInstance().reload();
				SkillNameHolder.getInstance().reload();
				StringsHolder.getInstance().reload();
				break;
			}
			case admin_reload_nobles:
			{
				OlympiadParticipantsDAO.getInstance().select();
				OlympiadDatabase.loadParticipantsRank();
				break;
			}
			case admin_reload_im:
			{
				ProductDataParser.getInstance().reload();
				break;
			}
			case admin_reload_events:
			{
				EventHolder.getInstance().clear();
				EventParser.getInstance().load();
				activeChar.sendMessage("Events Reloaded!");
				break;
			}
		}
		activeChar.sendPacket(new HtmlMessage(5).setFile("admin/reload.htm"));
		return true;
	}

	private void reloadQuestStates(Player p)
	{
		for(QuestState qs : p.getAllQuestsStates())
			p.removeQuestState(qs.getQuest());
		Quest.restoreQuestStates(p);
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}