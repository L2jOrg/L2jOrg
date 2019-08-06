package org.l2j.gameserver.world.zone.form;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.world.zone.ZoneForm;

import java.awt.*;

/**
 * A not so primitive npoly zone
 *
 * @author durgus
 */
public class ZonePolygonForm extends ZoneForm {
    private final Polygon polygon;
    private final int minZ;
    private final int maxZ;

    public ZonePolygonForm(int[] x, int[] y, int minZ, int maxZ) {
        polygon = new Polygon(x, y, x.length);

        this.minZ = Math.min(minZ, maxZ);
        this.maxZ = Math.max(minZ, maxZ);
    }

    @Override
    public boolean isInsideZone(int x, int y, int z) {
        return polygon.contains(x, y) &&  (z >= minZ) && (z <= maxZ);
    }

    @Override
    public boolean intersectsRectangle(int ax1, int ax2, int ay1, int ay2) {
        return polygon.intersects(Math.min(ax1, ax2), Math.min(ay1, ay2), Math.abs(ax2 - ax1), Math.abs(ay2 - ay1));
    }

    @Override
    public double getDistanceToZone(int x, int y) {
        final int[] _x = polygon.xpoints;
        final int[] _y = polygon.ypoints;
        double test;
        double shortestDist = Math.pow(_x[0] - x, 2) + Math.pow(_y[0] - y, 2);

        for (int i = 1; i < polygon.npoints; i++) {
            test = Math.pow(_x[i] - x, 2) + Math.pow(_y[i] - y, 2);
            if (test < shortestDist) {
                shortestDist = test;
            }
        }

        return Math.sqrt(shortestDist);
    }

    // getLowZ() / getHighZ() - These two functions were added to cope with the demand of the new fishing algorithms, wich are now able to correctly place the hook in the water, thanks to getHighZ(). getLowZ() was added, considering potential future modifications.
    @Override
    public int getLowZ() {
        return minZ;
    }

    @Override
    public int getHighZ() {
        return maxZ;
    }

    @Override
    public void visualizeZone(int z) {
        for (int i = 0; i < polygon.npoints; i++) {
            final int nextIndex = (i + 1) == polygon.xpoints.length ? 0 : i + 1;
            final int vx = polygon.xpoints[nextIndex] - polygon.xpoints[i];
            final int vy = polygon.ypoints[nextIndex] - polygon.ypoints[i];
            final float lenght = (float) Math.sqrt((vx * vx) + (vy * vy)) / STEP;
            for (int o = 1; o <= lenght; o++) {
                dropDebugItem(CommonItem.ADENA, 1, (int) (polygon.xpoints[i] + ((o / lenght) * vx)), (int) (polygon.ypoints[i] + ((o / lenght) * vy)), z);
            }
        }
    }

    @Override
    public Location getRandomPoint() {
        final int _minX = polygon.getBounds().x;
        final int _maxX = polygon.getBounds().x + polygon.getBounds().width;
        final int _minY = polygon.getBounds().y;
        final int _maxY = polygon.getBounds().y + polygon.getBounds().height;

        int x = Rnd.get(_minX, _maxX);
        int y = Rnd.get(_minY, _maxY);

        int antiBlocker = 0;
        while (!polygon.contains(x, y) && (antiBlocker++ < 1000)) {
            x = Rnd.get(_minX, _maxX);
            y = Rnd.get(_minY, _maxY);
        }

        return new Location(x, y, GeoEngine.getInstance().getHeight(x, y, minZ));
    }

    public int[] getX() {
        return polygon.xpoints;
    }

    public int[] getY() {
        return polygon.ypoints;
    }
}
