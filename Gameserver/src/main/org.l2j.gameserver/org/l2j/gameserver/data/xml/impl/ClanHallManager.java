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
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.enums.ClanHallGrade;
import org.l2j.gameserver.enums.ClanHallType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.holders.ClanHallTeleportHolder;
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
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author St3eT
 * @author JoeAlisson
 */
public final class ClanHallManager extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClanHallManager.class);

    private final IntMap<ClanHall> clanHalls = new HashIntMap<>();

    private ClanHallManager() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/residences/clanHalls/clanHall.xsd");
    }

    @Override
    public void load() {
        parseDatapackDirectory("data/residences/clanHalls", true);
        LOGGER.info("Loaded {} Clan Halls.", clanHalls.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        final List<Door> doors = new ArrayList<>();
        final List<Integer> npcs = new ArrayList<>();
        final List<ClanHallTeleportHolder> teleports = new ArrayList<>();
        final StatsSet params = new StatsSet();

        for (Node listNode = doc.getFirstChild(); listNode != null; listNode = listNode.getNextSibling()) {
            if ("list".equals(listNode.getNodeName())) {
                for (Node clanHallNode = listNode.getFirstChild(); clanHallNode != null; clanHallNode = clanHallNode.getNextSibling()) {
                    if ("clanHall".equals(clanHallNode.getNodeName())) {
                        params.set("id", parseInteger(clanHallNode.getAttributes(), "id"));
                        params.set("name", parseString(clanHallNode.getAttributes(), "name", "None"));
                        params.set("grade", parseEnum(clanHallNode.getAttributes(), ClanHallGrade.class, "grade", ClanHallGrade.NONE));
                        params.set("type", parseEnum(clanHallNode.getAttributes(), ClanHallType.class, "type", ClanHallType.OTHER));

                        for (Node tpNode = clanHallNode.getFirstChild(); tpNode != null; tpNode = tpNode.getNextSibling()) {
                            switch (tpNode.getNodeName()) {
                                case "auction": {
                                    final NamedNodeMap at = tpNode.getAttributes();
                                    params.set("minBid", parseInteger(at, "min-bid"));
                                    params.set("lease", parseInteger(at, "lease"));
                                    params.set("deposit", parseInteger(at, "deposit"));
                                    break;
                                }
                                case "npcs": {
                                    for (Node npcNode = tpNode.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling()) {
                                        if ("npc".equals(npcNode.getNodeName())) {
                                            final NamedNodeMap np = npcNode.getAttributes();
                                            final int npcId = parseInteger(np, "id");
                                            npcs.add(npcId);
                                        }
                                    }
                                    params.set("npcList", npcs);
                                    break;
                                }
                                case "doors": {
                                    for (Node npcNode = tpNode.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling()) {
                                        if ("door".equals(npcNode.getNodeName())) {
                                            final NamedNodeMap np = npcNode.getAttributes();
                                            final int doorId = parseInteger(np, "id");
                                            final Door door = DoorDataManager.getInstance().getDoor(doorId);
                                            if (door != null) {
                                                doors.add(door);
                                            }
                                        }
                                    }
                                    params.set("doorList", doors);
                                    break;
                                }
                                case "teleportList": {
                                    for (Node npcNode = tpNode.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling()) {
                                        if ("teleport".equals(npcNode.getNodeName())) {
                                            final NamedNodeMap np = npcNode.getAttributes();
                                            final int npcStringId = parseInteger(np, "npcStringId");
                                            final int x = parseInteger(np, "x");
                                            final int y = parseInteger(np, "y");
                                            final int z = parseInteger(np, "z");
                                            final int minFunctionLevel = parseInteger(np, "minFunctionLevel");
                                            final int cost = parseInteger(np, "cost");
                                            teleports.add(new ClanHallTeleportHolder(npcStringId, x, y, z, minFunctionLevel, cost));
                                        }
                                    }
                                    params.set("teleportList", teleports);
                                    break;
                                }
                                case "ownerRestartPoint": {
                                    final NamedNodeMap ol = tpNode.getAttributes();
                                    params.set("owner_loc", new Location(parseInteger(ol, "x"), parseInteger(ol, "y"), parseInteger(ol, "z")));
                                    break;
                                }
                                case "banishPoint": {
                                    final NamedNodeMap bl = tpNode.getAttributes();
                                    params.set("banish_loc", new Location(parseInteger(bl, "x"), parseInteger(bl, "y"), parseInteger(bl, "z")));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        clanHalls.put(params.getInt("id"), new ClanHall(params));
    }

    public ClanHall getClanHallById(int clanHallId) {
        return clanHalls.get(clanHallId);
    }

    public Collection<ClanHall> getClanHalls() {
        return clanHalls.values();
    }

    public ClanHall getClanHallByNpcId(int npcId) {
        return clanHalls.values().stream().filter(ch -> ch.getNpcs().contains(npcId)).findFirst().orElse(null);
    }

    public ClanHall getClanHallByClan(Clan clan) {
        return clanHalls.values().stream().filter(ch -> ch.getOwner() == clan).findFirst().orElse(null);
    }

    public ClanHall getClanHallByDoorId(int doorId) {
        final Door door = DoorDataManager.getInstance().getDoor(doorId);
        return clanHalls.values().stream().filter(ch -> ch.getDoors().contains(door)).findFirst().orElse(null);
    }

    public List<ClanHall> getFreeAuctionableHall() {
        return clanHalls.values().stream().filter(ch -> (ch.getType() == ClanHallType.AUCTIONABLE) && (ch.getOwner() == null)).sorted(Comparator.comparingInt(ClanHall::getId)).collect(Collectors.toList());
    }

    public static void init() {
        getInstance().load();
    }

    public static ClanHallManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ClanHallManager INSTANCE = new ClanHallManager();
    }
}
