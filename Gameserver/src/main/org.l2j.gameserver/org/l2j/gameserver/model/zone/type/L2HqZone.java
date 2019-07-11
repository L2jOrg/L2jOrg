/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.model.zone.ZoneId;

/**
 * Zone where 'Build Headquarters' is allowed.
 *
 * @author Gnacik
 */
public class L2HqZone extends L2ZoneType {
    public L2HqZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if ("castleId".equals(name)) {
            // TODO
        } else if ("fortId".equals(name)) {
            // TODO
        } else if ("clanHallId".equals(name)) {
            // TODO
        } else if ("territoryId".equals(name)) {
            // TODO
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature character) {
        if (character.isPlayer()) {
            character.setInsideZone(ZoneId.HQ, true);
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (character.isPlayer()) {
            character.setInsideZone(ZoneId.HQ, false);
        }
    }
}
