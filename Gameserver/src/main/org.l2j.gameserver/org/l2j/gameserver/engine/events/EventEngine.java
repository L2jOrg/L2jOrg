/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.engine.events;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.EventMethodNotification;
import org.l2j.gameserver.model.eventengine.EventScheduler;
import org.l2j.gameserver.model.eventengine.IConditionalEventScheduler;
import org.l2j.gameserver.model.eventengine.conditions.BetweenConditionalScheduler;
import org.l2j.gameserver.model.eventengine.conditions.HaventRunConditionalScheduler;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.nonNull;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class EventEngine extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventEngine.class);

    private EventEngine() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/events/events.xsd");
    }

    @Override
    public void load() {
        parseDatapackDirectory("data/events", true);
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "event", this::parseEvent));
    }

    private void parseEvent(Node eventNode) {
        final var attr = eventNode.getAttributes();
        final String eventName = parseString(attr, "name");
        final String className = parseString(attr, "class");
        AbstractEventManager<?> eventManager = null;
        try {
            final Class<?> clazz = Class.forName(className);

            // Attempt to find a provider method
            for (Method method : clazz.getMethods()) {
                if (Modifier.isStatic(method.getModifiers()) && AbstractEventManager.class.isAssignableFrom(method.getReturnType()) && (method.getParameterCount() == 0)) {
                    eventManager = (AbstractEventManager<?>) method.invoke(null);
                    break;
                }
            }

            if (eventManager == null) {
                throw new NoSuchMethodError("Couldn't method that gives instance of AbstractEventManager!");
            }
        } catch (Exception e) {
            LOGGER.warn("Couldn't locate event manager {} instance for event: {}!", className, eventName, e);
            return;
        }

        for (Node child = eventNode.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
            switch (child.getNodeName()){
                case "scheduler" -> parseScheduler(eventManager, child);
                case "config" -> eventManager.config(this, child);
            }
        }

        eventManager.setName(eventName);
        eventManager.onInitialized();
        eventManager.startScheduler();
        eventManager.startConditionalSchedulers();

        LOGGER.info("{}:[{}] Initialized", eventName, eventManager.getClass().getSimpleName());
    }

    private void parseScheduler(AbstractEventManager<?> eventManager, Node innerNode) {
        final Set<EventScheduler> schedulers = new LinkedHashSet<>();
        final Set<IConditionalEventScheduler> conditionalSchedulers = new LinkedHashSet<>();

        for (Node node = innerNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {

            if ("schedule".equals(node.getNodeName())) {
                schedulers.add(parseSchedule(eventManager, node));

            } else if ("conditionalSchedule".equals(node.getNodeName())) {
                parseConditinalSchedule(eventManager, conditionalSchedulers, node);
            }
        }
        eventManager.setSchedulers(schedulers);
        eventManager.setConditionalSchedulers(conditionalSchedulers);
    }

    private void parseConditinalSchedule(AbstractEventManager<?> eventManager, Set<IConditionalEventScheduler> conditionalSchedulers, Node node) {
        for (Node eventNode = node.getFirstChild(); nonNull(eventNode); eventNode = eventNode.getNextSibling()) {

            if ("run".equals(eventNode.getNodeName())) {
                final String name = parseString(eventNode.getAttributes(), "name");
                final String ifType = parseString(eventNode.getAttributes(), "if", "BETWEEN").toUpperCase();

                switch (ifType) {
                    case "BETWEEN" -> parseBetweenSchedule(eventManager, conditionalSchedulers, eventNode, name);
                    case "HAVENT_RUN" -> conditionalSchedulers.add(new HaventRunConditionalScheduler(eventManager, name));
                }
            }
        }
    }

    private void parseBetweenSchedule(AbstractEventManager<?> eventManager, Set<IConditionalEventScheduler> conditionalSchedulers, Node eventNode, String name) {
        NodeList childs = eventNode.getChildNodes();
        if(childs.getLength() != 2) {
            LOGGER.warn("Event: {} has incorrect amount of schedulers expected: 2 found: {}", eventManager.getName(), childs.getLength());
        } else {
            conditionalSchedulers.add(new BetweenConditionalScheduler(eventManager, name, childs.item(0).getTextContent(), childs.item(1).getTextContent()));
        }
    }

    private EventScheduler parseSchedule(AbstractEventManager<?> eventManager, Node scheduleNode) {
        StatsSet params = new StatsSet(parseAttributes(scheduleNode));
        final EventScheduler scheduler = new EventScheduler(eventManager, params);
        try {
            scheduler.addEventNotification(new EventMethodNotification(eventManager, params.getString("event")));
        } catch (Exception e) {
            LOGGER.warn("Couldn't add event notification for {}", eventManager.getClass(), e);
        }
        return scheduler;
    }

    public static void init() {
        getInstance().load();
    }

    public static EventEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final EventEngine INSTANCE = new EventEngine();
    }
}
