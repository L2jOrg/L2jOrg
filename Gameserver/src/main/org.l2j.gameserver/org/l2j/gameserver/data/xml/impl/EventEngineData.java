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
package org.l2j.gameserver.data.xml.impl;

import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.EventMethodNotification;
import org.l2j.gameserver.model.eventengine.EventScheduler;
import org.l2j.gameserver.model.eventengine.IConditionalEventScheduler;
import org.l2j.gameserver.model.eventengine.conditions.BetweenConditionalScheduler;
import org.l2j.gameserver.model.eventengine.conditions.HaventRunConditionalScheduler;
import org.l2j.gameserver.model.eventengine.drop.*;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * @author UnAfraid
 */
public final class EventEngineData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventEngineData.class);

    private EventEngineData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/events.xsd");
    }

    private static <T> List<T> newList(Class<T> type) {
        return new ArrayList<>();
    }

    private static <K, V> Map<K, V> newMap(Class<K> keyClass, Class<V> valueClass) {
        return new LinkedHashMap<>();
    }

    @Override
    public void load() {
        parseDatapackDirectory("data/events", true);
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node listNode = doc.getFirstChild(); listNode != null; listNode = listNode.getNextSibling()) {
            if ("list".equals(listNode.getNodeName())) {
                for (Node eventNode = listNode.getFirstChild(); eventNode != null; eventNode = eventNode.getNextSibling()) {
                    if ("event".equals(eventNode.getNodeName())) {
                        parseEvent(eventNode);
                    }
                }
            }
        }
    }

    /**
     * @param eventNode
     */
    private void parseEvent(Node eventNode) {
        final String eventName = parseString(eventNode.getAttributes(), "name");
        final String className = parseString(eventNode.getAttributes(), "class");
        AbstractEventManager<?> eventManager = null;
        try {
            final Class<?> clazz = Class.forName(className);

            // Attempt to find getInstance() method
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
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't locate event manager instance for event: " + eventName + " !", e);
            return;
        }

        for (Node innerNode = eventNode.getFirstChild(); innerNode != null; innerNode = innerNode.getNextSibling()) {
            if ("variables".equals(innerNode.getNodeName())) {
                parseVariables(eventManager, innerNode);
            } else if ("scheduler".equals(innerNode.getNodeName())) {
                parseScheduler(eventManager, innerNode);
            } else if ("rewards".equals(innerNode.getNodeName())) {
                parseRewards(eventManager, innerNode);
            }
        }

        // Assign event name
        eventManager.setName(eventName);

        // Start the scheduler
        eventManager.startScheduler();

        // Start conditional schedulers
        eventManager.startConditionalSchedulers();

        // Notify the event manager that we've done initializing its stuff
        eventManager.onInitialized();

        LOGGER.info(getClass().getSimpleName() + ": " + eventManager.getClass().getSimpleName() + ": Initialized");
    }

    /**
     * @param eventManager
     * @param innerNode
     */
    private void parseVariables(AbstractEventManager<?> eventManager, Node innerNode) {
        final StatsSet variables = new StatsSet(LinkedHashMap::new);
        for (Node variableNode = innerNode.getFirstChild(); variableNode != null; variableNode = variableNode.getNextSibling()) {
            if ("variable".equals(variableNode.getNodeName())) {
                variables.set(parseString(variableNode.getAttributes(), "name"), parseString(variableNode.getAttributes(), "value"));
            } else if ("list".equals(variableNode.getNodeName())) {
                parseListVariables(eventManager, variables, variableNode);
            } else if ("map".equals(variableNode.getNodeName())) {
                parseMapVariables(eventManager, variables, variableNode);
            }
        }
        eventManager.setVariables(variables);
    }

    /**
     * @param eventManager
     * @param innerNode
     */
    private void parseScheduler(AbstractEventManager<?> eventManager, Node innerNode) {
        eventManager.stopScheduler();
        final Set<EventScheduler> schedulers = new LinkedHashSet<>();
        final Set<IConditionalEventScheduler> conditionalSchedulers = new LinkedHashSet<>();
        for (Node scheduleNode = innerNode.getFirstChild(); scheduleNode != null; scheduleNode = scheduleNode.getNextSibling()) {
            if ("schedule".equals(scheduleNode.getNodeName())) {
                final StatsSet params = new StatsSet(LinkedHashMap::new);
                final NamedNodeMap attrs = scheduleNode.getAttributes();
                for (int i = 0; i < attrs.getLength(); i++) {
                    final Node node = attrs.item(i);
                    params.set(node.getNodeName(), node.getNodeValue());
                }

                final EventScheduler scheduler = new EventScheduler(eventManager, params);
                for (Node eventNode = scheduleNode.getFirstChild(); eventNode != null; eventNode = eventNode.getNextSibling()) {
                    if ("event".equals(eventNode.getNodeName())) {
                        String methodName = parseString(eventNode.getAttributes(), "name");
                        if (methodName.charAt(0) == '#') {
                            methodName = methodName.substring(1);
                        }

                        final List<Object> args = new ArrayList<>();
                        for (Node argsNode = eventNode.getFirstChild(); argsNode != null; argsNode = argsNode.getNextSibling()) {
                            if ("arg".equals(argsNode.getNodeName())) {
                                final String type = parseString(argsNode.getAttributes(), "type");
                                final Object value = parseObject(eventManager, type, argsNode.getTextContent());
                                if (value != null) {
                                    args.add(value);
                                }
                            }
                        }

                        try {
                            scheduler.addEventNotification(new EventMethodNotification(eventManager, methodName, args));
                        } catch (Exception e) {
                            LOGGER.warn(getClass().getSimpleName() + ": Couldn't add event notification for " + eventManager.getClass().getSimpleName(), e);
                        }
                    }
                }
                schedulers.add(scheduler);
            } else if ("conditionalSchedule".equals(scheduleNode.getNodeName())) {
                final StatsSet params = new StatsSet(LinkedHashMap::new);
                final NamedNodeMap attrs = scheduleNode.getAttributes();
                for (int i = 0; i < attrs.getLength(); i++) {
                    final Node node = attrs.item(i);
                    params.set(node.getNodeName(), node.getNodeValue());
                }

                for (Node eventNode = scheduleNode.getFirstChild(); eventNode != null; eventNode = eventNode.getNextSibling()) {
                    if ("run".equals(eventNode.getNodeName())) {
                        final String name = parseString(eventNode.getAttributes(), "name");
                        final String ifType = parseString(eventNode.getAttributes(), "if", "BETWEEN").toUpperCase();
                        switch (ifType) {
                            case "BETWEEN": {
                                final List<String> names = new ArrayList<>(2);
                                for (Node innerData = eventNode.getFirstChild(); innerData != null; innerData = innerData.getNextSibling()) {
                                    if ("name".equals(innerData.getNodeName())) {
                                        names.add(innerData.getTextContent());
                                    }
                                }
                                if (names.size() != 2) {
                                    LOGGER.warn(": Event: " + eventManager.getClass().getSimpleName() + " has incorrect amount of scheduler names: " + names + " expected: 2 found: " + names.size());
                                } else {
                                    conditionalSchedulers.add(new BetweenConditionalScheduler(eventManager, name, names.get(0), names.get(1)));
                                }
                                break;
                            }
                            case "HAVENT_RUN": {
                                conditionalSchedulers.add(new HaventRunConditionalScheduler(eventManager, name));
                                break;
                            }
                        }
                    }
                }
            }
        }
        eventManager.setSchedulers(schedulers);
        eventManager.setConditionalSchedulers(conditionalSchedulers);
    }

    /**
     * @param eventManager
     * @param innerNode
     */
    private void parseRewards(AbstractEventManager<?> eventManager, Node innerNode) {
        final Map<String, IEventDrop> rewards = new LinkedHashMap<>();
        forEach(innerNode, XmlReader::isNode, rewardsNode ->
        {
            if ("reward".equalsIgnoreCase(rewardsNode.getNodeName())) {
                final String name = parseString(rewardsNode.getAttributes(), "name");
                final EventDrops dropType = parseEnum(rewardsNode.getAttributes(), EventDrops.class, "type");
                switch (dropType) {
                    case GROUPED: {
                        final GroupedDrop droplist = dropType.newInstance();
                        forEach(rewardsNode, "group", groupsNode ->
                        {
                            final EventDropGroup group = new EventDropGroup(parseDouble(groupsNode.getAttributes(), "chance"));
                            forEach(groupsNode, "item", itemNode ->
                            {
                                final NamedNodeMap attrs = itemNode.getAttributes();
                                final int id = parseInteger(attrs, "id");
                                final int min = parseInteger(attrs, "min");
                                final int max = parseInteger(attrs, "max");
                                final double chance = parseDouble(attrs, "chance");
                                group.addItem(new EventDropItem(id, min, max, chance));
                            });
                        });
                        rewards.put(name, droplist);
                        break;
                    }
                    case NORMAL: {
                        final NormalDrop droplist = dropType.newInstance();
                        forEach(rewardsNode, "item", itemNode ->
                        {
                            final NamedNodeMap attrs = itemNode.getAttributes();
                            final int id = parseInteger(attrs, "id");
                            final int min = parseInteger(attrs, "min");
                            final int max = parseInteger(attrs, "max");
                            final double chance = parseDouble(attrs, "chance");
                            droplist.addItem(new EventDropItem(id, min, max, chance));
                        });
                        rewards.put(name, droplist);
                        break;
                    }
                }
            }
        });
        eventManager.setRewards(rewards);
    }

    /**
     * @param eventManager
     * @param variables
     * @param variableNode
     */
    @SuppressWarnings("unchecked")
    private void parseListVariables(AbstractEventManager<?> eventManager, StatsSet variables, Node variableNode) {
        final String name = parseString(variableNode.getAttributes(), "name");
        final String type = parseString(variableNode.getAttributes(), "type");
        final Class<?> classType = getClassByName(eventManager, type);
        final List<?> values = newList(classType);
        switch (type) {
            case "Byte":
            case "Short":
            case "Integer":
            case "Float":
            case "Long":
            case "Double":
            case "String": {
                for (Node stringNode = variableNode.getFirstChild(); stringNode != null; stringNode = stringNode.getNextSibling()) {
                    if ("value".equals(stringNode.getNodeName())) {
                        ((List<Object>) values).add(parseObject(eventManager, type, stringNode.getTextContent()));
                    }
                }
                break;
            }
            case "ItemHolder": {
                for (Node stringNode = variableNode.getFirstChild(); stringNode != null; stringNode = stringNode.getNextSibling()) {
                    if ("item".equals(stringNode.getNodeName())) {
                        ((List<ItemHolder>) values).add(new ItemHolder(parseInteger(stringNode.getAttributes(), "id"), parseLong(stringNode.getAttributes(), "count", 1L)));
                    }
                }
                break;
            }
            case "SkillHolder": {
                for (Node stringNode = variableNode.getFirstChild(); stringNode != null; stringNode = stringNode.getNextSibling()) {
                    if ("skill".equals(stringNode.getNodeName())) {
                        ((List<SkillHolder>) values).add(new SkillHolder(parseInteger(stringNode.getAttributes(), "id"), parseInteger(stringNode.getAttributes(), "level", 1)));
                    }
                }
                break;
            }
            case "Location": {
                for (Node stringNode = variableNode.getFirstChild(); stringNode != null; stringNode = stringNode.getNextSibling()) {
                    if ("location".equals(stringNode.getNodeName())) {
                        ((List<Location>) values).add(new Location(parseInteger(stringNode.getAttributes(), "x"), parseInteger(stringNode.getAttributes(), "y"), parseInteger(stringNode.getAttributes(), "z", parseInteger(stringNode.getAttributes(), "heading", 0))));
                    }
                }
                break;
            }
            default: {
                LOGGER.info(getClass().getSimpleName() + ": Unhandled list case: " + type + " for event: " + eventManager.getClass().getSimpleName());
                break;
            }
        }
        variables.set(name, values);
    }

    /**
     * @param eventManager
     * @param variables
     * @param variableNode
     */
    @SuppressWarnings("unchecked")
    private void parseMapVariables(AbstractEventManager<?> eventManager, StatsSet variables, Node variableNode) {
        final String name = parseString(variableNode.getAttributes(), "name");
        final String keyType = parseString(variableNode.getAttributes(), "keyType");
        final String valueType = parseString(variableNode.getAttributes(), "valueType");
        final Class<?> keyClass = getClassByName(eventManager, keyType);
        final Class<?> valueClass = getClassByName(eventManager, valueType);
        final Map<?, ?> map = newMap(keyClass, valueClass);
        forEach(variableNode, XmlReader::isNode, stringNode ->
        {
            switch (stringNode.getNodeName()) {
                case "entry": {
                    final NamedNodeMap attrs = stringNode.getAttributes();
                    ((Map<Object, Object>) map).put(parseObject(eventManager, keyType, parseString(attrs, "key")), parseObject(eventManager, valueType, parseString(attrs, "value")));
                    break;
                }
                case "item": {
                    final NamedNodeMap attrs = stringNode.getAttributes();
                    ((Map<Object, ItemHolder>) map).put(parseObject(eventManager, keyType, parseString(attrs, "key")), new ItemHolder(parseInteger(stringNode.getAttributes(), "id"), parseLong(stringNode.getAttributes(), "count")));
                    break;
                }
                case "skill": {
                    final NamedNodeMap attrs = stringNode.getAttributes();
                    ((Map<Object, SkillHolder>) map).put(parseObject(eventManager, keyType, parseString(attrs, "key")), new SkillHolder(parseInteger(stringNode.getAttributes(), "id"), parseInteger(stringNode.getAttributes(), "level")));
                    break;
                }
                case "location": {
                    final NamedNodeMap attrs = stringNode.getAttributes();
                    ((Map<Object, Location>) map).put(parseObject(eventManager, keyType, parseString(attrs, "key")), new Location(parseInteger(stringNode.getAttributes(), "x"), parseInteger(stringNode.getAttributes(), "y"), parseInteger(stringNode.getAttributes(), "z", parseInteger(stringNode.getAttributes(), "heading", 0))));
                    break;
                }
                default: {
                    LOGGER.warn(": Unhandled map case: " + name + " " + stringNode.getNodeName() + " for event: " + eventManager.getClass().getSimpleName());
                }
            }
        });
        variables.set(name, map);
    }

    private Class<?> getClassByName(AbstractEventManager<?> eventManager, String name) {
        switch (name) {
            case "Byte": {
                return Byte.class;
            }
            case "Short": {
                return Short.class;
            }
            case "Integer": {
                return Integer.class;
            }
            case "Float": {
                return Float.class;
            }
            case "Long": {
                return Long.class;
            }
            case "Double": {
                return Double.class;
            }
            case "String": {
                return String.class;
            }
            case "ItemHolder": {
                return ItemHolder.class;
            }
            case "SkillHolder": {
                return SkillHolder.class;
            }
            case "Location": {
                return Location.class;
            }
            default: {
                LOGGER.warn(": Unhandled class case: " + name + " for event: " + eventManager.getClass().getSimpleName());
                return Object.class;
            }
        }
    }

    private Object parseObject(AbstractEventManager<?> eventManager, String type, String value) {
        switch (type) {
            case "Byte": {
                return Byte.decode(value);
            }
            case "Short": {
                return Short.decode(value);
            }
            case "Integer": {
                return Integer.decode(value);
            }
            case "Float": {
                return Float.parseFloat(value);
            }
            case "Long": {
                return Long.decode(value);
            }
            case "Double": {
                return Double.parseDouble(value);
            }
            case "String": {
                return value;
            }
            default: {
                LOGGER.warn(": Unhandled object case: " + type + " for event: " + eventManager.getClass().getSimpleName());
                return null;
            }
        }
    }

    public static EventEngineData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final EventEngineData INSTANCE = new EventEngineData();
    }
}
