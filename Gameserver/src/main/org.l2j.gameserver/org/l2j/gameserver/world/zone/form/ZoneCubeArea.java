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
package org.l2j.gameserver.world.zone.form;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExServerPrimitive;
import org.l2j.gameserver.world.zone.ZoneArea;

import java.awt.*;

/**
 * A primitive rectangular zone
 *
 * @author durgus
 * @author JoeAlisson
 */
public class ZoneCubeArea implements ZoneArea {
    private final int minZ;
    private final int maxZ;
    private final Rectangle rectangle;

    public ZoneCubeArea(int x1, int x2, int y1, int y2, int z1, int z2) {
        final int minX = Math.min(x1, x2);
        final int maxX = Math.max(x1, x2);
        final int minY = Math.min(y1, y2);
        final int maxY = Math.max(y1, y2);

        rectangle = new Rectangle(minX, minY, maxX - minX, maxY - minY);

        minZ = Math.min(z1, z2);
        maxZ = Math.max(z1, z2);
    }

    @Override
    public boolean isInside(int x, int y, int z) {
        return z >= minZ && z <= maxZ && rectangle.contains(x, y);
    }

    @Override
    public boolean intersectsRectangle(int x1, int x2, int y1, int y2) {
        return rectangle.intersects(x1, y1, (double) x2 - x1, y2 - y1);
    }

    @Override
    public double distanceFrom(int x, int y) {
        final int _x1 = rectangle.x;
        final int _x2 = rectangle.x + rectangle.width;
        final int _y1 = rectangle.y;
        final int _y2 = rectangle.y + rectangle.height;
        double test = Math.pow(_x1 - x, 2) + Math.pow(_y2 - y, 2);
        double shortestDist = Math.pow(_x1 - x, 2) + Math.pow(_y1 - y, 2);

        if (test < shortestDist) {
            shortestDist = test;
        }

        test = Math.pow(_x2 - x, 2) + Math.pow(_y1 - y, 2);
        if (test < shortestDist) {
            shortestDist = test;
        }

        test = Math.pow(_x2 - x, 2) + Math.pow(_y2 - y, 2);
        if (test < shortestDist) {
            shortestDist = test;
        }

        return Math.sqrt(shortestDist);
    }

    @Override
    public void visualize(Player player, String zoneName) {
        var z = player.getZ() + (int) (player.getCollisionHeight() / 2);
        var maxX = rectangle.x + rectangle.width;
        var maxY = rectangle.y + rectangle.height;

        var primitive = new ExServerPrimitive(zoneName, rectangle.x, rectangle.y, z);
        primitive.addLine(zoneName +"_00", Color.BLUE, false, rectangle.x, rectangle.y, z, maxX, rectangle.y, z);
        primitive.addLine(Color.BLUE, rectangle.x, rectangle.y, minZ, rectangle.x, rectangle.y, maxZ);
        primitive.addLine(Color.BLUE, rectangle.x, rectangle.y, minZ, maxX, rectangle.y, minZ);
        primitive.addLine(Color.BLUE, rectangle.x, rectangle.y, maxZ, maxX, rectangle.y, maxZ);

        primitive.addLine(zoneName +"_10", Color.BLUE, false, maxX, rectangle.y, z, maxX, maxY, z);
        primitive.addLine(Color.BLUE, maxX, rectangle.y, minZ, maxX, rectangle.y, maxZ);
        primitive.addLine(Color.BLUE, maxX, rectangle.y, minZ, maxX, maxY, minZ);
        primitive.addLine(Color.BLUE, maxX, rectangle.y, maxZ, maxX, maxY, maxZ);

        primitive.addLine(zoneName +"_11", Color.BLUE, false, maxX, maxY, z, rectangle.x, maxY, z);
        primitive.addLine(Color.BLUE, maxX, maxY, minZ, maxX, maxY, maxZ);
        primitive.addLine(Color.BLUE, maxX, maxY, minZ, rectangle.x, maxY, minZ);
        primitive.addLine(Color.BLUE, maxX, maxY, maxZ, rectangle.x, maxY, maxZ);

        primitive.addLine(zoneName +"_01", Color.BLUE, false, rectangle.x, maxY, z, rectangle.x, rectangle.y, z);
        primitive.addLine(Color.BLUE, rectangle.x, maxY, minZ, rectangle.x, maxY, maxZ);
        primitive.addLine(Color.BLUE, rectangle.x, maxY, minZ, rectangle.x, rectangle.y, minZ);
        primitive.addLine(Color.BLUE, rectangle.x, maxY, maxZ, rectangle.x, rectangle.y, maxZ);

        player.sendPacket(primitive);
    }

    @Override
    public Location getRandomPoint() {
        final int x = Rnd.get(rectangle.x, rectangle.x + rectangle.width);
        final int y = Rnd.get(rectangle.y, rectangle.y + rectangle.height);

        return new Location(x, y, GeoEngine.getInstance().getHeight(x, y, minZ));
    }

    @Override
    public int getLowZ() {
        return minZ;
    }

    @Override
    public int getHighZ() {
        return maxZ;
    }
}
