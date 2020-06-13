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
package org.l2j.gameserver.instancemanager;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.instancemanager.tasks.StartMovingTask;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.NpcWalkerNode;
import org.l2j.gameserver.model.WalkInfo;
import org.l2j.gameserver.model.WalkRoute;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.tasks.npc.walker.ArrivedTask;
import org.l2j.gameserver.model.holders.NpcRoutesHolder;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.util.GameUtils.isMonster;
import static org.l2j.gameserver.util.MathUtil.calculateDistance3D;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius2D;

/**
 * This class manages walking monsters.
 *
 * @author GKR
 */
public final class WalkingManager extends GameXmlReader {
    // Repeat style:
    // -1 - no repeat
    // 0 - go back
    // 1 - go to first point (circle style)
    // 2 - teleport to first point (conveyor style)
    // 3 - random walking between points.
    public static final byte NO_REPEAT = -1;
    public static final byte REPEAT_GO_BACK = 0;
    public static final byte REPEAT_GO_FIRST = 1;
    public static final byte REPEAT_TELE_FIRST = 2;
    public static final byte REPEAT_RANDOM = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(WalkingManager.class);
    private final Map<String, WalkRoute> _routes = new HashMap<>(); // all available routes
    private final Map<Integer, WalkInfo> _activeRoutes = new HashMap<>(); // each record represents NPC, moving by predefined route from _routes, and moving progress
    private final Map<Integer, NpcRoutesHolder> _routesToAttach = new HashMap<>(); // each record represents NPC and all available routes for it
    private final Map<Npc, ScheduledFuture<?>> _startMoveTasks = new ConcurrentHashMap<>();
    private final Map<Npc, ScheduledFuture<?>> _repeatMoveTasks = new ConcurrentHashMap<>();
    private final Map<Npc, ScheduledFuture<?>> _arriveTasks = new ConcurrentHashMap<>();

