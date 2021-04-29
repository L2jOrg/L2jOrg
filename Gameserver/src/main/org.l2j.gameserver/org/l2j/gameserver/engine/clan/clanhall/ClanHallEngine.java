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
package org.l2j.gameserver.engine.clan.clanhall;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.enums.ClanHallGrade;
import org.l2j.gameserver.enums.ClanHallType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * @author St3eT
 * @author JoeAlisson
 */
public final class ClanHallEngine extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClanHallEngine.class);

    private final IntMap<ClanHall> clanHalls = new HashIntMap<>();

    private ClanHallEngine() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/residences/clanHalls/clanHall.xsd");
    }

    @Override
    public void load() {
        parseDatapackDirectory("data/residences/clanHalls", true);
        LOGGER.info("Loaded {} Clan Halls.", clanHalls.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        var listNode = doc.getFirstChild();
        for (var node = listNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            parseClanHall(node);
        }
    }

    private void parseClanHall(Node clanHallNode) {
        var attr = clanHallNode.getAttributes();
        var id = parseInt(attr, "id");
        var name = parseString(attr, "name");
        var grade = parseEnum(attr, ClanHallGrade.class, "grade");
        var type = parseEnum(attr, ClanHallType.class, "type");

        var clanHall = new ClanHall(id, name, grade, type);

        for(var node = clanHallNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            switch (node.getNodeName()) {
                case "auction" -> parseAuction(node, clanHall);
                case "npcs" -> clanHall.setNpcs(parseIntList(node));
                case "doors" -> parseDoors(node, clanHall);
                case "restart-point" -> clanHall.setRestartPoint(parseLocation(node));
                case "banish-point" -> clanHall.setBanishPoint(parseLocation(node));
                default -> LOGGER.warn("unknown node {}", node.getNodeName());
            }
        }
        clanHall.init();
        clanHalls.put(id, clanHall);
    }

    private void parseDoors(Node node, ClanHall clanHall) {
        var doorDataManager = DoorDataManager.getInstance();
        for (int id : parseIntArray(node)) {
            var door = doorDataManager.getDoor(id);
            if(nonNull(door)) {
                clanHall.addDoor(door);
            } else {
                LOGGER.warn("Unknown door id {} in {}", id, clanHall);
            }
        }
    }

    private void parseAuction(Node node, ClanHall clanHall) {
        var attr = node.getAttributes();
        clanHall.setMinBid(parseLong(attr, "min-bid"));
        clanHall.setLease(parseLong(attr, "lease"));
    }

    public ClanHall getClanHallById(int clanHallId) {
        return clanHalls.get(clanHallId);
    }

    public Collection<ClanHall> getClanHalls() {
        return clanHalls.values();
    }

    public void forEachClanHall(Consumer<ClanHall> action) {
        clanHalls.values().forEach(action);
    }

    public ClanHall getClanHallByNpcId(int npcId) {
        for (ClanHall clanHall : clanHalls.values()) {
            if(clanHall.getNpcs().contains(npcId)) {
                return clanHall;
            }
        }
        return null;
    }

    public ClanHall getClanHallByClan(Clan clan) {
        for (ClanHall clanHall : clanHalls.values()) {
            if(clanHall.getOwner() == clan) {
                return clanHall;
            }
        }
        return null;
    }

    public ClanHall getClanHallByDoorId(int doorId) {
        var door = DoorDataManager.getInstance().getDoor(doorId);
        for (ClanHall clanHall : clanHalls.values()) {
            if(clanHall.hasDoor(door)) {
                return clanHall;
            }
        }
        return null;
    }

    public List<ClanHall> getFreeAuctionableHall() {
        return clanHalls.values().stream().filter(this::isNotOwnedAuctionable).sorted(Comparator.comparingInt(ClanHall::getId)).collect(Collectors.toList());
    }

    private boolean isNotOwnedAuctionable(ClanHall ch) {
        return (ch.getType() == ClanHallType.AUCTIONABLE) && (ch.getOwner() == null);
    }

    public static void init() {
        getInstance().load();
    }

    public static ClanHallEngine getInstance() {
        return Singleton.INSTANCE;
    }

    public int getClanHallAmount() {
        return clanHalls.size();
    }

    private static class Singleton {
        private static final ClanHallEngine INSTANCE = new ClanHallEngine();
    }
}
