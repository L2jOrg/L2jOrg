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

import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.enums.SiegeGuardType;
import org.l2j.gameserver.model.holders.CastleSpawnHolder;
import org.l2j.gameserver.model.holders.SiegeGuardHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author St3eT
 */
public final class CastleDataManager extends GameXmlReader {
    private static final Map<Integer, List<SiegeGuardHolder>> _siegeGuards = new HashMap<>();
    private final Map<Integer, List<CastleSpawnHolder>> _spawns = new HashMap<>();

    private CastleDataManager() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/castleData.xsd");
    }

    @Override
    public void load() {
        _spawns.clear();
        _siegeGuards.clear();
        parseDatapackDirectory("data/residences/castles", true);
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node listNode = doc.getFirstChild(); listNode != null; listNode = listNode.getNextSibling()) {
            if ("list".equals(listNode.getNodeName())) {
                for (Node castleNode = listNode.getFirstChild(); castleNode != null; castleNode = castleNode.getNextSibling()) {
                    if ("castle".equals(castleNode.getNodeName())) {
                        final int castleId = parseInteger(castleNode.getAttributes(), "id");
                        for (Node tpNode = castleNode.getFirstChild(); tpNode != null; tpNode = tpNode.getNextSibling()) {
                            final List<CastleSpawnHolder> spawns = new ArrayList<>();

                            if ("spawns".equals(tpNode.getNodeName())) {
                                for (Node npcNode = tpNode.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling()) {
                                    if ("npc".equals(npcNode.getNodeName())) {
                                        final NamedNodeMap np = npcNode.getAttributes();
                                        final int npcId = parseInteger(np, "id");
                                        final CastleSide side = parseEnum(np, CastleSide.class, "castleSide", CastleSide.NEUTRAL);
                                        final int x = parseInteger(np, "x");
                                        final int y = parseInteger(np, "y");
                                        final int z = parseInteger(np, "z");
                                        final int heading = parseInteger(np, "heading");

                                        spawns.add(new CastleSpawnHolder(npcId, side, x, y, z, heading));
                                    }
                                }
                                _spawns.put(castleId, spawns);
                            } else if ("siegeGuards".equals(tpNode.getNodeName())) {
                                final List<SiegeGuardHolder> guards = new ArrayList<>();

                                for (Node npcNode = tpNode.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling()) {
                                    if ("guard".equals(npcNode.getNodeName())) {
                                        final NamedNodeMap np = npcNode.getAttributes();
                                        final int itemId = parseInteger(np, "itemId");
                                        final SiegeGuardType type = parseEnum(tpNode.getAttributes(), SiegeGuardType.class, "type");
                                        final boolean stationary = parseBoolean(np, "stationary", false);
                                        final int npcId = parseInteger(np, "npcId");
                                        final int npcMaxAmount = parseInteger(np, "npcMaxAmount");

                                        guards.add(new SiegeGuardHolder(castleId, itemId, type, stationary, npcId, npcMaxAmount));
                                    }
                                }
                                _siegeGuards.put(castleId, guards);
                            }
                        }
                    }
                }
            }
        }
    }

    public final List<CastleSpawnHolder> getSpawnsForSide(int castleId, CastleSide side) {
        return _spawns.getOrDefault(castleId, Collections.emptyList()).stream().filter(s -> s.getSide() == side).collect(Collectors.toList());
    }

    public final List<SiegeGuardHolder> getSiegeGuardsForCastle(int castleId) {
        return _siegeGuards.getOrDefault(castleId, Collections.emptyList());
    }

    public static CastleDataManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final CastleDataManager INSTANCE = new CastleDataManager();
    }
}
