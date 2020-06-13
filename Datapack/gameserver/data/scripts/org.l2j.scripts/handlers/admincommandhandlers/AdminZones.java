/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.enums.PlayerAction;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.Priority;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerDlgAnswer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMoveRequest;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;
import org.l2j.gameserver.network.serverpackets.ExServerPrimitive;
import org.l2j.gameserver.network.serverpackets.ExShowTerritory;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.form.ZonePolygonArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.l2j.commons.util.Util.isDigit;

/**
 * @author UnAfraid
 */
public class AdminZones extends AbstractNpcAI implements IAdminCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminZones.class);
    private final Map<Integer, ZoneNodeHolder> _zones = new ConcurrentHashMap<>();

    private static final String[] COMMANDS = { "admin_zones" };

    @Override
    public boolean useAdminCommand(String command, Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command);
        st.nextToken();

        if (!st.hasMoreTokens()) {
            buildZonesEditorWindow(activeChar);
            return false;
        }

        final String subCmd = st.nextToken();
        switch (subCmd) {
            case "load" -> {
                if (st.hasMoreTokens()) {
                    StringBuilder name = new StringBuilder();
                    while (st.hasMoreTokens()) {
                        name.append(st.nextToken()).append(" ");
                    }
                    loadZone(activeChar, name.toString().trim());
                }
            }
            case "create" -> buildHtmlWindow(activeChar, 0);
            case "setname" -> {
                StringBuilder name = new StringBuilder();
                while (st.hasMoreTokens()) {
                    name.append(st.nextToken()).append(" ");
                }
                setName(activeChar, name.toString().trim());
            }
            case "start" -> enablePicking(activeChar);
            case "finish" -> disablePicking(activeChar);
            case "setMinZ" -> {
                if (st.hasMoreTokens()) {
                    final int minZ = Integer.parseInt(st.nextToken());
                    setMinZ(activeChar, minZ);
                }
            }
            case "setMaxZ" -> {
                if (st.hasMoreTokens()) {
                    final int maxZ = Integer.parseInt(st.nextToken());
                    setMaxZ(activeChar, maxZ);
                }
            }
            case "show" -> {
                showPoints(activeChar);
                final ConfirmDlg dlg = new ConfirmDlg("When enable show territory you must restart client to remove it, are you sure about that?");
                dlg.addTime(15 * 1000);
                activeChar.sendPacket(dlg);
                activeChar.addAction(PlayerAction.ADMIN_SHOW_TERRITORY);
            }
            case "hide" -> {
                final ZoneNodeHolder holder = _zones.get(activeChar.getObjectId());
                if (holder != null) {
                    final ExServerPrimitive exsp = new ExServerPrimitive("DebugPoint_" + activeChar.getObjectId(), activeChar.getX(), activeChar.getY(), activeChar.getZ());
                    exsp.addPoint(Color.BLACK, 0, 0, 0);
                    activeChar.sendPacket(exsp);
                }
            }
            case "change" -> {
                if (!st.hasMoreTokens()) {
                    BuilderUtil.sendSysMessage(activeChar, "Missing node index!");
                    break;
                }
                final String indexToken = st.nextToken();
                if (!Util.isInteger(indexToken)) {
                    BuilderUtil.sendSysMessage(activeChar, "Node index should be int!");
                    break;
                }
                final int index = Integer.parseInt(indexToken);
                changePoint(activeChar, index);
            }
            case "delete" -> {
                if (!st.hasMoreTokens()) {
                    BuilderUtil.sendSysMessage(activeChar, "Missing node index!");
                    break;
                }
                final String indexToken = st.nextToken();
                if (!isDigit(indexToken)) {
                    BuilderUtil.sendSysMessage(activeChar, "Node index should be int!");
                    break;
                }
                final int index = Integer.parseInt(indexToken);
                deletePoint(activeChar, index);
                showPoints(activeChar);
            }
            case "clear" -> _zones.remove(activeChar.getObjectId());
            case "dump" -> dumpPoints(activeChar);
            case "list" -> {
                final int page = Util.parseNextInt(st, 0);
                buildHtmlWindow(activeChar, page);
                return false;
            }
        }

        buildHtmlWindow(activeChar, 0);
        return false;
    }

    private void setMinZ(Player activeChar, int minZ)
    {
        _zones.computeIfAbsent(activeChar.getObjectId(), key -> new ZoneNodeHolder(activeChar)).setMinZ(minZ);
    }

    private void setMaxZ(Player activeChar, int maxZ)
    {
        _zones.computeIfAbsent(activeChar.getObjectId(), key -> new ZoneNodeHolder(activeChar)).setMaxZ(maxZ);
    }

    private void buildZonesEditorWindow(Player activeChar) {
        final StringBuilder sb = new StringBuilder();
        final List<Zone> zones = ZoneManager.getInstance().getZones(activeChar);
        for (Zone zone : zones) {
            if (zone.getArea() instanceof ZonePolygonArea) {
                sb.append("<tr>");
                sb.append("<td fixwidth=200><a action=\"bypass -h admin_zones load ").append(zone.getName()).append("\">").append(zone.getName()).append("</a></td>");
                sb.append("</tr>");
            }
        }

        final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
        msg.setFile(activeChar, "data/html/admin/zone_editor.htm");
        msg.replace("%zones%", sb.toString());
        activeChar.sendPacket(msg);
    }

    private void loadZone(Player activeChar, String zoneName) {
        BuilderUtil.sendSystemMessage(activeChar, "Searching for zone: %s", zoneName);
        Zone zone = ZoneManager.getInstance().getZoneByName(zoneName);

        if ((zone != null) && (zone.getArea() instanceof ZonePolygonArea)) {
            final ZonePolygonArea zoneArea = (ZonePolygonArea) zone.getArea();
            final ZoneNodeHolder holder = _zones.computeIfAbsent(activeChar.getObjectId(), val -> new ZoneNodeHolder(activeChar));
            holder.getNodes().clear();
            holder.setName(zone.getName());
            holder.setMinZ(zoneArea.getLowZ());
            holder.setMaxZ(zoneArea.getHighZ());
            for (int i = 0; i < zoneArea.getX().length; i++)
            {
                final int x = zoneArea.getX()[i];
                final int y = zoneArea.getY()[i];
                holder.addNode(new Location(x, y, GeoEngine.getInstance().getHeight(x, y, Rnd.get(zoneArea.getLowZ(), zoneArea.getHighZ()))));
            }
            showPoints(activeChar);
        }
    }

    private void setName(Player player, String name) {
        if (name.contains("<") || name.contains(">") || name.contains("&") || name.contains("\\") || name.contains("\"") || name.contains("$")) {
            BuilderUtil.sendSysMessage(player, "You cannot use symbols like: < > & \" $ \\");
            return;
        }
        _zones.computeIfAbsent(player.getObjectId(), key -> new ZoneNodeHolder(player)).setName(name);
    }


    private void enablePicking(Player player) {
        if (!player.hasAction(PlayerAction.ADMIN_POINT_PICKING)) {
            player.addAction(PlayerAction.ADMIN_POINT_PICKING);
            BuilderUtil.sendSysMessage(player, "Point picking mode activated!");
        } else {
            BuilderUtil.sendSysMessage(player, "Point picking mode is already activated!");
        }
    }

    private void disablePicking(Player player) {
        if (player.removeAction(PlayerAction.ADMIN_POINT_PICKING)) {
            BuilderUtil.sendSysMessage(player, "Point picking mode deactivated!");
        } else {
            BuilderUtil.sendSysMessage(player, "Point picking mode was not activated!");
        }
    }

    private void showPoints(Player player) {
        final ZoneNodeHolder holder = _zones.get(player.getObjectId());
        if (holder != null)
        {
            if (holder.getNodes().size() < 3)
            {
                BuilderUtil.sendSysMessage(player, "In order to visualize this zone you must have at least 3 points.");
                return;
            }
            final ExServerPrimitive exsp = new ExServerPrimitive("DebugPoint_" + player.getObjectId(), player);
            final List<Location> list = holder.getNodes();
            for (int i = 1; i < list.size(); i++)
            {
                final Location prevLoc = list.get(i - 1);
                final Location nextLoc = list.get(i);
                if (holder.getMinZ() != 0)
                {
                    exsp.addLine("Min Point " + i + " > " + (i + 1), Color.CYAN, true, prevLoc.getX(), prevLoc.getY(), holder.getMinZ(), nextLoc.getX(), nextLoc.getY(), holder.getMinZ());
                }
                exsp.addLine("Point " + i + " > " + (i + 1), Color.WHITE, true, prevLoc, nextLoc);
                if (holder.getMaxZ() != 0)
                {
                    exsp.addLine("Max Point " + i + " > " + (i + 1), Color.RED, true, prevLoc.getX(), prevLoc.getY(), holder.getMaxZ(), nextLoc.getX(), nextLoc.getY(), holder.getMaxZ());
                }
            }
            final Location prevLoc = list.get(list.size() - 1);
            final Location nextLoc = list.get(0);
            if (holder.getMinZ() != 0)
            {
                exsp.addLine("Min Point " + list.size() + " > 1", Color.CYAN, true, prevLoc.getX(), prevLoc.getY(), holder.getMinZ(), nextLoc.getX(), nextLoc.getY(), holder.getMinZ());
            }
            exsp.addLine("Point " + list.size() + " > 1", Color.WHITE, true, prevLoc, nextLoc);
            if (holder.getMaxZ() != 0)
            {
                exsp.addLine("Max Point " + list.size() + " > 1", Color.RED, true, prevLoc.getX(), prevLoc.getY(), holder.getMaxZ(), nextLoc.getX(), nextLoc.getY(), holder.getMaxZ());
            }
            player.sendPacket(exsp);
        }
    }

    private void changePoint(Player player, int index) {
        final ZoneNodeHolder holder = _zones.get(player.getObjectId());
        if (holder != null)
        {
            final Location loc = holder.getNodes().get(index);
            if (loc != null)
            {
                enablePicking(player);
                holder.setChangingLoc(loc);
            }
        }
    }

    private void deletePoint(Player player, int index) {
        final ZoneNodeHolder holder = _zones.get(player.getObjectId());
        if (holder != null)
        {
            final Location loc = holder.getNodes().get(index);
            if (loc != null)
            {
                holder.getNodes().remove(loc);
                BuilderUtil.sendSysMessage(player, "Node " + index + " has been removed!");
                if (holder.getNodes().isEmpty())
                {
                    BuilderUtil.sendSysMessage(player, "Since node list is empty destroying session!");
                    _zones.remove(player.getObjectId());
                }
            }
        }
    }

    private void dumpPoints(Player player) {
        final ZoneNodeHolder holder = _zones.get(player.getObjectId());
        if ((holder != null) && !holder.getNodes().isEmpty())
        {
            if (holder.getName().isEmpty())
            {
                BuilderUtil.sendSysMessage(player, "Set name first!");
                return;
            }

            final Location firstNode = holder.getNodes().get(0);
            final StringJoiner sj = new StringJoiner(System.lineSeparator());
            sj.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sj.add("<list xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://l2j.org\" xsi:schemaLocation=\"http://l2j.org zones.xsd\">");
            sj.add("\t<zone name=\"" + holder.getName() + "\" type=\"org.l2j.gameserver.world.zone.type.ScriptZone\" form=\"Polygon\" minZ=\"" + (holder.getMinZ() != 0 ? holder.getMinZ() : firstNode.getZ() - 100) + "\" maxZ=\"" + (holder.getMaxZ() != 0 ? holder.getMaxZ() : firstNode.getZ() + 100) + "\">");
            for (Location loc : holder.getNodes())
            {
                sj.add("\t\t<point x=\"" + loc.getX() + "\" y=\"" + loc.getY() + "\" />");
            }
            sj.add("\t</zone>");
            sj.add("</list>");
            sj.add(""); // new line at end of file
            try
            {
                File file = new File("log/points/" + player.getAccountName() + "/" + holder.getName() + ".xml");
                if (file.exists())
                {
                    int i = 0;
                    while ((file = new File("log/points/" + player.getAccountName() + "/" + holder.getName() + i + ".xml")).exists())
                    {
                        i++;
                    }
                }
                if (!file.getParentFile().isDirectory())
                {
                    file.getParentFile().mkdirs();
                }
                Files.writeString(file.toPath(), sj.toString());
                BuilderUtil.sendSysMessage(player, "Successfully written on: " + file.getAbsolutePath().replace(new File(".").getCanonicalFile().getAbsolutePath(), ""));
            }
            catch (Exception e)
            {
                BuilderUtil.sendSysMessage(player, "Failed writing the dump: " + e.getMessage());
                LOGGER.warn("Failed writing point picking dump for " + player.getName() + ":" + e.getMessage(), e);
            }
        }
    }

    @RegisterEvent(EventType.ON_PLAYER_MOVE_REQUEST)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    @Priority(Integer.MAX_VALUE)
    public TerminateReturn onPlayerPointPicking(OnPlayerMoveRequest event)
    {
        final Player activeChar = event.getActiveChar();
        if (activeChar.hasAction(PlayerAction.ADMIN_POINT_PICKING))
        {
            final Location newLocation = event.getLocation();
            final ZoneNodeHolder holder = _zones.computeIfAbsent(activeChar.getObjectId(), key -> new ZoneNodeHolder(activeChar));
            final Location changeLog = holder.getChangingLoc();
            if (changeLog != null)
            {
                changeLog.setXYZ(newLocation);
                holder.setChangingLoc(null);
                BuilderUtil.sendSysMessage(activeChar, "Location " + (holder.indexOf(changeLog) + 1) + " has been updated!");
                disablePicking(activeChar);
            }
            else
            {
                holder.addNode(newLocation);
                BuilderUtil.sendSysMessage(activeChar, "Location " + (holder.indexOf(changeLog) + 1) + " has been added!");
            }
            // Auto visualization when nodes >= 3
            if (holder.getNodes().size() >= 3)
            {
                showPoints(activeChar);
            }
            buildHtmlWindow(activeChar, 0);

            return new TerminateReturn(true, true, false);
        }
        return null;
    }

    @RegisterEvent(EventType.ON_PLAYER_DLG_ANSWER)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void onPlayerDlgAnswer(OnPlayerDlgAnswer event)
    {
        final Player activeChar = event.getActiveChar();
        if (activeChar.removeAction(PlayerAction.ADMIN_SHOW_TERRITORY) && (event.getAnswer() == 1))
        {
            final ZoneNodeHolder holder = _zones.get(activeChar.getObjectId());
            if (holder != null)
            {
                final List<Location> list = holder.getNodes();
                if (list.size() < 3)
                {
                    BuilderUtil.sendSysMessage(activeChar, "You must have at least 3 nodes to use this option!");
                    return;
                }

                final Location firstLoc = list.get(0);
                final int minZ = holder.getMinZ() != 0 ? holder.getMinZ() : firstLoc.getZ() - 100;
                final int maxZ = holder.getMaxZ() != 0 ? holder.getMaxZ() : firstLoc.getZ() + 100;
                final ExShowTerritory exst = new ExShowTerritory(minZ, maxZ);
                list.forEach(exst::addVertice);
                activeChar.sendPacket(exst);
                BuilderUtil.sendSysMessage(activeChar, "In order to remove the debug you must restart your game client!");
            }
        }
    }

    @Override
    public String[] getAdminCommandList()
    {
        return COMMANDS;
    }

    private void buildHtmlWindow(Player activeChar, int page)
    {
        final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
        msg.setFile(activeChar, "data/html/admin/zone_editor_create.htm");
        final ZoneNodeHolder holder = _zones.computeIfAbsent(activeChar.getObjectId(), key -> new ZoneNodeHolder(activeChar));
        final AtomicInteger position = new AtomicInteger(page * 20);

        final PageResult result = PageBuilder.newBuilder(holder.getNodes(), 20, "bypass -h admin_zones list").currentPage(page).bodyHandler((pages, loc, sb) ->
        {
            sb.append("<tr>");
            sb.append("<td fixwidth=5></td>");
            sb.append("<td fixwidth=20>").append(position.getAndIncrement()).append("</td>");
            sb.append("<td fixwidth=60>").append(loc.getX()).append("</td>");
            sb.append("<td fixwidth=60>").append(loc.getY()).append("</td>");
            sb.append("<td fixwidth=60>").append(loc.getZ()).append("</td>");
            sb.append("<td fixwidth=30><a action=\"bypass -h admin_zones change ").append(holder.indexOf(loc)).append("\">[E]</a></td>");
            sb.append("<td fixwidth=30><a action=\"bypass -h admin_move_to ").append(loc.getX()).append(" ").append(loc.getY()).append(" ").append(loc.getZ()).append("\">[T]</a></td>");
            sb.append("<td fixwidth=30><a action=\"bypass -h admin_zones delete ").append(holder.indexOf(loc)).append("\">[D]</a></td>");
            sb.append("<td fixwidth=5></td>");
            sb.append("</tr>");
        }).build();

        msg.replace("%name%", holder.getName());
        msg.replace("%minZ%", holder.getMinZ());
        msg.replace("%maxZ%", holder.getMaxZ());
        msg.replace("%pages%", result.getPagerTemplate());
        msg.replace("%nodes%", result.getBodyTemplate());
        activeChar.sendPacket(msg);
    }

    protected static class ZoneNodeHolder
    {
        private String _name = "";
        private Location _changingLoc = null;
        private int _minZ;
        private int _maxZ;
        private final List<Location> _nodes = new ArrayList<>();

        ZoneNodeHolder(Player player)
        {
            _minZ = player.getZ() - 200;
            _maxZ = player.getZ() + 200;
        }

        public void setName(String name)
        {
            _name = name;
        }

        public String getName()
        {
            return _name;
        }

        void setChangingLoc(Location loc)
        {
            _changingLoc = loc;
        }

        Location getChangingLoc()
        {
            return _changingLoc;
        }

        void addNode(Location loc)
        {
            _nodes.add(loc);
        }

        public List<Location> getNodes()
        {
            return _nodes;
        }

        int indexOf(Location loc)
        {
            return _nodes.indexOf(loc);
        }

        public int getMinZ()
        {
            return _minZ;
        }

        public int getMaxZ()
        {
            return _maxZ;
        }

        public void setMinZ(int minZ)
        {
            _minZ = minZ;
        }

        public void setMaxZ(int maxZ)
        {
            _maxZ = maxZ;
        }
    }
}
