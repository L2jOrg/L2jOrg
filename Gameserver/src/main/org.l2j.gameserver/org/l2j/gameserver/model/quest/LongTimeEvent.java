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
package org.l2j.gameserver.model.quest;

import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.DateRange;
import org.l2j.gameserver.data.database.announce.EventAnnouncement;
import org.l2j.gameserver.data.database.announce.manager.AnnouncementsManager;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.datatables.drop.EventDropHolder;
import org.l2j.gameserver.datatables.drop.EventDropList;
import org.l2j.gameserver.instancemanager.EventShrineManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.isNotEmpty;

/**
 * Parent class for long time events.<br>
 * Maintains config reading, spawn of NPCs, adding of event's drop.
 *
 * @author GKR
 * @author JoeAlisson
 */
public class LongTimeEvent extends Quest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LongTimeEvent.class);

    private final List<NpcSpawn> spawnList = new ArrayList<>();
    private final List<EventDropHolder> dropList = new LinkedList<>();
    private final IntSet itemsToDestroy = new HashIntSet();
    private String name;
    private String startMessage;
    private String endMessage;
    private int enterAnnounceId = -1;
    private DateRange period = DateRange.STARTED_DAY;
    private boolean enableShrines = false;

    protected LongTimeEvent() {
        super(-1);
        var parser = new EventParser();
        parser.load();

        final var today = LocalDate.now();
        if (period.isWithinRange(today)) {
            startEvent();
        } else if (period.isAfter(today)) {
            ThreadPool.schedule(this::startEvent, period.secondsToStart(today), TimeUnit.SECONDS);
            LOGGER.info("Event {} will be started at {}", name, period.getStartDate());
        } else {
            destroyItemsOnEnd();
            LOGGER.info("Event {} has passed... Ignored ",  name);
        }
    }

    protected void startEvent() {
        LOGGER.info("Event {} active until {}", name, period.getEndDate());
        dropList.forEach(drop -> EventDropList.getInstance().addGlobalDrop(drop, period));

        final var eventEnd = period.millisToEnd();

        spawnList.forEach(spawn -> AbstractScript.addSpawn(spawn.npcId, spawn.loc.getX(), spawn.loc.getY(), spawn.loc.getZ(), spawn.loc.getHeading(), false, eventEnd, false));

        if (enableShrines) {
            EventShrineManager.getInstance().setEnabled(true);
        }

        if(isNotEmpty(startMessage)) {
            Broadcast.toAllOnlinePlayers(startMessage);
            var announce = new EventAnnouncement(period, startMessage);
            AnnouncementsManager.getInstance().addAnnouncement(announce);
            enterAnnounceId = announce.getId();
        }
        ThreadPool.schedule(new ScheduleEnd(), eventEnd);
    }

    private void destroyItemsOnEnd() {
        itemsToDestroy.forEach(itemId -> {
            World.getInstance().forEachPlayer(player -> player.destroyItemByItemId(name, itemId, -1, player, true));
            getDAO(ItemDAO.class).deleteAllItemsById(itemId);
        });
    }

    public boolean isEventPeriod() {
        return period.isWithinRange(LocalDate.now());
    }

    private class EventParser extends GameXmlReader {

        @Override
        protected Path getSchemaFilePath() {
            return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/eventConfig.xsd");
        }

        @Override
        public void load() {
            parseDatapackFile("data/scripts/org.l2j.scripts/events/" + getScriptName() + "/config.xml");
            releaseResources();
        }

        @Override
        public void parseDocument(Document doc, File f) {
            forEach(doc, "event", eventNode -> {
                var attrs = eventNode.getAttributes();
                name = parseString(attrs, "name");
                period = DateRange.parse(parseString(attrs, "start-date"), parseString(attrs, "end-date"));
                enableShrines = parseBoolean(attrs, "enable-shrines");
                startMessage = parseString(attrs, "start-message");
                endMessage = parseString(attrs, "end-message");

                final var today = LocalDate.now();

                if(period.isWithinRange(today)) {
                    for(var node = eventNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
                        switch (node.getNodeName()) {
                            case "drop" -> parseDrop(node);
                            case "spawns" -> parseSpawns(node);
                        }
                    }
                }

                forEach(eventNode, "destroy-items-on-end",
                        destroyNode -> itemsToDestroy.addAll(parseIntSet(destroyNode)));

            });
        }

        private void parseSpawns(Node node) {
            forEach(node, "spawn", spawnNode -> {
               final var npcId = parseInt(spawnNode.getAttributes(), "npc");

               if(!NpcData.getInstance().existsNpc(npcId)) {
                   LOGGER.warn("{} event: Npc Id {} not found", getScriptName(), npcId);
                   return;
               }
               spawnList.add(new NpcSpawn(npcId, parseLocation(spawnNode)));
            });
        }

        private void parseDrop(Node node) {
            forEach(node, "item", itemNode -> {
                final var attrs  = itemNode.getAttributes();
                final var id = parseInt(attrs, "id");
                final var min = parseInt(attrs, "min");
                final var max = parseInt(attrs, "max");
                final var chance = parseDouble(attrs, "chance");
                final var minLevel = parseInt(attrs, "min-level");
                final var maxLevel = parseInt(attrs, "max-level");
                final var monsters = parseIntSet(attrs, "monsters");

                dropList.add(new EventDropHolder(id, min, max, chance, minLevel, maxLevel, monsters));
            });
        }
    }

    protected static class NpcSpawn {
        protected final Location loc;
        protected final int npcId;

        protected NpcSpawn(int pNpcId, Location spawnLoc) {
            loc = spawnLoc;
            npcId = pNpcId;
        }
    }

    protected class ScheduleEnd implements Runnable {
        @Override
        public void run() {
            if (enableShrines) {
                EventShrineManager.getInstance().setEnabled(false);
            }

            destroyItemsOnEnd();

            if(isNotEmpty(endMessage)) {
                Broadcast.toAllOnlinePlayers(endMessage);
            }

            if(enterAnnounceId != -1) {
                AnnouncementsManager.getInstance().deleteAnnouncement(enterAnnounceId);
            }
        }
    }
}
