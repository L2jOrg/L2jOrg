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

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

/**
 * @author Sdw
 */
public class TeleportZone extends Zone {
    private int x = -1;
    private int y = -1;
    private int z = -1;

    public TeleportZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        switch (name) {
            case "oustX" -> x = Integer.parseInt(value);
            case "oustY" -> y = Integer.parseInt(value);
            case "oustZ" -> z = Integer.parseInt(value);
            default -> super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature creature) {
        creature.teleToLocation(new Location(x, y, z));
    }

    @Override
    protected void onExit(Creature creature) {
    }
}