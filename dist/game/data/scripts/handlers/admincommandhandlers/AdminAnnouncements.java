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

import java.util.StringTokenizer;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.cache.HtmCache;
import com.l2jmobius.gameserver.data.sql.impl.AnnouncementsTable;
import com.l2jmobius.gameserver.handler.IAdminCommandHandler;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.announce.Announcement;
import com.l2jmobius.gameserver.model.announce.AnnouncementType;
import com.l2jmobius.gameserver.model.announce.AutoAnnouncement;
import com.l2jmobius.gameserver.model.announce.IAnnouncement;
import com.l2jmobius.gameserver.model.html.PageBuilder;
import com.l2jmobius.gameserver.model.html.PageResult;
import com.l2jmobius.gameserver.util.Broadcast;
import com.l2jmobius.gameserver.util.BuilderUtil;
import com.l2jmobius.gameserver.util.Util;

/**
 * @author UnAfraid
 */
public class AdminAnnouncements implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_announce",
		"admin_announce_crit",
		"admin_announce_screen",
		"admin_announces",
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command);
		final String cmd = st.hasMoreTokens() ? st.nextToken() : "";
		switch (cmd)
		{
			case "admin_announce":
			case "admin_announce_crit":
			case "admin_announce_screen":
			{
				if (!st.hasMoreTokens())
				{
					BuilderUtil.sendSysMessage(activeChar, "Syntax: //announce <text to announce here>");
					return false;
				}
				String announce = st.nextToken();
				while (st.hasMoreTokens())
				{
					announce += " " + st.nextToken();
				}
				if (cmd.equals("admin_announce_screen"))
				{
					Broadcast.toAllOnlinePlayersOnScreen(announce);
				}
				else
				{
					if (Config.GM_ANNOUNCER_NAME)
					{
						announce = announce + " [" + activeChar.getName() + "]";
					}
					Broadcast.toAllOnlinePlayers(announce, cmd.equals("admin_announce_crit"));
				}
				AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
				break;
			}
			case "admin_announces":
			{
				final String subCmd = st.hasMoreTokens() ? st.nextToken() : "";
				switch (subCmd)
				{
					case "add":
					{
						if (!st.hasMoreTokens())
						{
							final String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/announces-add.htm");
							Util.sendCBHtml(activeChar, content);
							break;
						}
						final String annType = st.nextToken();
						final AnnouncementType type = AnnouncementType.findByName(annType);
						// ************************************
						if (!st.hasMoreTokens())
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final String annInitDelay = st.nextToken();
						if (!Util.isDigit(annInitDelay))
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final int initDelay = Integer.parseInt(annInitDelay) * 1000;
						// ************************************
						if (!st.hasMoreTokens())
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final String annDelay = st.nextToken();
						if (!Util.isDigit(annDelay))
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final int delay = Integer.parseInt(annDelay) * 1000;
						if ((delay < (10 * 1000)) && ((type == AnnouncementType.AUTO_NORMAL) || (type == AnnouncementType.AUTO_CRITICAL)))
						{
							BuilderUtil.sendSysMessage(activeChar, "Delay cannot be less then 10 seconds!");
							break;
						}
						// ************************************
						if (!st.hasMoreTokens())
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final String annRepeat = st.nextToken();
						if (!Util.isDigit(annRepeat))
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						int repeat = Integer.parseInt(annRepeat);
						if (repeat == 0)
						{
							repeat = -1;
						}
						// ************************************
						if (!st.hasMoreTokens())
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						String content = st.nextToken();
						while (st.hasMoreTokens())
						{
							content += " " + st.nextToken();
						}
						// ************************************
						final IAnnouncement announce;
						if ((type == AnnouncementType.AUTO_CRITICAL) || (type == AnnouncementType.AUTO_NORMAL))
						{
							announce = new AutoAnnouncement(type, content, activeChar.getName(), initDelay, delay, repeat);
						}
						else
						{
							announce = new Announcement(type, content, activeChar.getName());
						}
						AnnouncementsTable.getInstance().addAnnouncement(announce);
						BuilderUtil.sendSysMessage(activeChar, "Announcement has been successfully added!");
						return useAdminCommand("admin_announces list", activeChar);
					}
					case "edit":
					{
						if (!st.hasMoreTokens())
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces edit <id>");
							break;
						}
						final String annId = st.nextToken();
						if (!Util.isDigit(annId))
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces edit <id>");
							break;
						}
						final int id = Integer.parseInt(annId);
						final IAnnouncement announce = AnnouncementsTable.getInstance().getAnnounce(id);
						if (announce == null)
						{
							BuilderUtil.sendSysMessage(activeChar, "Announcement does not exist!");
							break;
						}
						if (!st.hasMoreTokens())
						{
							String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/announces-edit.htm");
							final String announcementId = Integer.toString(announce.getId());
							final String announcementType = announce.getType().name();
							String announcementInital = "0";
							String announcementDelay = "0";
							String announcementRepeat = "0";
							final String announcementAuthor = announce.getAuthor();
							final String announcementContent = announce.getContent();
							if (announce instanceof AutoAnnouncement)
							{
								final AutoAnnouncement autoAnnounce = (AutoAnnouncement) announce;
								announcementInital = Long.toString(autoAnnounce.getInitial() / 1000);
								announcementDelay = Long.toString(autoAnnounce.getDelay() / 1000);
								announcementRepeat = Integer.toString(autoAnnounce.getRepeat());
							}
							content = content.replaceAll("%id%", announcementId);
							content = content.replaceAll("%type%", announcementType);
							content = content.replaceAll("%initial%", announcementInital);
							content = content.replaceAll("%delay%", announcementDelay);
							content = content.replaceAll("%repeat%", announcementRepeat);
							content = content.replaceAll("%author%", announcementAuthor);
							content = content.replaceAll("%content%", announcementContent);
							Util.sendCBHtml(activeChar, content);
							break;
						}
						final String annType = st.nextToken();
						final AnnouncementType type = AnnouncementType.findByName(annType);
						switch (announce.getType())
						{
							case AUTO_CRITICAL:
							case AUTO_NORMAL:
							{
								switch (type)
								{
									case AUTO_CRITICAL:
									case AUTO_NORMAL:
									{
										break;
									}
									default:
									{
										BuilderUtil.sendSysMessage(activeChar, "Announce type can be changed only to AUTO_NORMAL or AUTO_CRITICAL!");
										return false;
									}
								}
								break;
							}
							case NORMAL:
							case CRITICAL:
							{
								switch (type)
								{
									case NORMAL:
									case CRITICAL:
									{
										break;
									}
									default:
									{
										BuilderUtil.sendSysMessage(activeChar, "Announce type can be changed only to NORMAL or CRITICAL!");
										return false;
									}
								}
								break;
							}
						}
						// ************************************
						if (!st.hasMoreTokens())
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final String annInitDelay = st.nextToken();
						if (!Util.isDigit(annInitDelay))
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final int initDelay = Integer.parseInt(annInitDelay);
						// ************************************
						if (!st.hasMoreTokens())
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final String annDelay = st.nextToken();
						if (!Util.isDigit(annDelay))
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final int delay = Integer.parseInt(annDelay);
						if ((delay < 10) && ((type == AnnouncementType.AUTO_NORMAL) || (type == AnnouncementType.AUTO_CRITICAL)))
						{
							BuilderUtil.sendSysMessage(activeChar, "Delay cannot be less then 10 seconds!");
							break;
						}
						// ************************************
						if (!st.hasMoreTokens())
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final String annRepeat = st.nextToken();
						if (!Util.isDigit(annRepeat))
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						int repeat = Integer.parseInt(annRepeat);
						if (repeat == 0)
						{
							repeat = -1;
						}
						// ************************************
						String content = "";
						if (st.hasMoreTokens())
						{
							content = st.nextToken();
							while (st.hasMoreTokens())
							{
								content += " " + st.nextToken();
							}
						}
						if (content.isEmpty())
						{
							content = announce.getContent();
						}
						// ************************************
						announce.setType(type);
						announce.setContent(content);
						announce.setAuthor(activeChar.getName());
						if (announce instanceof AutoAnnouncement)
						{
							final AutoAnnouncement autoAnnounce = (AutoAnnouncement) announce;
							autoAnnounce.setInitial(initDelay * 1000);
							autoAnnounce.setDelay(delay * 1000);
							autoAnnounce.setRepeat(repeat);
						}
						announce.updateMe();
						BuilderUtil.sendSysMessage(activeChar, "Announcement has been successfully edited!");
						return useAdminCommand("admin_announces list", activeChar);
					}
					case "remove":
					{
						if (!st.hasMoreTokens())
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces remove <announcement id>");
							break;
						}
						final String token = st.nextToken();
						if (!Util.isDigit(token))
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces remove <announcement id>");
							break;
						}
						final int id = Integer.parseInt(token);
						if (AnnouncementsTable.getInstance().deleteAnnouncement(id))
						{
							BuilderUtil.sendSysMessage(activeChar, "Announcement has been successfully removed!");
						}
						else
						{
							BuilderUtil.sendSysMessage(activeChar, "Announcement does not exist!");
						}
						return useAdminCommand("admin_announces list", activeChar);
					}
					case "restart":
					{
						if (!st.hasMoreTokens())
						{
							for (IAnnouncement announce : AnnouncementsTable.getInstance().getAllAnnouncements())
							{
								if (announce instanceof AutoAnnouncement)
								{
									final AutoAnnouncement autoAnnounce = (AutoAnnouncement) announce;
									autoAnnounce.restartMe();
								}
							}
							BuilderUtil.sendSysMessage(activeChar, "Auto announcements has been successfully restarted.");
							break;
						}
						final String token = st.nextToken();
						if (!Util.isDigit(token))
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces show <announcement id>");
							break;
						}
						final int id = Integer.parseInt(token);
						final IAnnouncement announce = AnnouncementsTable.getInstance().getAnnounce(id);
						if (announce != null)
						{
							if (announce instanceof AutoAnnouncement)
							{
								final AutoAnnouncement autoAnnounce = (AutoAnnouncement) announce;
								autoAnnounce.restartMe();
								BuilderUtil.sendSysMessage(activeChar, "Auto announcement has been successfully restarted.");
							}
							else
							{
								BuilderUtil.sendSysMessage(activeChar, "This option has effect only on auto announcements!");
							}
						}
						else
						{
							BuilderUtil.sendSysMessage(activeChar, "Announcement does not exist!");
						}
						break;
					}
					case "show":
					{
						if (!st.hasMoreTokens())
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces show <announcement id>");
							break;
						}
						final String token = st.nextToken();
						if (!Util.isDigit(token))
						{
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces show <announcement id>");
							break;
						}
						final int id = Integer.parseInt(token);
						final IAnnouncement announce = AnnouncementsTable.getInstance().getAnnounce(id);
						if (announce != null)
						{
							String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/announces-show.htm");
							final String announcementId = Integer.toString(announce.getId());
							final String announcementType = announce.getType().name();
							String announcementInital = "0";
							String announcementDelay = "0";
							String announcementRepeat = "0";
							final String announcementAuthor = announce.getAuthor();
							final String announcementContent = announce.getContent();
							if (announce instanceof AutoAnnouncement)
							{
								final AutoAnnouncement autoAnnounce = (AutoAnnouncement) announce;
								announcementInital = Long.toString(autoAnnounce.getInitial() / 1000);
								announcementDelay = Long.toString(autoAnnounce.getDelay() / 1000);
								announcementRepeat = Integer.toString(autoAnnounce.getRepeat());
							}
							content = content.replaceAll("%id%", announcementId);
							content = content.replaceAll("%type%", announcementType);
							content = content.replaceAll("%initial%", announcementInital);
							content = content.replaceAll("%delay%", announcementDelay);
							content = content.replaceAll("%repeat%", announcementRepeat);
							content = content.replaceAll("%author%", announcementAuthor);
							content = content.replaceAll("%content%", announcementContent);
							Util.sendCBHtml(activeChar, content);
							break;
						}
						BuilderUtil.sendSysMessage(activeChar, "Announcement does not exist!");
						return useAdminCommand("admin_announces list", activeChar);
					}
					case "list":
					{
						int page = 0;
						if (st.hasMoreTokens())
						{
							final String token = st.nextToken();
							if (Util.isDigit(token))
							{
								page = Integer.valueOf(token);
							}
						}
						
						String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/announces-list.htm");
						final PageResult result = PageBuilder.newBuilder(AnnouncementsTable.getInstance().getAllAnnouncements(), 8, "bypass admin_announces list").currentPage(page).bodyHandler((pages, announcement, sb) ->
						{
							sb.append("<tr>");
							sb.append("<td width=5></td>");
							sb.append("<td width=80>" + announcement.getId() + "</td>");
							sb.append("<td width=100>" + announcement.getType() + "</td>");
							sb.append("<td width=100>" + announcement.getAuthor() + "</td>");
							if ((announcement.getType() == AnnouncementType.AUTO_NORMAL) || (announcement.getType() == AnnouncementType.AUTO_CRITICAL))
							{
								sb.append("<td width=60><button action=\"bypass -h admin_announces restart " + announcement.getId() + "\" value=\"Restart\" width=\"60\" height=\"21\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
							}
							else
							{
								sb.append("<td width=60><button action=\"\" value=\"\" width=\"60\" height=\"21\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
							}
							if (announcement.getType() == AnnouncementType.EVENT)
							{
								sb.append("<td width=60><button action=\"bypass -h admin_announces show " + announcement.getId() + "\" value=\"Show\" width=\"60\" height=\"21\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
								sb.append("<td width=60></td>");
							}
							else
							{
								sb.append("<td width=60><button action=\"bypass -h admin_announces show " + announcement.getId() + "\" value=\"Show\" width=\"60\" height=\"21\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
								sb.append("<td width=60><button action=\"bypass -h admin_announces edit " + announcement.getId() + "\" value=\"Edit\" width=\"60\" height=\"21\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
							}
							sb.append("<td width=60><button action=\"bypass -h admin_announces remove " + announcement.getId() + "\" value=\"Remove\" width=\"60\" height=\"21\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
							sb.append("<td width=5></td>");
							sb.append("</tr>");
						}).build();
						
						content = content.replaceAll("%pages%", result.getPagerTemplate().toString());
						content = content.replaceAll("%announcements%", result.getBodyTemplate().toString());
						Util.sendCBHtml(activeChar, content);
						break;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
