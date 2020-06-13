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

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.enums.TeleportType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.teleporter.TeleportHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author UnAfraid
 * @author joeAlisson
 */
public class TeleportersData extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeleportersData.class);
    private final IntMap<Map<String, TeleportHolder>> teleporters = new HashIntMap<>();

    private TeleportersData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/teleporters/teleporterData.xsd");
    }

    @Override
    public void load() {
        teleporters.clear();
        parseDatapackDirectory("data/teleporters", true);
        LOGGER.info("Loaded: {} npc teleporters.", teleporters.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", list -> forEach(list, "npc", npc -> {

            final var teleportList = new HashMap<String, TeleportHolder>();
            final int npcId = parseInteger(npc.getAttributes(), "id");

            forEach(npc, node -> {
                switch (node.getNodeName()) {
                    case "teleport" -> parseTeleport(teleportList, npcId, node);
                    case "npcs" -> parseNpcs(teleportList, node);
                }
            });

            registerTeleportList(npcId, teleportList);
        }));
    }

    private void parseNpcs(final HashMap<String, TeleportHolder> teleportList, Node node) {
        forEach(node, "npc", npcNode -> registerTeleportList(parseInteger(npcNode.getAttributes(), "id"), teleportList));
    }

    private void parseTeleport(HashMap<String, TeleportHolder> teleportList, int npcId, Node node) {
        final NamedNodeMap nodeAttrs = node.getAttributes();

        final TeleportType type = parseEnum(nodeAttrs, TeleportType.class, "type");
        final String name = parseString(nodeAttrs, "name", type.name());

        final TeleportHolder holder = new TeleportHolder(name, type);
        forEach(node, "location", location -> holder.registerLocation(new StatsSet(parseAttributes(location))));

        if (nonNull(teleportList.putIfAbsent(name, holder))) {
            LOGGER.warn("Duplicate teleport list ({}) has been found for NPC: {}", name, npcId);
        }
    }

    /**
     * Register teleport data to global teleport list holder. Also show warning when any duplicate occurs.
     *
     * @param npcId    template id of teleporter
     * @param teleList teleport data to register
     */
    private void registerTeleportList(int npcId, Map<String, TeleportHolder> teleList) {
        teleporters.put(npcId, teleList);
    }

    /**
     * Gets teleport data for specified NPC and list name
     *
     * @param npcId    template id of teleporter
     * @param listName name of teleport list
     * @return {@link TeleportHolder} if found otherwise {@code null}
     */
    public TeleportHolder getHolder(int npcId, String listName) {
        return teleporters.getOrDefault(npcId, Collections.emptyMap()).get(listName);
    }

    public static TeleportersData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final TeleportersData INSTANCE = new TeleportersData();
    }
}