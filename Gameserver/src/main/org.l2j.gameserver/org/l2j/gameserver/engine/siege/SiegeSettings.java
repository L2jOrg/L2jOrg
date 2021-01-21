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
import org.l2j.gameserver.util.GameXmlReader;
import org.w3c.dom.Node;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
class SiegeSettings {

    final IntMap<Collection<SiegeSchedule>> siegeSchedules = new HashIntMap<>();
    int maxSiegesInDay;
    int minClanLevel;
    int maxAttackers;
    int maxDefenders;
    int minMercenaryLevel;
    int maxMercenaries;

    private SiegeSettings() {

    }

    static SiegeSettings parse(GameXmlReader reader, Node configNode) {
        SiegeSettings settings = new SiegeSettings();
        final var siegeConfig= configNode.getFirstChild();

        if(nonNull(siegeConfig) && siegeConfig.getNodeName().equals("siege-config")) {
            for(var node = siegeConfig.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
                if(node.getNodeName().equals("castle")) {
                    parseCastleInfo(settings, reader, node);
                }
            }
            var attr = siegeConfig.getAttributes();
            settings.maxSiegesInDay = reader.parseInt(attr, "max-in-day");
            settings.minClanLevel = reader.parseInt(attr, "min-clan-level");
            settings.maxAttackers = reader.parseInt(attr, "max-attackers");
            settings.maxDefenders = reader.parseInt(attr, "max-defenders");
            settings.minMercenaryLevel = reader.parseInt(attr, "min-mercenary-level");
            settings.maxMercenaries = reader.parseInt(attr, "min-mercenary-level");
        }

        return settings;
    }

    private static void parseCastleInfo(SiegeSettings settings, GameXmlReader reader, Node castleNode) {
        var id = reader.parseInt(castleNode.getAttributes(), "id");
        for(var node = castleNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            if(node.getNodeName().equals("scheduled-day")) {
                var attrs = node.getAttributes();
                var day = reader.parseEnum(attrs, DayOfWeek.class, "day");
                var hour = reader.parseInt(attrs, "hour");
                settings.siegeSchedules.computeIfAbsent(id, i -> new ArrayList<>()).add(new SiegeSchedule(day, hour));
            }
        }

    }
}