    private WalkingManager() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/Routes.xsd");
    }

    @Override
    public final void load() {
        parseDatapackFile("data/Routes.xml");
        LOGGER.info("Loaded {} walking routes.", _routes.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        final Node n = doc.getFirstChild();
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
            if (d.getNodeName().equals("route")) {
                final String routeName = parseString(d.getAttributes(), "name");
                final boolean repeat = parseBoolean(d.getAttributes(), "repeat");
                final String repeatStyle = d.getAttributes().getNamedItem("repeatStyle").getNodeValue().toLowerCase();

                final byte repeatType;
                switch (repeatStyle) {
                    case "back": {
                        repeatType = REPEAT_GO_BACK;
                        break;
                    }
                    case "cycle": {
                        repeatType = REPEAT_GO_FIRST;
                        break;
                    }
                    case "conveyor": {
                        repeatType = REPEAT_TELE_FIRST;
                        break;
                    }
                    case "random": {
                        repeatType = REPEAT_RANDOM;
                        break;
                    }
                    default: {
                        repeatType = NO_REPEAT;
                        break;
                    }
                }

                final List<NpcWalkerNode> list = new ArrayList<>();
                for (Node r = d.getFirstChild(); r != null; r = r.getNextSibling()) {
                    if (r.getNodeName().equals("point")) {
                        final NamedNodeMap attrs = r.getAttributes();
                        final int x = parseInteger(attrs, "X");
                        final int y = parseInteger(attrs, "Y");
                        final int z = parseInteger(attrs, "Z");
                        final int delay = parseInteger(attrs, "delay");
                        final boolean run = parseBoolean(attrs, "run");
                        NpcStringId npcString = null;
                        String chatString = null;

                        Node node = attrs.getNamedItem("string");
                        if (node != null) {
                            chatString = node.getNodeValue();
                        } else {
                            node = attrs.getNamedItem("npcString");
                            if (node != null) {
                                npcString = NpcStringId.getNpcStringId(node.getNodeValue());
                                if (npcString == null) {
                                    LOGGER.warn(": Unknown npcString '" + node.getNodeValue() + "' for route '" + routeName + "'");
                                    continue;
                                }
                            } else {
                                node = attrs.getNamedItem("npcStringId");
                                if (node != null) {
                                    npcString = NpcStringId.getNpcStringId(Integer.parseInt(node.getNodeValue()));
                                    if (npcString == null) {
                                        LOGGER.warn(": Unknown npcString '" + node.getNodeValue() + "' for route '" + routeName + "'");
                                        continue;
                                    }
                                }
                            }
                        }
                        list.add(new NpcWalkerNode(x, y, z, delay, run, npcString, chatString));
                    } else if (r.getNodeName().equals("target")) {
                        final NamedNodeMap attrs = r.getAttributes();
                        try {
                            final int npcId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                            final int x = Integer.parseInt(attrs.getNamedItem("spawnX").getNodeValue());
                            final int y = Integer.parseInt(attrs.getNamedItem("spawnY").getNodeValue());
                            final int z = Integer.parseInt(attrs.getNamedItem("spawnZ").getNodeValue());

                            if (NpcData.getInstance().getTemplate(npcId) != null) {
                                final NpcRoutesHolder holder = _routesToAttach.containsKey(npcId) ? _routesToAttach.get(npcId) : new NpcRoutesHolder();
                                holder.addRoute(routeName, new Location(x, y, z));
                                _routesToAttach.put(npcId, holder);
                            } else {
                                LOGGER.warn(": NPC with id " + npcId + " for route '" + routeName + "' does not exist.");
                            }
                        } catch (Exception e) {
                            LOGGER.warn(": Error in target definition for route '" + routeName + "'");
                        }
                    }
                }
                _routes.put(routeName, new WalkRoute(routeName, list, repeat, false, repeatType));
            }
        }
    }

    /**
     * @param npc NPC to check
     * @return {@code true} if given NPC, or its leader is controlled by Walking Manager and moves currently.
     */
    public boolean isOnWalk(Npc npc) {
        Monster monster = null;

        if (isMonster(npc)) {
            if (((Monster) npc).getLeader() == null) {
                monster = (Monster) npc;
            } else {
                monster = ((Monster) npc).getLeader();
            }
        }

        if (((monster != null) && !isRegistered(monster)) || !isRegistered(npc)) {
            return false;
        }

        final WalkInfo walk = monster != null ? _activeRoutes.get(monster.getObjectId()) : _activeRoutes.get(npc.getObjectId());
        if (walk.isStoppedByAttack() || walk.isSuspended()) {
            return false;
        }
        return true;
    }

    public WalkRoute getRoute(String route) {
        return _routes.get(route);
    }

    /**
     * @param npc NPC to check
     * @return {@code true} if given NPC controlled by Walking Manager.
     */
    public boolean isRegistered(Npc npc) {
        return _activeRoutes.containsKey(npc.getObjectId());
    }

    /**
     * @param npc
     * @return name of route
     */
    public String getRouteName(Npc npc) {
        return _activeRoutes.containsKey(npc.getObjectId()) ? _activeRoutes.get(npc.getObjectId()).getRoute().getName() : "";
    }

    /**
     * Start to move given NPC by given route
     *
     * @param npc       NPC to move
     * @param routeName name of route to move by
     */
    public void startMoving(Npc npc, String routeName) {
        if (_routes.containsKey(routeName) && (npc != null) && !npc.isDead()) // check, if these route and NPC present
        {
            if (!_activeRoutes.containsKey(npc.getObjectId())) // new walk task
            {
                // only if not already moved / not engaged in battle... should not happens if called on spawn
                if ((npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) || (npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)) {
                    final WalkInfo walk = new WalkInfo(routeName);
                    NpcWalkerNode node = walk.getCurrentNode();

                    // adjust next waypoint, if NPC spawns at first waypoint
                    if ((npc.getX() == node.getX()) && (npc.getY() == node.getY())) {
                        walk.calculateNextNode(npc);
                        node = walk.getCurrentNode();
                    }

                    if (!MathUtil.isInsideRadius3D(npc, node, 3000)) {
                        LOGGER.warn("Route '" + routeName + "': NPC (id=" + npc.getId() + ", x=" + npc.getX() + ", y=" + npc.getY() + ", z=" + npc.getZ() + ") is too far from starting point (node x=" + node.getX() + ", y=" + node.getY() + ", z=" + node.getZ() + ", range=" + calculateDistance3D(npc, node) + "). Teleporting to proper location.");
                        npc.teleToLocation(node);
                    }

                    if (node.runToLocation()) {
                        npc.setRunning();
                    } else {
                        npc.setWalking();
                    }
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, node);

                    final ScheduledFuture<?> task = _repeatMoveTasks.get(npc);
                    if ((task == null) || task.isCancelled() || task.isDone())
                    {
                        final ScheduledFuture<?> newTask = ThreadPool.scheduleAtFixedRate(new StartMovingTask(npc, routeName), 60000, 60000);
                        _repeatMoveTasks.put(npc, newTask);
                        walk.setWalkCheckTask(newTask); // start walk check task, for resuming walk after fight
                    }

                    _activeRoutes.put(npc.getObjectId(), walk); // register route
                } else {
                    final ScheduledFuture<?> task = _startMoveTasks.get(npc);
                    if ((task == null) || task.isCancelled() || task.isDone())
                    {
                        _startMoveTasks.put(npc, ThreadPool.schedule(new StartMovingTask(npc, routeName), 60000));
                    }
                }
            } else
            // walk was stopped due to some reason (arrived to node, script action, fight or something else), resume it
            {
                if (_activeRoutes.containsKey(npc.getObjectId()) && ((npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) || (npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE))) {
                    final WalkInfo walk = _activeRoutes.get(npc.getObjectId());
                    if (walk == null) {
                        return;
                    }

                    // Prevent call simultaneously from scheduled task and onArrived() or temporarily stop walking for resuming in future
                    if (walk.isBlocked() || walk.isSuspended()) {
                        return;
                    }

                    walk.setBlocked(true);
                    final NpcWalkerNode node = walk.getCurrentNode();
                    if (node.runToLocation()) {
                        npc.setRunning();
                    } else {
                        npc.setWalking();
                    }
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, node);
                    walk.setBlocked(false);
                    walk.setStoppedByAttack(false);
                }
            }
        }
    }

    /**
     * Cancel NPC moving permanently
     *
     * @param npc NPC to cancel
     */
    public synchronized void cancelMoving(Npc npc) {
        final WalkInfo walk = _activeRoutes.remove(npc.getObjectId());
        if (walk != null) {
            walk.getWalkCheckTask().cancel(true);
        }
    }

    /**
     * Resumes previously stopped moving
     *
     * @param npc NPC to resume
     */
    public void resumeMoving(Npc npc) {
        final WalkInfo walk = _activeRoutes.get(npc.getObjectId());
        if (walk != null) {
            walk.setSuspended(false);
            walk.setStoppedByAttack(false);
            startMoving(npc, walk.getRoute().getName());
        }
    }

    /**
     * Pause NPC moving until it will be resumed
     *
     * @param npc             NPC to pause moving
     * @param suspend         {@code true} if moving was temporarily suspended for some reasons of AI-controlling script
     * @param stoppedByAttack {@code true} if moving was suspended because of NPC was attacked or desired to attack
     */
    public void stopMoving(Npc npc, boolean suspend, boolean stoppedByAttack) {
        Monster monster = null;

        if (isMonster(npc)) {
            if (((Monster) npc).getLeader() == null) {
                monster = (Monster) npc;
            } else {
                monster = ((Monster) npc).getLeader();
            }
        }

        if (((monster != null) && !isRegistered(monster)) || !isRegistered(npc)) {
            return;
        }

        final WalkInfo walk = monster != null ? _activeRoutes.get(monster.getObjectId()) : _activeRoutes.get(npc.getObjectId());

        walk.setSuspended(suspend);
        walk.setStoppedByAttack(stoppedByAttack);

        if (monster != null) {
            monster.stopMove(null);
            monster.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        } else {
            npc.stopMove(null);
            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }

    /**
     * Manage "node arriving"-related tasks: schedule move to next node; send ON_NODE_ARRIVED event to Quest script
     *
     * @param npc NPC to manage
     */
    public void onArrived(Npc npc) {
        if (_activeRoutes.containsKey(npc.getObjectId())) {
            final WalkInfo walk = _activeRoutes.get(npc.getObjectId());

            // Opposite should not happen... but happens sometime
            if ((walk.getCurrentNodeId() >= 0) && (walk.getCurrentNodeId() < walk.getRoute().getNodesCount())) {
                final NpcWalkerNode node = walk.getRoute().getNodeList().get(walk.getCurrentNodeId());
                if (isInsideRadius2D(npc, node, 10)) {
                    walk.calculateNextNode(npc);
                    walk.setBlocked(true); // prevents to be ran from walk check task, if there is delay in this node.

                    if (node.getNpcString() != null) {
                        npc.broadcastSay(ChatType.NPC_GENERAL, node.getNpcString());
                    } else if (!node.getChatText().isEmpty()) {
                        npc.broadcastSay(ChatType.NPC_GENERAL, node.getChatText());
                    }

                    final ScheduledFuture<?> task = _arriveTasks.get(npc);
                    if ((task == null) || task.isCancelled() || task.isDone())
                    {
                        _arriveTasks.put(npc, ThreadPool.schedule(new ArrivedTask(npc, walk), 100 + (node.getDelay() * 1000)));
                    }
                }
            }
        }
    }

    /**
     * Manage "on death"-related tasks: permanently cancel moving of died NPC
     *
     * @param npc NPC to manage
     */
    public void onDeath(Npc npc) {
        cancelMoving(npc);
    }

    /**
     * Manage "on spawn"-related tasks: start NPC moving, if there is route attached to its spawn point
     *
     * @param npc NPC to manage
     */
    public void onSpawn(Npc npc) {
        if (_routesToAttach.containsKey(npc.getId())) {
            final String routeName = _routesToAttach.get(npc.getId()).getRouteName(npc);
            if (!routeName.isEmpty()) {
                startMoving(npc, routeName);
            }
        }
    }

    public static WalkingManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final WalkingManager INSTANCE = new WalkingManager();
    }
}