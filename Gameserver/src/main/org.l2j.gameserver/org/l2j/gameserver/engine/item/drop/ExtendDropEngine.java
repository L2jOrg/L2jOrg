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
package org.l2j.gameserver.engine.item.drop;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.handler.ExtendDropConditionHandler;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class ExtendDropEngine extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendDropEngine.class);
    private final IntMap<ExtendDrop> drops = new HashIntMap<>();

    private ExtendDropEngine() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/xsd/extend-drop.xsd");
    }

    @Override
    public void load() {
        drops.clear();
        parseDatapackFile("data/extend-drop.xml");
        LOGGER.info("Loaded {} ExtendDrop.", drops.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        var listNode = doc.getFirstChild();
        for(var dropNode = listNode.getFirstChild(); dropNode != null; dropNode = dropNode.getNextSibling()) {
            parseDrop(dropNode);
        }
    }

    private void parseDrop(Node dropNode) {
        List<ExtendDropItem> items = new ArrayList<>();
        List<ExtendDropCondition> conditions = new ArrayList<>();
        for(var node = dropNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            String nodeName = node.getNodeName();
            if ("items".equals(nodeName)) {
                parseItems(node, items);
            } else if ("conditions".equals(nodeName)) {
                parseConditions(node, conditions);
            }
        }

        var id = parseInt(dropNode.getAttributes(), "id");
        items = items.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(items);
        conditions = conditions.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(conditions);
        drops.put(id, new ExtendDrop(items, conditions));
    }

    private void parseConditions(Node conditionsNode, List<ExtendDropCondition> conditions) {
        for(var node = conditionsNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            var factory = ExtendDropConditionHandler.getInstance().getHandlerFactory(node.getNodeName());
            if(factory != null) {
                conditions.add(factory.apply(node));
            } else {
                LOGGER.warn("Extend Drop Condition Factory not found {}", node.getNodeName());
            }
        }
    }

    private void parseItems(Node itemsNode, List<ExtendDropItem> items) {
        for(var node = itemsNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            var attr = node.getAttributes();
            var id = parseInt(attr, "id");
            var count = parseLong(attr, "count");
            var chance = parseFloat(attr, "chance");
            var maxCount = parseLong(attr, "max-count");
            items.add(new ExtendDropItem(id, count, maxCount, chance));
        }
    }

    public ExtendDrop getExtendDropById(int id) {
        return drops.getOrDefault(id, null);
    }

    public static void init() {
        getInstance().load();
    }

    public static ExtendDropEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ExtendDropEngine INSTANCE = new ExtendDropEngine();
    }
}
