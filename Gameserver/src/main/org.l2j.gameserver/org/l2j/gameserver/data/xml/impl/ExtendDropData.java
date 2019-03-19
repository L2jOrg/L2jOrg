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
package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.handler.ConditionHandler;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.conditions.ICondition;
import org.l2j.gameserver.model.holders.ExtendDropDataHolder;
import org.l2j.gameserver.model.holders.ExtendDropItemHolder;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;
import java.util.function.Function;


/**
 * @author Sdw
 */
public class ExtendDropData implements IGameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendDropData.class);
    private final Map<Integer, ExtendDropDataHolder> _extendDrop = new HashMap<>();

    protected ExtendDropData() {
        load();
    }

    public static ExtendDropData getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void load() {
        _extendDrop.clear();
        parseDatapackFile("data/ExtendDrop.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _extendDrop.size() + " ExtendDrop.");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "drop", dropNode ->
        {
            final StatsSet set = new StatsSet(parseAttributes(dropNode));

            final List<ExtendDropItemHolder> items = new ArrayList<>(1);
            forEach(dropNode, "items", itemsNode -> forEach(itemsNode, "item", itemNode ->
            {
                final int itemId = parseInteger(itemNode.getAttributes(), "id");
                final int itemCount = parseInteger(itemNode.getAttributes(), "count");
                final int itemMaxCount = parseInteger(itemNode.getAttributes(), "maxCount");
                final double itemChance = parseDouble(itemNode.getAttributes(), "chance");
                final double itemAdditionalChance = parseDouble(itemNode.getAttributes(), "additionalChance");
                items.add(new ExtendDropItemHolder(itemId, itemCount, itemMaxCount, itemChance, itemAdditionalChance));
            }));
            set.set("items", items);

            final List<ICondition> conditions = new ArrayList<>(1);
            forEach(dropNode, "conditions", conditionsNode -> forEach(conditionsNode, "condition", conditionNode ->
            {
                final String conditionName = parseString(conditionNode.getAttributes(), "name");
                final StatsSet params = (StatsSet) parseValue(conditionNode);
                final Function<StatsSet, ICondition> conditionFunction = ConditionHandler.getInstance().getHandlerFactory(conditionName);
                if (conditionFunction != null) {
                    conditions.add(conditionFunction.apply(params));
                } else {
                    LOGGER.warn(": Missing condition for ExtendDrop Id[" + set.getInt("id") + "] Condition Name[" + conditionName + "]");
                }

            }));
            set.set("conditions", conditions);

            final Map<Long, SystemMessageId> systemMessages = new HashMap<>();
            forEach(dropNode, "systemMessages", systemMessagesNode -> forEach(systemMessagesNode, "systemMessage", systemMessageNode ->
            {
                final long amount = parseLong(systemMessageNode.getAttributes(), "amount");
                final SystemMessageId systemMessageId = SystemMessageId.getSystemMessageId(parseInteger(systemMessageNode.getAttributes(), "id"));
                systemMessages.put(amount, systemMessageId);
            }));
            set.set("systemMessages", systemMessages);

            _extendDrop.put(set.getInt("id"), new ExtendDropDataHolder(set));
        }));
    }

    private Object parseValue(Node node) {
        StatsSet statsSet = null;
        List<Object> list = null;
        Object text = null;
        for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
            final String nodeName = node.getNodeName();
            switch (node.getNodeName()) {
                case "#text": {
                    final String value = node.getNodeValue().trim();
                    if (!value.isEmpty()) {
                        text = value;
                    }
                    break;
                }
                case "item": {
                    if (list == null) {
                        list = new LinkedList<>();
                    }

                    final Object value = parseValue(node);
                    if (value != null) {
                        list.add(value);
                    }
                    break;
                }
                default: {
                    final Object value = parseValue(node);
                    if (value != null) {
                        if (statsSet == null) {
                            statsSet = new StatsSet();
                        }

                        statsSet.set(nodeName, value);
                    }
                }
            }
        }
        if (list != null) {
            if (text != null) {
                throw new IllegalArgumentException("Text and list in same node are not allowed. Node[" + node + "]");
            }
            if (statsSet != null) {
                statsSet.set(".", list);
            } else {
                return list;
            }
        }
        if (text != null) {
            if (list != null) {
                throw new IllegalArgumentException("Text and list in same node are not allowed. Node[" + node + "]");
            }
            if (statsSet != null) {
                statsSet.set(".", text);
            } else {
                return text;
            }
        }
        return statsSet;
    }

    public ExtendDropDataHolder getExtendDropById(int id) {
        return _extendDrop.getOrDefault(id, null);
    }

    private static class SingletonHolder {
        protected static final ExtendDropData _instance = new ExtendDropData();
    }
}
