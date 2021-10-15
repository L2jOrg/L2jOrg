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
package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class ConditionZone extends Zone {
    private final boolean allowDrop;
    private final boolean allowBookmark;

    private ConditionZone(int id, boolean allowBookmark, boolean allowDrop) {
        super(id);
        this.allowBookmark = allowBookmark;
        this.allowDrop = allowDrop;
    }

    @Override
    protected void onEnter(Creature creature) {
        if (isPlayer(creature)) {
            if (!allowBookmark) {
                creature.setInsideZone(ZoneType.NO_BOOKMARK, true);
            }
            if (!allowDrop) {
                creature.setInsideZone(ZoneType.NO_ITEM_DROP, true);
            }
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (isPlayer(creature)) {
            if (!allowBookmark) {
                creature.setInsideZone(ZoneType.NO_BOOKMARK, false);
            }
            if (!allowDrop) {
                creature.setInsideZone(ZoneType.NO_ITEM_DROP, false);
            }
        }
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            var attr = zoneNode.getAttributes();
            var allowBookmark = reader.parseBoolean(attr, "allow-bookmark");
            var allowDrop = reader.parseBoolean(attr, "allow-droop");
            return new ConditionZone(id, allowBookmark, allowDrop);
        }

        @Override
        public String type() {
            return "condition";
        }
    }
}
