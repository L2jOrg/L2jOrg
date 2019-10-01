package handlers.admincommandhandlers;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.data.Announce;
import org.l2j.gameserver.data.database.data.AnnounceData;
import org.l2j.gameserver.data.database.manager.AnnouncementsManager;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.announce.AnnouncementType;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.GameUtils;

import java.util.StringTokenizer;

import static java.util.Objects.isNull;
import static org.l2j.commons.util.Util.SPACE;
import static org.l2j.commons.util.Util.isDigit;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class AdminAnnouncements implements IAdminCommandHandler {

	private static final String[] ADMIN_COMMANDS = {
		"admin_announce",
		"admin_announce_crit",
		"admin_announce_screen",
		"admin_announces",
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar) {
		final StringTokenizer st = new StringTokenizer(command);
		final String cmd = st.hasMoreTokens() ? st.nextToken() : "";

		switch (cmd) {
			case "admin_announce", "admin_announce_crit", "admin_announce_screen" -> {
				if (!st.hasMoreTokens()) {
					BuilderUtil.sendSysMessage(activeChar, "Syntax: //announce <text to announce here>");
					return false;
				}

				doAnnouncement(activeChar, st, cmd);
			}
			case "admin_announces" -> {
				final String subCmd = st.hasMoreTokens() ? st.nextToken() : "";
				switch (subCmd) {
					case "add": {

						if (!st.hasMoreTokens()) {
							final String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/announces-add.htm");
							GameUtils.sendCBHtml(activeChar, content);
							break;
						}

						final String annType = st.nextToken();

						if (!st.hasMoreTokens()) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}


						final String annInitDelay = st.nextToken();

						if (!Util.isInteger(annInitDelay)) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}


						if (!st.hasMoreTokens()) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}

						final String annDelay = st.nextToken();
						if (!isDigit(annDelay)) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}

						final int delay = Integer.parseInt(annDelay) * 1000;
						final int initDelay = Integer.parseInt(annInitDelay) * 1000;
						final AnnouncementType type = AnnouncementType.findByName(annType);

						if ((delay < (10 * 1000)) && ((type == AnnouncementType.AUTO_NORMAL) || (type == AnnouncementType.AUTO_CRITICAL))) {
							BuilderUtil.sendSysMessage(activeChar, "Delay cannot be less then 10 seconds!");
							break;
						}

						if (!st.hasMoreTokens()) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final String annRepeat = st.nextToken();

						if (!isDigit(annRepeat)) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}

						int repeat = Integer.parseInt(annRepeat);
						if (repeat == 0) {
							repeat = -1;
						}

						if (!st.hasMoreTokens()) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}

						var contentBuilder = new StringBuilder(st.nextToken());
						while (st.hasMoreTokens()) {
							contentBuilder.append(SPACE).append(st.nextToken());
						}

						final AnnounceData announce;
						if ((type == AnnouncementType.AUTO_CRITICAL) || (type == AnnouncementType.AUTO_NORMAL)) {
							announce = new AnnounceData(type, contentBuilder.toString(), activeChar.getName(), initDelay, delay, repeat);
						} else {
							announce = new AnnounceData(type, contentBuilder.toString(), activeChar.getName());
						}
						AnnouncementsManager.getInstance().addAnnouncement(announce);
						BuilderUtil.sendSysMessage(activeChar, "Announcement has been successfully added!");
						return useAdminCommand("admin_announces list", activeChar);
					}
					case "edit": {
						if (!st.hasMoreTokens()) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces edit <id>");
							break;
						}
						final String annId = st.nextToken();
						if (!isDigit(annId)) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces edit <id>");
							break;
						}
						final int id = Integer.parseInt(annId);
						final Announce announce = AnnouncementsManager.getInstance().getAnnounce(id);
						if (isNull(announce)) {
							BuilderUtil.sendSysMessage(activeChar, "Announcement does not exist!");
							break;
						}
						if (!st.hasMoreTokens()) {
							String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/announces-edit.htm");
							final String announcementId = Integer.toString(announce.getId());
							final String announcementType = announce.getType().name();
							String announcementInital = "0";
							String announcementDelay = "0";
							String announcementRepeat = "0";
							final String announcementAuthor = announce.getAuthor();
							final String announcementContent = announce.getContent();
							if (AnnouncementType.isAutoAnnounce(announce.getType())) {
								var autoAnnounce = (AnnounceData) announce;
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
							GameUtils.sendCBHtml(activeChar, content);
							break;
						}
						final String annType = st.nextToken();
						final AnnouncementType type = AnnouncementType.findByName(annType);
						switch (announce.getType()) {
							case AUTO_CRITICAL:
							case AUTO_NORMAL: {
								switch (type) {
									case AUTO_CRITICAL:
									case AUTO_NORMAL: {
										break;
									}
									default: {
										BuilderUtil.sendSysMessage(activeChar, "Announce type can be changed only to AUTO_NORMAL or AUTO_CRITICAL!");
										return false;
									}
								}
								break;
							}
							case NORMAL:
							case CRITICAL: {
								switch (type) {
									case NORMAL:
									case CRITICAL: {
										break;
									}
									default: {
										BuilderUtil.sendSysMessage(activeChar, "Announce type can be changed only to NORMAL or CRITICAL!");
										return false;
									}
								}
								break;
							}
						}
						// ************************************
						if (!st.hasMoreTokens()) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final String annInitDelay = st.nextToken();
						if (!isDigit(annInitDelay)) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final int initDelay = Integer.parseInt(annInitDelay);
						// ************************************
						if (!st.hasMoreTokens()) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final String annDelay = st.nextToken();
						if (!isDigit(annDelay)) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final int delay = Integer.parseInt(annDelay);
						if ((delay < 10) && ((type == AnnouncementType.AUTO_NORMAL) || (type == AnnouncementType.AUTO_CRITICAL))) {
							BuilderUtil.sendSysMessage(activeChar, "Delay cannot be less then 10 seconds!");
							break;
						}
						// ************************************
						if (!st.hasMoreTokens()) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						final String annRepeat = st.nextToken();
						if (!isDigit(annRepeat)) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces add <type> <delay> <repeat> <text>");
							break;
						}
						int repeat = Integer.parseInt(annRepeat);
						if (repeat == 0) {
							repeat = -1;
						}
						// ************************************
						String content = "";
						if (st.hasMoreTokens()) {
							content = st.nextToken();
							while (st.hasMoreTokens()) {
								content += " " + st.nextToken();
							}
						}
						if (content.isEmpty()) {
							content = announce.getContent();
						}
						// ************************************
						announce.setType(type);
						announce.setContent(content);
						announce.setAuthor(activeChar.getName());
						if (AnnouncementType.isAutoAnnounce(announce.getType())) {
							var autoAnnounce = (AnnounceData) announce;
							autoAnnounce.setInitial(initDelay * 1000);
							autoAnnounce.setDelay(delay * 1000);
							autoAnnounce.setRepeat(repeat);
						}
						AnnouncementsManager.getInstance().updateAnnouncement(announce);
						BuilderUtil.sendSysMessage(activeChar, "Announcement has been successfully edited!");
						return useAdminCommand("admin_announces list", activeChar);
					}
					case "remove": {
						if (!st.hasMoreTokens()) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces remove <announcement id>");
							break;
						}
						final String token = st.nextToken();
						if (!isDigit(token)) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces remove <announcement id>");
							break;
						}
						final int id = Integer.parseInt(token);
						if (AnnouncementsManager.getInstance().deleteAnnouncement(id)) {
							BuilderUtil.sendSysMessage(activeChar, "Announcement has been successfully removed!");
						} else {
							BuilderUtil.sendSysMessage(activeChar, "Announcement does not exist!");
						}
						return useAdminCommand("admin_announces list", activeChar);
					}
					case "restart": {
						if (!st.hasMoreTokens()) {
							for (Announce announce : AnnouncementsManager.getInstance().getAllAnnouncements()) {
								if (AnnouncementType.isAutoAnnounce(announce.getType())) {
									var autoAnnounce = (AnnounceData) announce;
									AnnouncementsManager.getInstance().scheduleAnnounce(autoAnnounce);
								}
							}
							BuilderUtil.sendSysMessage(activeChar, "Auto announcements has been successfully restarted.");
							break;
						}
						final String token = st.nextToken();
						if (!isDigit(token)) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces show <announcement id>");
							break;
						}
						final int id = Integer.parseInt(token);
						final Announce announce = AnnouncementsManager.getInstance().getAnnounce(id);
						if (announce != null) {
							if (AnnouncementType.isAutoAnnounce(announce.getType())) {
								var autoAnnounce = (AnnounceData) announce;
								AnnouncementsManager.getInstance().scheduleAnnounce(autoAnnounce);
								BuilderUtil.sendSysMessage(activeChar, "Auto announcement has been successfully restarted.");
							} else {
								BuilderUtil.sendSysMessage(activeChar, "This option has effect only on auto announcements!");
							}
						} else {
							BuilderUtil.sendSysMessage(activeChar, "Announcement does not exist!");
						}
						break;
					}
					case "show": {
						if (!st.hasMoreTokens()) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces show <announcement id>");
							break;
						}
						final String token = st.nextToken();
						if (!isDigit(token)) {
							BuilderUtil.sendSysMessage(activeChar, "Syntax: //announces show <announcement id>");
							break;
						}
						final int id = Integer.parseInt(token);
						final Announce announce = AnnouncementsManager.getInstance().getAnnounce(id);
						if (announce != null) {
							String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/announces-show.htm");
							final String announcementId = Integer.toString(announce.getId());
							final String announcementType = announce.getType().name();
							String announcementInital = "0";
							String announcementDelay = "0";
							String announcementRepeat = "0";
							final String announcementAuthor = announce.getAuthor();
							final String announcementContent = announce.getContent();
							if (AnnouncementType.isAutoAnnounce(announce.getType())) {
								var autoAnnounce = (AnnounceData) announce;
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
							GameUtils.sendCBHtml(activeChar, content);
							break;
						}
						BuilderUtil.sendSysMessage(activeChar, "Announcement does not exist!");
						return useAdminCommand("admin_announces list", activeChar);
					}
					case "list": {
						int page = 0;
						if (st.hasMoreTokens()) {
							final String token = st.nextToken();
							if (isDigit(token)) {
								page = Integer.parseInt(token);
							}
						}

						String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/announces-list.htm");
						final PageResult result = PageBuilder.newBuilder(AnnouncementsManager.getInstance().getAllAnnouncements(), 8, "bypass admin_announces list").currentPage(page).bodyHandler((pages, announcement, sb) ->
						{
							sb.append("<tr>");
							sb.append("<td width=5></td>");
							sb.append("<td width=80>" + announcement.getId() + "</td>");
							sb.append("<td width=100>" + announcement.getType() + "</td>");
							sb.append("<td width=100>" + announcement.getAuthor() + "</td>");
							if ((announcement.getType() == AnnouncementType.AUTO_NORMAL) || (announcement.getType() == AnnouncementType.AUTO_CRITICAL)) {
								sb.append("<td width=60><button action=\"bypass -h admin_announces restart " + announcement.getId() + "\" value=\"Restart\" width=\"60\" height=\"21\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
							} else {
								sb.append("<td width=60><button action=\"\" value=\"\" width=\"60\" height=\"21\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
							}
							if (announcement.getType() == AnnouncementType.EVENT) {
								sb.append("<td width=60><button action=\"bypass -h admin_announces show " + announcement.getId() + "\" value=\"Show\" width=\"60\" height=\"21\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
								sb.append("<td width=60></td>");
							} else {
								sb.append("<td width=60><button action=\"bypass -h admin_announces show " + announcement.getId() + "\" value=\"Show\" width=\"60\" height=\"21\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
								sb.append("<td width=60><button action=\"bypass -h admin_announces edit " + announcement.getId() + "\" value=\"Edit\" width=\"60\" height=\"21\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
							}
							sb.append("<td width=60><button action=\"bypass -h admin_announces remove " + announcement.getId() + "\" value=\"Remove\" width=\"60\" height=\"21\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
							sb.append("<td width=5></td>");
							sb.append("</tr>");
						}).build();

						content = content.replaceAll("%pages%", result.getPagerTemplate().toString());
						content = content.replaceAll("%announcements%", result.getBodyTemplate().toString());
						GameUtils.sendCBHtml(activeChar, content);
						break;
					}
				}
			}
		}
		return false;
	}

	private void doAnnouncement(Player activeChar, StringTokenizer st, String cmd) {
		var announceBuilder  = new StringBuilder(st.nextToken());
		while (st.hasMoreTokens()) {
			announceBuilder.append(SPACE).append(st.nextToken());
		}

		if (cmd.equals("admin_announce_screen")) {
			Broadcast.toAllOnlinePlayersOnScreen(announceBuilder.toString());
		}
		else {
			if (Config.GM_ANNOUNCER_NAME) {
				announceBuilder.append("[").append(activeChar.getName()).append("]");
			}
			Broadcast.toAllOnlinePlayers(announceBuilder.toString(), cmd.equals("admin_announce_crit"));
		}
		AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
