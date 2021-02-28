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
package org.l2j.gameserver.engine.siege;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.ArtifactSpawn;
import org.l2j.gameserver.util.GameXmlReader;
import org.w3c.dom.Node;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
class SiegeSettings {

    final IntMap<EnumSet<DayOfWeek>> siegeScheduleDays = new HashIntMap<>();
    final IntMap<ArtifactSpawn> castleLords = new HashIntMap<>();
    final IntMap<Collection<ArtifactSpawn>> controlTowers = new HashIntMap<>();
    final IntMap<Collection<ArtifactSpawn>> flameTowers = new HashIntMap<>();
    int maxSiegesInDay;
    int minClanLevel;
    int maxAttackers;
    int maxDefenders;
    int minMercenaryLevel;
    int maxMercenaries;

    private SiegeSettings() { }

    static SiegeSettings parse(GameXmlReader reader, Node configNode) {
        SiegeSettings settings = new SiegeSettings();
        final var siegeConfig= configNode.getFirstChild();

        if(nonNull(siegeConfig) && siegeConfig.getNodeName().equals("siege-config")) {
            settings.parseConfig(siegeConfig, reader);
        }
        return settings;
    }

    private void parseConfig(Node siegeConfig, GameXmlReader reader) {
        for(var node = siegeConfig.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            if(node.getNodeName().equals("castle")) {
                parseCastleInfo(reader, node);
            }
        }
        var attr = siegeConfig.getAttributes();
        maxSiegesInDay = reader.parseInt(attr, "max-in-day");
        minClanLevel = reader.parseInt(attr, "min-clan-level");
        maxAttackers = reader.parseInt(attr, "max-attackers");
        maxDefenders = reader.parseInt(attr, "max-defenders");
        minMercenaryLevel = reader.parseInt(attr, "min-mercenary-level");
        maxMercenaries = reader.parseInt(attr, "min-mercenary-level");
    }

    private void parseCastleInfo(GameXmlReader reader, Node castleNode) {
        var castleId = reader.parseInt(castleNode.getAttributes(), "id");
        for(var node = castleNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            switch (node.getNodeName()) {
                case "scheduled-days" -> siegeScheduleDays.put(castleId, reader.parseEnumSet(node, DayOfWeek.class));
                case "castle-lord" -> parseCastleLord(castleId, reader, node);
                case "control-tower" -> parseControlTower(castleId, reader, node);
                case "flame-tower" -> parseFlameTower(castleId, reader, node);
            }
        }
        var days = reader.parseEnumSet(castleNode.getFirstChild(), DayOfWeek.class);
        siegeScheduleDays.put(castleId, days);

    }

    private void parseCastleLord(int castleId, GameXmlReader reader, Node node) {
        var id = reader.parseInt(node.getAttributes(), "id");
        var location = reader.parseLocation(node);
        castleLords.put(castleId, new ArtifactSpawn(id, location));
    }

    private void parseFlameTower(int castleId, GameXmlReader reader, Node flameNode) {
        var towerId = reader.parseInt(flameNode.getAttributes(), "id");
        var location = reader.parseLocation(flameNode);
        var zones = reader.parseIntList(flameNode.getFirstChild());
        flameTowers.computeIfAbsent(castleId, id -> new ArrayList<>()).add(new ArtifactSpawn(towerId, location, zones));
    }

    private void parseControlTower(int castleId, GameXmlReader reader, Node controlNode) {
        var towerId = reader.parseInt(controlNode.getAttributes(), "id");
        var location = reader.parseLocation(controlNode);
        controlTowers.computeIfAbsent(castleId, id -> new ArrayList<>()).add(new ArtifactSpawn(towerId, location));
    }
}
