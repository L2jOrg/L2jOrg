package handlers.admincommandhandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.announce.Announce;
import org.l2j.gameserver.data.database.data.AnnounceData;
import org.l2j.gameserver.data.database.announce.manager.AnnouncementsManager;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.database.announce.AnnouncementType;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.GameUtils;

import java.util.StringTokenizer;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.*;

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
    public boolean useAdminCommand(String command, Player gm) {
        final StringTokenizer st = new StringTokenizer(command);
        final String cmd = st.hasMoreTokens() ? st.nextToken() : "";

        switch (cmd) {
            case "admin_announce", "admin_announce_crit", "admin_announce_screen" -> {
                if (!st.hasMoreTokens()) {
                    BuilderUtil.sendSysMessage(gm, "Syntax: //announce <text to announce here>");
                    return false;
                }

                doAnnouncement(gm, st, cmd);
            }
            case "admin_announces" -> {
                final String subCmd = st.hasMoreTokens() ? st.nextToken() : "";
                return manageAnnouncements(gm, st, subCmd);
            }
        }
        return false;
    }

    private boolean manageAnnouncements(Player gm, StringTokenizer st, String subCmd) {
        return switch (subCmd) {
            case "add" -> addAnnouncements(gm, st);
            case "edit" -> editAnnounce(gm, st);
            case "remove" -> removeAnnounce(gm, st);
            case "restart" -> restartAnnounce(gm, st);
            case "show" -> showAnnounce(gm, st);
            case "list" -> listAnnounces(gm, st);
            default -> false;
        };
    }

    private boolean showAnnounce(Player gm, StringTokenizer st) {
        if (!st.hasMoreTokens()) {
            BuilderUtil.sendSysMessage(gm, "Syntax: //announces show <announcement id>");
            return false;
        }

        final int id = parseNextInt(st, 0);

        final Announce announce = AnnouncementsManager.getInstance().getAnnounce(id);
        if (nonNull(announce)) {
            showAnnounce(gm, announce, "data/html/admin/announces-show.htm");
            return true;
        }
        BuilderUtil.sendSysMessage(gm, "Announcement does not exist!");
        return useAdminCommand("admin_announces list", gm);
    }

    private boolean restartAnnounce(Player gm, StringTokenizer st) {
        if (!st.hasMoreTokens()) {
            var manager = AnnouncementsManager.getInstance();
            manager.getAllAnnouncements().stream().filter(AnnouncementType::isAutoAnnounce).forEach(a -> manager.scheduleAnnounce((AnnounceData) a));
            BuilderUtil.sendSysMessage(gm, "Auto announcements has been successfully restarted.");
            return true;
        }

        final int id = parseNextInt(st, 0);
        final Announce announce = AnnouncementsManager.getInstance().getAnnounce(id);

        if (nonNull(announce)) {
            if (AnnouncementType.isAutoAnnounce(announce)) {
                var autoAnnounce = (AnnounceData) announce;
                AnnouncementsManager.getInstance().scheduleAnnounce(autoAnnounce);
                BuilderUtil.sendSysMessage(gm, "Auto announcement has been successfully restarted.");
                return true;
            } else {
                BuilderUtil.sendSysMessage(gm, "This option has effect only on auto announcements!");
            }
        } else {
            BuilderUtil.sendSysMessage(gm, "Announcement does not exist!");
        }
        return false;
    }

    private boolean removeAnnounce(Player gm, StringTokenizer st) {
        if (!st.hasMoreTokens()) {
            BuilderUtil.sendSysMessage(gm, "Syntax: //announces remove <announcement id>");
            return false;
        }
        final int id = parseNextInt(st, 0);
        if (AnnouncementsManager.getInstance().deleteAnnouncement(id)) {
            BuilderUtil.sendSysMessage(gm, "Announcement has been successfully removed!");
        } else {
            BuilderUtil.sendSysMessage(gm, "Announcement does not exist!");
        }
        return useAdminCommand("admin_announces list", gm);
    }

    private boolean editAnnounce(Player gm, StringTokenizer st) {
        if (!st.hasMoreTokens()) {
            BuilderUtil.sendSysMessage(gm, "Syntax: //announces edit <id>");
            return false;
        }

        var id = parseNextInt(st, 0);

        final Announce announce = AnnouncementsManager.getInstance().getAnnounce(id);

        if ( !(announce instanceof AnnounceData)) {
            BuilderUtil.sendSysMessage(gm, "Announcement does not exist!");
            return false;
        }

        if (!st.hasMoreTokens()) {
            showAnnounce(gm, announce, "data/html/admin/announces-edit.htm");
            return true;
        }

        final AnnouncementType type = AnnouncementType.findByName(st.nextToken());

        if(type == AnnouncementType.EVENT) {
            BuilderUtil.sendSysMessage(gm, "You can't edit event's announcements!");
            return false;
        }


        var announceData = (AnnounceData) announce;
        var tokens = st.countTokens();

        if(tokens < 1) {
            BuilderUtil.sendSysMessage(gm, "Syntax: //announces edit <id> <type> <delay> <repeat> <text>");
            return  false;
        }

        if(type.isAutoAnnounce()) {
            if(tokens >= 3) {
                var delay = st.nextToken();
                var repeatToken = st.nextToken();

                if(!isInteger(delay) || !isInteger(repeatToken) ) {
                    BuilderUtil.sendSysMessage(gm, "Syntax: //announces edit <id> <type> <delay> <repeat> <text>");
                    return false;
                }

                var content = getContent(st);
                var startTime = Integer.parseInt(delay) * 1000L;
                var repeat = Integer.parseInt(repeatToken);

                if(repeat <= 0) {
                    repeat = -1;
                }

                announceData.setAuthor(gm.getName());
                announceData.setDelay(startTime);
                announceData.setInitial(startTime);
                announceData.setRepeat(repeat);
                announceData.setType(type);
                if(content.length() > 0) {
                    announceData.setContent(content.toString());
                }
            } else {
                BuilderUtil.sendSysMessage(gm, "Syntax: //announces edit <id> <type> <delay> <repeat> <text>");
                return  false;
            }
        } else if(tokens > 1) {

            announceData.setAuthor(gm.getName());
            announceData.setType(type);
            var content = getContent(st);
            if(content.length() > 0) {
                announceData.setContent(content.toString());
            }
        }

        AnnouncementsManager.getInstance().updateAnnouncement(announce);
        BuilderUtil.sendSysMessage(gm, "Announcement has been successfully edited!");
        return useAdminCommand("admin_announces list", gm);
    }

    private boolean addAnnouncements(Player gm, StringTokenizer st) {
        if (!st.hasMoreTokens()) {
            final String content = HtmCache.getInstance().getHtm(gm, "data/html/admin/announces-add.htm");
            GameUtils.sendCBHtml(gm, content);
            return true;
        }

        int tokens = st.countTokens();

        if(tokens >= 2)  {
            Announce announce = tryCreateAnnounce(gm, st, tokens);

            if(nonNull(announce)) {
                AnnouncementsManager.getInstance().addAnnouncement(announce);
                BuilderUtil.sendSysMessage(gm, "Announcement has been successfully added!");
                return useAdminCommand("admin_announces list", gm);
            }
        }

        BuilderUtil.sendSysMessage(gm, "Syntax: //announces add <type> [<delay>] [<repeat>] <text>");
        return false;
    }

    private Announce tryCreateAnnounce(Player gm, StringTokenizer st, int tokens) {
        Announce announce = null;
        var type = AnnouncementType.findByName(st.nextToken());

        if(type.isAutoAnnounce()) {
            if(tokens >= 4) {
                announce = tryCreateAutoAnnounce(gm, type, st);
            }
        } else {
            StringBuilder contentBuilder = getContent(st);

            announce = new AnnounceData(type, contentBuilder.toString(), gm.getName());
        }
        return announce;
    }

    private Announce tryCreateAutoAnnounce(Player gm, AnnouncementType type, StringTokenizer st) {
        var delay = st.nextToken();
        var repeatToken = st.nextToken();

        if(!isInteger(delay) || !isInteger(repeatToken)) {
            BuilderUtil.sendSysMessage(gm, "Syntax: //announces add <type> <delay> <repeat> <text>");
            return null;
        }

        var timeToStart = Integer.parseInt(delay) * 1000L;

        if(timeToStart < 10000) {
            BuilderUtil.sendSysMessage(gm, "Delay cannot be less then 10 seconds!");
            return null;
        }

        var repeat = Integer.parseInt(repeatToken);
        if(repeat <= 0) {
            repeat = -1;
        }

        StringBuilder contentBuilder = getContent(st);
        return  new AnnounceData(type, contentBuilder.toString(), gm.getName(), timeToStart, timeToStart, repeat);
    }

    private StringBuilder getContent(StringTokenizer st) {
        var contentBuilder = new StringBuilder(st.nextToken());
        while (st.hasMoreTokens()) {
            contentBuilder.append(SPACE).append(st.nextToken());
        }
        return contentBuilder;
    }

    private void showAnnounce(Player gm, Announce announce, String htmlTemplate) {
        String content = HtmCache.getInstance().getHtm(gm, htmlTemplate);
        final String announcementType = announce.getType().name();
        String announcementInital = "0";
        String announcementDelay = "0";
        String announcementRepeat = "0";

        if (AnnouncementType.isAutoAnnounce(announce)) {
            var autoAnnounce = (AnnounceData) announce;
            announcementInital = Long.toString(autoAnnounce.getInitial() / 1000);
            announcementDelay = Long.toString(autoAnnounce.getDelay() / 1000);
            announcementRepeat = Integer.toString(autoAnnounce.getRepeat());
        }

        content = content.replaceAll("%id%", Integer.toString(announce.getId()))
                .replaceAll("%type%", announcementType)
                .replaceAll("%initial%", announcementInital)
                .replaceAll("%delay%", announcementDelay)
                .replaceAll("%repeat%", announcementRepeat)
                .replaceAll("%author%", announce.getAuthor())
                .replaceAll("%content%", announce.getContent());

        GameUtils.sendCBHtml(gm, content);
    }

    private boolean listAnnounces(Player gm, StringTokenizer st) {
        int page = parseNextInt(st, 0);

        var content = HtmCache.getInstance().getHtm(gm, "data/html/admin/announces-list.htm");

        final PageResult result = PageBuilder.newBuilder(AnnouncementsManager.getInstance().getAllAnnouncements(), 8, "bypass admin_announces list").currentPage(page)
            .bodyHandler((pages, announcement, sb) -> {

                var id = announcement.getId();

                sb.append("<tr><td width=5></td><td width=80>").append(id)
                    .append("</td><td width=100>").append(announcement.getType())
                    .append("</td><td width=100>").append(announcement.getAuthor())
                    .append("</td><td width=60>").append( createButton("admin_announces show", id, "show") )
                    .append("</td><td width=60>").append( createButton("admin_announces remove", id, "remove"))
                    .append("</td>");

                if (announcement.getType() != AnnouncementType.EVENT) {
                    sb.append("<td width=60>").append( createButton("admin_announces edit", id, "edit")).append("</td>");
                } else {
                    sb.append("<td width=60></td>");
                }

                if( AnnouncementType.isAutoAnnounce(announcement) ) {
                    sb.append("<td width=60>").append( createButton("admin_announces restart", id, "restart")).append("</td>");
                } else {
                    sb.append("<td width=60></td>");
                }
                sb.append("<td width=5></td></tr>");
        }).build();

        content = content.replaceAll("%pages%", result.getPagerTemplate().toString());
        content = content.replaceAll("%announcements%", result.getBodyTemplate().toString());
        GameUtils.sendCBHtml(gm, content);
        return true;
    }

    private String createButton(String bypass, int id, String name) {
        return String.format("<button action=\"bypass -h %s %d\" width=60 height=21>%s</button>", bypass, id, name);
    }


    private void doAnnouncement(Player activeChar, StringTokenizer st, String cmd) {
        StringBuilder announceBuilder = getContent(st);

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
