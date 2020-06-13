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
package org.l2j.gameserver.world.zone;

import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * Abstract base class for any zone area
 *
 * @author durgus
 */
public abstract class ZoneArea {
    protected static final int STEP = 10;

    public abstract boolean isInsideZone(int x, int y, int z);

    public abstract boolean intersectsRectangle(int x1, int x2, int y1, int y2);

    public abstract double getDistanceToZone(int x, int y);

    public abstract int getLowZ(); // Support for the ability to extract the z coordinates of zones.

    public abstract int getHighZ(); // New fishing patch makes use of that to get the Z for the hook

    public abstract void visualizeZone(int z);

    // TODO Drop this
    protected final void dropDebugItem(int itemId, int num, int x, int y, int z) {

        final Item item = new Item(IdFactory.getInstance().getNextId(), itemId);
        item.setCount(num);
        item.spawnMe(x, y, z + 5);
        ZoneManager.getInstance().getDebugItems().add(item);
    }

    public abstract Location getRandomPoint();
}
