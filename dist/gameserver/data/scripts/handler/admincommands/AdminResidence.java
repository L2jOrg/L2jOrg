package handler.admincommands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import l2s.commons.dao.JdbcEntityState;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.dao.SiegeClanDAO;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.handler.admincommands.AdminCommandHandler;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.entity.events.objects.SiegeClanObject;
import l2s.gameserver.model.entity.events.objects.SpawnExObject;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.entity.residence.ResidenceSide;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.tables.ClanTable;
import l2s.gameserver.utils.HtmlUtils;

/**
 * @author VISTALL
 * @date 15:10/06.03.2011
 */
public class AdminResidence extends ScriptAdminCommand
{
	private static enum Commands
	{
		admin_residence_list,
		admin_residence,
		admin_set_owner,
		admin_set_siege_time,
		//
		admin_quick_siege_start,
		admin_quick_siege_stop
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;

		final Residence r;
		final SiegeEvent<?, ?> event;
		Calendar calendar;
		HtmlMessage msg;
		switch(command)
		{
			case admin_residence_list:
				msg = new HtmlMessage(5);
				msg.setFile("admin/residence/residence_list.htm");

				StringBuilder replyMSG = new StringBuilder(200);
				for(Residence residence : ResidenceHolder.getInstance().getResidences())
					if(residence != null && !residence.isInstant())
					{
						replyMSG.append("<tr><td>");
						replyMSG.append("<a action=\"bypass -h admin_residence ").append(residence.getId()).append("\">").append(HtmlUtils.htmlResidenceName(residence.getId())).append("</a>");
						replyMSG.append("</td><td>");

						Clan owner = residence.getOwner();
						if(owner == null)
							replyMSG.append("NPC");
						else
							replyMSG.append(owner.getName());

						replyMSG.append("</td></tr>");
					}
				msg.replace("%residence_list%", replyMSG.toString());
				activeChar.sendPacket(msg);
				break;
			case admin_residence:
				if(wordList.length != 2)
					return false;
				r = ResidenceHolder.getInstance().getResidence(Integer.parseInt(wordList[1]));
				if(r == null)
					return false;
				event = r.getSiegeEvent();
				msg = new HtmlMessage(5);
				msg.setFile("admin/residence/siege_info.htm");
				msg.replace("%residence%",  HtmlUtils.htmlResidenceName(r.getId()));
				msg.replace("%id%", String.valueOf(r.getId()));
				msg.replace("%owner%", r.getOwner() == null ? "NPC" : r.getOwner().getName());
				msg.replace("%cycle%", String.valueOf(r.getCycle()));
				msg.replace("%paid_cycle%", String.valueOf(r.getPaidCycle()));
				msg.replace("%reward_count%", "0");
				msg.replace("%left_time%", String.valueOf(r.getCycleDelay()));

				StringBuilder clans = new StringBuilder(100);
				for(Map.Entry<Object, List<Object>> entry : event.getObjects().entrySet())
				{
					for(Object o : entry.getValue())
					{
						if(o instanceof SiegeClanObject)
						{
							SiegeClanObject siegeClanObject = (SiegeClanObject)o;
							clans.append("<tr>").append("<td>").append(siegeClanObject.getClan().getName()).append("</td>").append("<td>").append(siegeClanObject.getClan().getLeaderName()).append("</td>").append("<td>").append(siegeClanObject.getType()).append("</td>").append("</tr>");
						}
					}
				}
				msg.replace("%clans%", clans.toString());

				msg.replace("%hour%", String.valueOf(r.getSiegeDate().get(Calendar.HOUR_OF_DAY)));
				msg.replace("%minute%", String.valueOf(r.getSiegeDate().get(Calendar.MINUTE)));
				msg.replace("%day%", String.valueOf(r.getSiegeDate().get(Calendar.DAY_OF_MONTH)));
				msg.replace("%month%", String.valueOf(r.getSiegeDate().get(Calendar.MONTH) + 1));
				msg.replace("%year%", String.valueOf(r.getSiegeDate().get(Calendar.YEAR)));
				activeChar.sendPacket(msg);
				break;
			case admin_set_owner:
				if(wordList.length != 3)
					return false;
				r = ResidenceHolder.getInstance().getResidence(Integer.parseInt(wordList[1]));
				if(r == null)
					return false;
				Clan clan = null;
				String clanName = wordList[2];
				if(!clanName.equalsIgnoreCase("npc"))
				{
					clan = ClanTable.getInstance().getClanByName(clanName);
					if(clan == null)
					{
						activeChar.sendPacket(SystemMsg.INCORRECT_NAME);
						AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, "admin_residence " + r.getId());
						return false;
					}
				}

				event = r.getSiegeEvent();

				event.clearActions();

				r.getLastSiegeDate().setTimeInMillis(clan == null ? 0 : System.currentTimeMillis());
				r.getOwnDate().setTimeInMillis(clan == null ? 0 : System.currentTimeMillis());
				r.changeOwner(clan);

				if(r.getResidenceSide() == ResidenceSide.NEUTRAL)
				{
					r.setResidenceSide(ResidenceSide.LIGHT, false);
					for(Object object : event.getObjects("castle_peace_light_npcs"))
					{
						if(object instanceof SpawnExObject)
							((SpawnExObject) object).spawnObject(event);
					}
				}

				event.reCalcNextTime(false);
				break;
			case admin_set_siege_time:
				r = ResidenceHolder.getInstance().getResidence(Integer.parseInt(wordList[1]));
				if(r == null)
					return false;

				calendar = (Calendar) r.getSiegeDate().clone();
				for(int i = 2; i < wordList.length; i++)
				{
					int type;
					int val = Integer.parseInt(wordList[i]);
					switch(i)
					{
						case 2:
							type = Calendar.HOUR_OF_DAY;
							break;
						case 3:
							type = Calendar.MINUTE;
							break;
						case 4:
							type = Calendar.DAY_OF_MONTH;
							break;
						case 5:
							type = Calendar.MONTH;
							val -= 1;
							break;
						case 6:
							type = Calendar.YEAR;
							break;
						default:
							continue;
					}
					calendar.set(type, val);
				}
				event = r.getSiegeEvent();

				event.clearActions();
				r.getSiegeDate().setTimeInMillis(calendar.getTimeInMillis());
				event.registerActions();
				r.setJdbcState(JdbcEntityState.UPDATED);
				r.update();

				AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, "admin_residence " + r.getId());
				break;
			case admin_quick_siege_start:
				r = ResidenceHolder.getInstance().getResidence(Integer.parseInt(wordList[1]));
				if(r == null)
					return false;

				calendar = Calendar.getInstance();
				if(wordList.length >= 3)
					calendar.set(Calendar.SECOND, -Integer.parseInt(wordList[2]));
				event = r.getSiegeEvent();

				event.clearActions();
				r.getSiegeDate().setTimeInMillis(calendar.getTimeInMillis());
				event.registerActions();
				r.setJdbcState(JdbcEntityState.UPDATED);
				r.update();

				AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, "admin_residence " + r.getId());
				break;
			case admin_quick_siege_stop:
				r = ResidenceHolder.getInstance().getResidence(Integer.parseInt(wordList[1]));
				if(r == null)
					return false;

				event = r.getSiegeEvent();

				event.clearActions();
				ThreadPoolManager.getInstance().execute(() -> event.stopEvent(true));

				AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, "admin_residence " + r.getId());
				break;
		}
		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
