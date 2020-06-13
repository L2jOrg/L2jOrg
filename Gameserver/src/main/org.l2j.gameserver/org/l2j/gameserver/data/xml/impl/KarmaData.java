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

import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author UnAfraid
 */
public class KarmaData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(KarmaData.class);

    private final Map<Integer, Double> _karmaTable = new HashMap<>();

    private KarmaData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/pcKarmaIncrease.xsd");
    }

    @Override
    public synchronized void load() {
        _karmaTable.clear();
        parseDatapackFile("data/stats/chars/pcKarmaIncrease.xml");
        LOGGER.info("Loaded {} karma modifiers.", _karmaTable.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("pcKarmaIncrease".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("increase".equalsIgnoreCase(d.getNodeName())) {
                        final NamedNodeMap attrs = d.getAttributes();
                        final int level = parseInteger(attrs, "lvl");
                        _karmaTable.put(level, parseDouble(attrs, "val"));
                    }
                }
            }
        }
    }

    /**
     * @param level
     * @return {@code double} modifier used to calculate karma lost upon death.
     */
    public double getMultiplier(int level) {
        return _karmaTable.get(level);
    }

    public static KarmaData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final KarmaData INSTANCE = new KarmaData();
    }
}
