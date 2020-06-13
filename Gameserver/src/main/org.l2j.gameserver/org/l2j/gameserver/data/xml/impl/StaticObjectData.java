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

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * This class loads and holds all static object data.
 *
 * @author UnAfraid
 */
public final class StaticObjectData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticObjectData.class);

    private final Map<Integer, StaticWorldObject> _staticObjects = new HashMap<>();

    private StaticObjectData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/StaticObjects.xsd");
    }

    @Override
    public void load() {
        _staticObjects.clear();
        parseDatapackFile("data/StaticObjects.xml");
        LOGGER.info("Loaded {} static object templates.", _staticObjects.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("object".equalsIgnoreCase(d.getNodeName())) {
                        final NamedNodeMap attrs = d.getAttributes();
                        final StatsSet set = new StatsSet();
                        for (int i = 0; i < attrs.getLength(); i++) {
                            final Node att = attrs.item(i);
                            set.set(att.getNodeName(), att.getNodeValue());
                        }
                        addObject(set);
                    }
                }
            }
        }
    }

    /**
     * Initialize an static object based on the stats set and add it to the map.
     *
     * @param set the stats set to add.
     */
    private void addObject(StatsSet set) {
        final StaticWorldObject obj = new StaticWorldObject(new CreatureTemplate(new StatsSet()), set.getInt("id"));
        obj.setType(set.getInt("type", 0));
        obj.setName(set.getString("name"));
        obj.setMap(set.getString("texture", "none"), set.getInt("map_x", 0), set.getInt("map_y", 0));
        obj.spawnMe(set.getInt("x"), set.getInt("y"), set.getInt("z"));
        _staticObjects.put(obj.getObjectId(), obj);
    }

    /**
     * Gets the static objects.
     *
     * @return a collection of static objects.
     */
    public Collection<StaticWorldObject> getStaticObjects() {
        return _staticObjects.values();
    }

    public static StaticObjectData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final StaticObjectData INSTANCE = new StaticObjectData();
    }
}
