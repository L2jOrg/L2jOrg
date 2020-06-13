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
package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class ConditionZone extends Zone {
    private boolean NO_ITEM_DROP = false;
    private boolean NO_BOOKMARK = false;

    public ConditionZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equalsIgnoreCase("NoBookmark")) {
            NO_BOOKMARK = Boolean.parseBoolean(value);
        } else if (name.equalsIgnoreCase("NoItemDrop")) {
            NO_ITEM_DROP = Boolean.parseBoolean(value);
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature character) {
        if (isPlayer(character)) {
            if (NO_BOOKMARK) {
                character.setInsideZone(ZoneType.NO_BOOKMARK, true);
            }
            if (NO_ITEM_DROP) {
                character.setInsideZone(ZoneType.NO_ITEM_DROP, true);
            }
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (isPlayer(character)) {
            if (NO_BOOKMARK) {
                character.setInsideZone(ZoneType.NO_BOOKMARK, false);
            }
            if (NO_ITEM_DROP) {
                character.setInsideZone(ZoneType.NO_ITEM_DROP, false);
            }
        }
    }
}
