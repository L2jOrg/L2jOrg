/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.util;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.geo.geodata.GeoStructure;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExServerPrimitive;

import java.awt.*;

/**
 * @author HorridoJoho
 */
public final class GeoUtils {
    public static void debug2DLine(Player player, int x, int y, int tx, int ty, int z) {
        final int gx = GeoEngine.getGeoX(x);
        final int gy = GeoEngine.getGeoY(y);

        final int tgx = GeoEngine.getGeoX(tx);
        final int tgy = GeoEngine.getGeoY(ty);

        final ExServerPrimitive prim = new ExServerPrimitive("Debug2DLine", x, y, z);
        prim.addLine(Color.BLUE, GeoEngine.getWorldX(gx), GeoEngine.getWorldY(gy), z, GeoEngine.getWorldX(tgx), GeoEngine.getWorldY(tgy), z);

        final LinePointIterator iter = new LinePointIterator(gx, gy, tgx, tgy);

        while (iter.next()) {
            final int wx = GeoEngine.getWorldX(iter.x());
            final int wy = GeoEngine.getWorldY(iter.y());

            prim.addPoint(Color.RED, wx, wy, z);
        }
        player.sendPacket(prim);
    }

    public static void debug3DLine(Player player, int x, int y, int z, int tx, int ty, int tz) {
        final int gx = GeoEngine.getGeoX(x);
        final int gy = GeoEngine.getGeoY(y);

        final int tgx = GeoEngine.getGeoX(tx);
        final int tgy = GeoEngine.getGeoY(ty);

        final ExServerPrimitive prim = new ExServerPrimitive("Debug3DLine", x, y, z);
        prim.addLine(Color.BLUE, GeoEngine.getWorldX(gx), GeoEngine.getWorldY(gy), z, GeoEngine.getWorldX(tgx), GeoEngine.getWorldY(tgy), tz);

        final LinePointIterator3D iter = new LinePointIterator3D(gx, gy, z, tgx, tgy, tz);
        iter.next();
        int prevX = iter.x();
        int prevY = iter.y();
        int wx = GeoEngine.getWorldX(prevX);
        int wy = GeoEngine.getWorldY(prevY);
        int wz = iter.z();
        prim.addPoint(Color.RED, wx, wy, wz);

        while (iter.next()) {
            final int curX = iter.x();
            final int curY = iter.y();

            if ((curX != prevX) || (curY != prevY)) {
                wx = GeoEngine.getWorldX(curX);
                wy = GeoEngine.getWorldY(curY);
                wz = iter.z();

                prim.addPoint(Color.RED, wx, wy, wz);

                prevX = curX;
                prevY = curY;
            }
        }
        player.sendPacket(prim);
    }

    private static Color getDirectionColor(int x, int y, int z, int nswe) {
        if ((GeoEngine.getInstance().getNsweNearest(x, y, z) & nswe) == nswe) {
            return Color.GREEN;
        }
        return Color.RED;
    }

    public static void debugGrid(Player player) {
        final int geoRadius = 20;
        final int blocksPerPacket = 40;

        int iBlock = blocksPerPacket;
        int iPacket = 0;

        ExServerPrimitive exsp = null;
        final int playerGx = GeoEngine.getGeoX(player.getX());
        final int playerGy = GeoEngine.getGeoY(player.getY());
        for (int dx = -geoRadius; dx <= geoRadius; ++dx) {
            for (int dy = -geoRadius; dy <= geoRadius; ++dy) {
                if (iBlock >= blocksPerPacket) {
                    iBlock = 0;
                    if (exsp != null) {
                        ++iPacket;
                        player.sendPacket(exsp);
                    }
                    exsp = new ExServerPrimitive("DebugGrid_" + iPacket, player.getX(), player.getY(), -16000);
                }

                if (exsp == null) {
                    throw new IllegalStateException();
                }

                final int gx = playerGx + dx;
                final int gy = playerGy + dy;

                final int x = GeoEngine.getWorldX(gx);
                final int y = GeoEngine.getWorldY(gy);
                final int z = GeoEngine.getInstance().getHeightNearest(gx, gy, player.getZ());

                // north arrow
                Color col = getDirectionColor(gx, gy, z, GeoStructure.CELL_FLAG_N);
                exsp.addLine(col, x - 1, y - 7, z, x + 1, y - 7, z);
                exsp.addLine(col, x - 2, y - 6, z, x + 2, y - 6, z);
                exsp.addLine(col, x - 3, y - 5, z, x + 3, y - 5, z);
                exsp.addLine(col, x - 4, y - 4, z, x + 4, y - 4, z);

                // east arrow
                col = getDirectionColor(gx, gy, z, GeoStructure.CELL_FLAG_E);
                exsp.addLine(col, x + 7, y - 1, z, x + 7, y + 1, z);
                exsp.addLine(col, x + 6, y - 2, z, x + 6, y + 2, z);
                exsp.addLine(col, x + 5, y - 3, z, x + 5, y + 3, z);
                exsp.addLine(col, x + 4, y - 4, z, x + 4, y + 4, z);

                // south arrow
                col = getDirectionColor(gx, gy, z, GeoStructure.CELL_FLAG_S);
                exsp.addLine(col, x - 1, y + 7, z, x + 1, y + 7, z);
                exsp.addLine(col, x - 2, y + 6, z, x + 2, y + 6, z);
                exsp.addLine(col, x - 3, y + 5, z, x + 3, y + 5, z);
                exsp.addLine(col, x - 4, y + 4, z, x + 4, y + 4, z);

                col = getDirectionColor(gx, gy, z, GeoStructure.CELL_FLAG_W);
                exsp.addLine(col, x - 7, y - 1, z, x - 7, y + 1, z);
                exsp.addLine(col, x - 6, y - 2, z, x - 6, y + 2, z);
                exsp.addLine(col, x - 5, y - 3, z, x - 5, y + 3, z);
                exsp.addLine(col, x - 4, y - 4, z, x - 4, y + 4, z);

                ++iBlock;
            }
        }

        player.sendPacket(exsp);
    }

    /**
     * difference between x values: never above 1<br>
     * difference between y values: never above 1
     *
     * @param lastX
     * @param lastY
     * @param x
     * @param y
     * @return
     */
    public static int computeNswe(int lastX, int lastY, int x, int y) {
        if (x > lastX) // east
        {
            if (y > lastY) {
                return GeoStructure.CELL_FLAG_SE; // Direction.SOUTH_EAST;
            } else if (y < lastY) {
                return GeoStructure.CELL_FLAG_NE; // Direction.NORTH_EAST;
            } else {
                return GeoStructure.CELL_FLAG_E; // Direction.EAST;
            }
        } else if (x < lastX) // west
        {
            if (y > lastY) {
                return GeoStructure.CELL_FLAG_SW; // Direction.SOUTH_WEST;
            } else if (y < lastY) {
                return GeoStructure.CELL_FLAG_NW; // Direction.NORTH_WEST;
            } else {
                return GeoStructure.CELL_FLAG_W; // Direction.WEST;
            }
        } else
        // unchanged x
        {
            if (y > lastY) {
                return GeoStructure.CELL_FLAG_S; // Direction.SOUTH;
            } else if (y < lastY) {
                return GeoStructure.CELL_FLAG_N; // Direction.NORTH;
            } else {
                throw new RuntimeException();
            }
        }
    }
}