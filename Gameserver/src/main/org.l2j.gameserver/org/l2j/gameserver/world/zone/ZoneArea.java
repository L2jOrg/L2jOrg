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
package org.l2j.gameserver.world.zone;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * Abstract base class for any zone area
 *
 * @author durgus
 * @author JoeAlisson
 */
public interface ZoneArea {

    boolean isInside(int x, int y, int z);

    boolean intersectsRectangle(int x1, int x2, int y1, int y2);

    double distanceFrom(int x, int y);

    int getLowZ(); // Support for the ability to extract the z coordinates of zones.

    int getHighZ(); // New fishing patch makes use of that to get the Z for the hook

    void visualize(Player player, String zoneName);

    Location getRandomPoint();
}
