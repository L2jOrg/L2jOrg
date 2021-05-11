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
package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.residences.ResidenceFunctionTemplate;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * The residence functions data
 *
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class ResidenceFunctionsData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResidenceFunctionsData.class);
    private final IntMap<List<ResidenceFunctionTemplate>> functions = new HashIntMap<>();

    private ResidenceFunctionsData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/xsd/ResidenceFunctions.xsd");
    }

    @Override
    public void load() {
        functions.clear();
        parseDatapackFile("data/ResidenceFunctions.xml");
        LOGGER.info("Loaded: {} functions.", functions.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", list -> forEach(list, "function", func ->
        {
            final NamedNodeMap attrs = func.getAttributes();
            final StatsSet set = new StatsSet(HashMap::new);
            for (int i = 0; i < attrs.getLength(); i++) {
                final Node node = attrs.item(i);
                set.set(node.getNodeName(), node.getNodeValue());
            }
            forEach(func, "function", levelNode ->
            {
                final NamedNodeMap levelAttrs = levelNode.getAttributes();
                final StatsSet levelSet = new StatsSet(HashMap::new);
                levelSet.merge(set);
                for (int i = 0; i < levelAttrs.getLength(); i++) {
                    final Node node = levelAttrs.item(i);
                    levelSet.set(node.getNodeName(), node.getNodeValue());
                }
                final ResidenceFunctionTemplate template = new ResidenceFunctionTemplate(levelSet);
                functions.computeIfAbsent(template.getId(), key -> new ArrayList<>()).add(template);
            });
        }));
    }


    public ResidenceFunctionTemplate getFunction(int id, int level) {
        return functions.getOrDefault(id, Collections.emptyList()).stream().filter(template -> template.getLevel() == level).findAny().orElse(null);
    }

    public static void init() {
        getInstance().load();
    }

    public static ResidenceFunctionsData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ResidenceFunctionsData INSTANCE = new ResidenceFunctionsData();
    }
}
